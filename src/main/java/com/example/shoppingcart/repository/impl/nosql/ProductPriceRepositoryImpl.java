package com.example.shoppingcart.repository.impl.nosql;

import com.example.shoppingcart.model.dynamo.ProductCatalogItem;
import com.example.shoppingcart.model.ProductPrice;
import com.example.shoppingcart.repository.ProductCatalogRepository;
import com.example.shoppingcart.repository.ProductPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * NoSQL implementation of the ProductPriceRepository that acts as a facade over the single-table ProductCatalogRepository.
 * It translates between the ProductPrice model and the persistence-focused ProductCatalogItem model.
 */
@Repository("productPriceRepository")
@RequiredArgsConstructor
public class ProductPriceRepositoryImpl implements ProductPriceRepository {

    private final ProductCatalogRepository productCatalogRepository;
    private static final String PRICE_SK_PREFIX = "PRICE#";

    public <S extends ProductPrice> S save(S price) {
        ProductCatalogItem item = toCatalogItem(price);
        productCatalogRepository.save(item);
        return price;
    }

    public Optional<ProductPrice> findPrice(String productId, String region, String currency) {
        String sk = PRICE_SK_PREFIX + region + "#" + currency;
        ProductCatalogItem item = productCatalogRepository.findByProductIdAndSk(productId, sk);
        return Optional.ofNullable(item).map(this::toProductPrice);
    }

    public void deleteById(String productId, String region, String currency) {
    findPrice(productId, region, currency).ifPresent(this::delete);
    }

    public void delete(ProductPrice price) {
        ProductCatalogItem item = toCatalogItem(price);
        productCatalogRepository.delete(item);
    }

    // --- DynamoDB helper methods (facade-only, not part of public contract) ---
    public DynamoDbEnhancedClient getEnhancedClient() {
        throw new UnsupportedOperationException("Not available from facade repository");
    }

    public Class<ProductCatalogItem> getEntityClass() {
        return ProductCatalogItem.class;
    }

    public TableSchema<ProductCatalogItem> getTableSchema() {
        return TableSchema.fromBean(ProductCatalogItem.class);
    }

    public DynamoDbTable<ProductCatalogItem> getTable() {
        throw new UnsupportedOperationException("Direct table access not available from facade repository");
    }

    // --- Helper Methods for Data Mapping ---

    private ProductPrice toProductPrice(ProductCatalogItem item) {
        ProductPrice price = new ProductPrice();
    price.setProductId(item.getProductId());
    price.setRegion(item.getRegion());
    price.setCurrency(item.getCurrency());
    price.setPriceInSmallestUnit(item.getPriceInSmallestUnit());
        return price;
    }

    private ProductCatalogItem toCatalogItem(ProductPrice price) {
        ProductCatalogItem item = new ProductCatalogItem();
    item.setProductId(price.getProductId());
    item.setSk(PRICE_SK_PREFIX + price.getRegion() + "#" + price.getCurrency());
    item.setRegion(price.getRegion());
    item.setCurrency(price.getCurrency());
    item.setPriceInSmallestUnit(price.getPriceInSmallestUnit());
        return item;
    }

    // --- Unsupported Operations from CrudRepository ---

    public Optional<ProductPrice> findById(String s) {
        throw new UnsupportedOperationException("Use findById(productId, region, currency)");
    }

    public boolean existsById(String s) {
        throw new UnsupportedOperationException("Use findById(productId, region, currency)");
    }

    public Iterable<ProductPrice> findAll() {
        throw new UnsupportedOperationException("findAll is not supported for prices. Query by product.");
    }

    public Iterable<ProductPrice> findAllById(Iterable<String> strings) {
        throw new UnsupportedOperationException("findAllById is not supported for prices.");
    }

    public long count() {
        throw new UnsupportedOperationException("count is not supported for prices.");
    }

    public void deleteById(String s) {
        throw new UnsupportedOperationException("Use deleteById(productId, region, currency)");
    }

    public void deleteAll(Iterable<? extends ProductPrice> entities) {
        entities.forEach(this::delete);
    }

    public void deleteAll() {
        throw new UnsupportedOperationException("deleteAll is not supported for prices.");
    }
}
