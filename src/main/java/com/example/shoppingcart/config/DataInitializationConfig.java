package com.example.shoppingcart.config;

import com.example.shoppingcart.model.dynamo.CatalogItem;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializationConfig {

    private static final Logger log = LoggerFactory.getLogger(DataInitializationConfig.class);

    @Bean
    @Profile({"dev", "test", "default"})
    @DependsOn("initializeDynamoDBSchema")
    public CommandLineRunner initializeData(DynamoDbEnhancedClient enhancedClient) {

        return args -> {
            log.info("Initializing sample data for shopping cart application");

            DynamoDbTable<CatalogItem> catalogTable = enhancedClient.table("Catalog", TableSchema.fromBean(CatalogItem.class));
            DynamoDbTable<DiscountRule> promotionTable = enhancedClient.table("Promotions", TableSchema.fromBean(DiscountRule.class));

            initializeProducts(catalogTable);
            initializeDiscountRules(promotionTable);

            log.info("Sample data initialization completed");
        };
    }

    private void initializeProducts(DynamoDbTable<CatalogItem> catalogTable) {
        log.info("Creating sample products using single-table design");

        List<CatalogItem> itemsToSave = new ArrayList<>();

        itemsToSave.add(createProductItem("APPLE", "Apple", "FRUITS", 35, "UK"));
        itemsToSave.add(createProductItem("BANANA", "Banana", "FRUITS", 20, "UK"));
        itemsToSave.add(createProductItem("MELON", "Melon", "FRUITS", 50, "UK"));
    itemsToSave.add(createProductItem("LIME", "Lime", "FRUITS", 15, "UK"));

        for (CatalogItem item : itemsToSave) {
            catalogTable.putItem(item);
        }

        log.info("Created 3 sample products");
    }

    private CatalogItem createProductItem(String productId, String name, String category, int price, String region) {
        CatalogItem item = new CatalogItem();
        item.setPk("PRODUCT#" + productId);
        item.setSk("REGION#" + region);
        item.setName(name);
        item.setCategoryHierarchy("/" + category + "/");
        item.setPrice(price);
        item.setCurrency("GBP");
        return item;
    }

    private void initializeDiscountRules(DynamoDbTable<DiscountRule> promotionTable) {
        log.info("Creating sample discount rules");
    DiscountRule melonOffer = new DiscountRule();
    // Use unified DiscountRule model
    melonOffer.setRuleId("PROMOTION#MELON_BOGO");
    melonOffer.setRuleName("MELON_BOGO");
    melonOffer.setDescription("Buy one get one free for Melons");
    melonOffer.setIsStackable(false);

    DiscountRule.Condition melonCondition = new DiscountRule.Condition();
    melonCondition.setType("ITEM_QUANTITY");
    melonCondition.setProductId("MELON");
    melonCondition.setQuantity(2);
    melonOffer.setConditions(List.of(melonCondition));

    DiscountRule.Action melonAction = new DiscountRule.Action();
    // Use the canonical action type expected by the strategy
    melonAction.setType("BUY_X_GET_Y_FREE");
    melonAction.setProductId("MELON");
    melonAction.setBuyQuantity(1);
    melonAction.setGetQuantity(1);
    melonOffer.setActions(List.of(melonAction));

    promotionTable.putItem(melonOffer);

    // Add three-for-two offer for LIME
    DiscountRule limeOffer = new DiscountRule();
    limeOffer.setRuleId("PROMOTION#LIME_3_FOR_2");
    limeOffer.setRuleName("LIME_3_FOR_2");
    limeOffer.setDescription("Three for the price of two for Limes");
    limeOffer.setIsStackable(false);

    DiscountRule.Condition limeCondition = new DiscountRule.Condition();
    limeCondition.setType("ITEM_QUANTITY");
    limeCondition.setProductId("LIME");
    limeCondition.setQuantity(3);
    limeOffer.setConditions(List.of(limeCondition));

    DiscountRule.Action limeAction = new DiscountRule.Action();
    // Represent "3 for 2" as buy 2 get 1 free using the BUY_X_GET_Y_FREE strategy
    limeAction.setType("BUY_X_GET_Y_FREE");
    limeAction.setProductId("LIME");
    limeAction.setBuyQuantity(2);
    limeAction.setGetQuantity(1);
    limeOffer.setActions(List.of(limeAction));

    promotionTable.putItem(limeOffer);

    log.info("Created 1 sample discount rule");
    }
}