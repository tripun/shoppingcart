package com.example.shoppingcart.repository.impl.dynamo;

import com.example.shoppingcart.model.dynamo.PromotionRule;
import com.example.shoppingcart.repository.PromotionReadRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PromotionReadRepositoryImpl implements PromotionReadRepository {

    private final DynamoDbTable<PromotionRule> promotionTable;
    private final DynamoDbIndex<PromotionRule> productIndex;

    public PromotionReadRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.promotionTable = enhancedClient.table("Promotions", TableSchema.fromBean(PromotionRule.class));
        this.productIndex = promotionTable.index("Product-Index");
    }

    @Override
    public List<PromotionRule> findProductAndHierarchyRules(String productId, List<String> hierarchyPaths) {
        // This is a simplified implementation. A real implementation would query for the product and all its parent categories.
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue("PRODUCT#" + productId).build());
        return productIndex.query(query).stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }
}
