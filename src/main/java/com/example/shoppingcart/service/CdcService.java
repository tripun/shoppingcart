package com.example.shoppingcart.service;

import com.example.shoppingcart.model.postgres.Category;
import com.example.shoppingcart.model.postgres.DiscountRule;
import com.example.shoppingcart.model.postgres.Inventory;
import com.example.shoppingcart.model.postgres.Product;
import com.example.shoppingcart.model.postgres.ProductPrice;
import com.example.shoppingcart.model.postgres.User;

import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.dynamo.InventoryRecord;
import com.example.shoppingcart.model.dynamo.PromotionRule; // This is not used for writing, only reading
import com.example.shoppingcart.model.dynamo.ProductCatalogItem; // Corrected import

import com.example.shoppingcart.repository.ProductCatalogRepository;
import com.example.shoppingcart.repository.UserRepository; // DynamoDB User Repository
import com.example.shoppingcart.repository.DiscountRuleRepository; // DynamoDB DiscountRule Repository
import com.example.shoppingcart.repository.CatalogReadRepository; // Read-only, not for writing
import com.example.shoppingcart.repository.PromotionReadRepository; // Read-only, not for writing
import com.example.shoppingcart.repository.jpa.DiscountActionRepository;
import com.example.shoppingcart.repository.jpa.DiscountConditionRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CdcService {

    private static final Logger log = LoggerFactory.getLogger(CdcService.class);

    private final ProductCatalogRepository productCatalogRepository; // For writing ProductCatalogItem (includes product, price, inventory)
    private final UserRepository userRepository; // For writing DynamoDB User
    private final DiscountRuleRepository discountRuleRepository; // For writing DynamoDB DiscountRule
    private final DiscountConditionRepository discountConditionRepository;
    private final DiscountActionRepository discountActionRepository;

    // Method to sync Product (Postgres) to DynamoDB (ProductCatalogItem)
    public void syncProductToDynamoDB(Product postgresProduct) {
        log.info("CDC: Syncing Product {} to DynamoDB", postgresProduct.getId());
        ProductCatalogItem item = new ProductCatalogItem();
        item.setProductId(postgresProduct.getId()); // Use getId() for product_id
        item.setSk("METADATA"); // Standard SK for product metadata
        item.setName(postgresProduct.getName());
        item.setDescription(postgresProduct.getDescription());
        // Assuming categoryId is mapped to category name in ProductDto.ProductCategory
        // For now, just use the ID. A more robust solution would involve looking up the category name.
        item.setCategory(String.valueOf(postgresProduct.getCategoryId()));
        item.setStatus(postgresProduct.getStatus());
        item.setImageUrl(postgresProduct.getImageUrl());
        // Price, currency, stock, region are in separate tables in Postgres, will be synced by other methods
        productCatalogRepository.save(item);
    }

    // Method to sync Inventory (Postgres) to DynamoDB (ProductCatalogItem with INVENTORY SK)
    public void syncInventoryToDynamoDB(Inventory postgresInventory) {
        log.info("CDC: Syncing Inventory for product {} region {} to DynamoDB", postgresInventory.getProductId(), postgresInventory.getRegion());
        ProductCatalogItem item = new ProductCatalogItem();
        item.setProductId(postgresInventory.getProductId());
        item.setSk("INVENTORY#" + postgresInventory.getRegion()); // SK for inventory record
        item.setStock(postgresInventory.getQuantity());
        item.setRegion(postgresInventory.getRegion());
        // Other product details are in the METADATA item, not duplicated here
        productCatalogRepository.save(item);
    }

    // Method to sync ProductPrice (Postgres) to DynamoDB (ProductCatalogItem with PRICE SK)
    public void syncProductPriceToDynamoDB(ProductPrice postgresProductPrice) {
        log.info("CDC: Syncing ProductPrice for product {} region {} currency {} to DynamoDB",
                postgresProductPrice.getProductId(), postgresProductPrice.getRegion(), postgresProductPrice.getCurrency());
        ProductCatalogItem item = new ProductCatalogItem();
        item.setProductId(postgresProductPrice.getProductId());
    item.setSk("PRICE#" + postgresProductPrice.getRegion() + "#" + postgresProductPrice.getCurrency()); // SK for price record
    item.setPriceInSmallestUnit(postgresProductPrice.getPriceInSmallestUnit());
        item.setCurrency(postgresProductPrice.getCurrency());
        item.setRegion(postgresProductPrice.getRegion());
        // Other product details are in the METADATA item, not duplicated here
        productCatalogRepository.save(item);
    }

    // Method to sync DiscountRule (Postgres) to DynamoDB (DiscountRule)
    public void syncDiscountRuleToDynamoDB(DiscountRule postgresDiscountRule) {
        log.info("CDC: Syncing DiscountRule {} to DynamoDB", postgresDiscountRule.getId());
        com.example.shoppingcart.model.dynamo.DiscountRule dynamoDbDiscountRule = new com.example.shoppingcart.model.dynamo.DiscountRule();
        dynamoDbDiscountRule.setRuleId(String.valueOf(postgresDiscountRule.getId()));
        dynamoDbDiscountRule.setRuleName(postgresDiscountRule.getName());
        dynamoDbDiscountRule.setDescription(postgresDiscountRule.getDescription());
        dynamoDbDiscountRule.setActive(postgresDiscountRule.isActive());
        dynamoDbDiscountRule.setStartDate(postgresDiscountRule.getValidFromUtc());
        dynamoDbDiscountRule.setEndDate(postgresDiscountRule.getValidUntilUtc());
        dynamoDbDiscountRule.setPriority(postgresDiscountRule.getPriority());
        // Map conditions from Postgres discount_conditions table
    List<com.example.shoppingcart.model.postgres.DiscountCondition> conditions =
        discountConditionRepository.findAll().stream()
            .filter(c -> c.getRuleId() != null && c.getRuleId().equals(postgresDiscountRule.getId()))
            .collect(Collectors.toList());
        if (!conditions.isEmpty()) {
            List<com.example.shoppingcart.model.dynamo.DiscountRule.Condition> dynamoConditions = new ArrayList<>();
            for (com.example.shoppingcart.model.postgres.DiscountCondition pc : conditions) {
                com.example.shoppingcart.model.dynamo.DiscountRule.Condition dc = new com.example.shoppingcart.model.dynamo.DiscountRule.Condition();
                dc.setType(pc.getConditionType());
                dc.setProductId(pc.getProductId());
                dc.setQuantity(pc.getQuantity());
                // map amount/values to integer fields expected by Dynamo model
                dc.setValue(pc.getAmountInSmallestUnit());
                dynamoConditions.add(dc);
            }
            dynamoDbDiscountRule.setConditions(dynamoConditions);
        }

        // Map actions from Postgres discount_actions table
    List<com.example.shoppingcart.model.postgres.DiscountAction> actions =
        discountActionRepository.findAll().stream()
            .filter(a -> a.getRuleId() != null && a.getRuleId().equals(postgresDiscountRule.getId()))
            .collect(Collectors.toList());
        if (!actions.isEmpty()) {
            List<com.example.shoppingcart.model.dynamo.DiscountRule.Action> dynamoActions = new ArrayList<>();
            for (com.example.shoppingcart.model.postgres.DiscountAction pa : actions) {
                com.example.shoppingcart.model.dynamo.DiscountRule.Action da = new com.example.shoppingcart.model.dynamo.DiscountRule.Action();
                da.setType(pa.getActionType());
                da.setProductId(pa.getTargetProductId());
                da.setBuyQuantity(pa.getGetQuantity());
                if (pa.getValue() != null) {
                    da.setValue(pa.getValue().intValue());
                }
                dynamoActions.add(da);
            }
            dynamoDbDiscountRule.setActions(dynamoActions);
        }
        discountRuleRepository.save(dynamoDbDiscountRule);
    }

    // Method to sync User (Postgres) to DynamoDB (User)
    public void syncUserToDynamoDB(User postgresUser) {
        log.info("CDC: Syncing User {} to DynamoDB", postgresUser.getUsername());
        com.example.shoppingcart.model.dynamo.User dynamoDbUser = new com.example.shoppingcart.model.dynamo.User();
        dynamoDbUser.setUsername(postgresUser.getUsername());
        dynamoDbUser.setPassword(postgresUser.getPasswordHash()); // Storing hashed password in DynamoDB for consistency
        dynamoDbUser.setRole(postgresUser.getRole());
        dynamoDbUser.setBaseCurrency(postgresUser.getUserCurrency());
        dynamoDbUser.setEnabled(postgresUser.isActive());
        userRepository.save(dynamoDbUser);
    }

    // Method to sync Category (Postgres) to DynamoDB (embedded in ProductCatalogItem)
    public void syncCategoryToDynamoDB(Category postgresCategory) {
        log.info("CDC: Syncing Category {} to DynamoDB (indirectly via products)", postgresCategory.getName());
        // Categories are typically embedded in product data in DynamoDB single-table design.
        // A direct sync of Category might not be necessary or might involve re-synching all products in that category.
        // For now, this method is a placeholder, as direct Category entity in DynamoDB is not present.
        // If a category name changes, all products associated with that category would need to be re-synced.
    }
}