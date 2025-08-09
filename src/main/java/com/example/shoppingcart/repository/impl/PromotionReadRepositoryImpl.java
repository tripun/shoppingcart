package com.example.shoppingcart.repository.impl;

import com.example.shoppingcart.model.dynamo.PromotionRule;
import com.example.shoppingcart.repository.PromotionReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Repository
public class PromotionReadRepositoryImpl implements PromotionReadRepository {

    private final DynamoDbTable<PromotionRule> promotionTable;
    private final DynamoDbIndex<PromotionRule> gsi1;
    private final DynamoDbIndex<PromotionRule> gsi2;

    @Autowired
    public PromotionReadRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.promotionTable = enhancedClient.table("Promotions", TableSchema.fromBean(PromotionRule.class));
        this.gsi1 = promotionTable.index("GSI1");
        this.gsi2 = promotionTable.index("GSI2");
    }

    // This implementation demonstrates the parallel query pattern.
    @Override
    public List<PromotionRule> findProductAndHierarchyRules(String productId, List<String> hierarchyPaths) {
        List<CompletableFuture<List<PromotionRule>>> futures = new ArrayList<>();

        // Query for product-specific rules across all hierarchy paths
        for (String path : hierarchyPaths) {
            futures.add(CompletableFuture.supplyAsync(() -> queryGsi2(productId, path)));
        }

        // Query for broad rules across all hierarchy paths
        for (String path : hierarchyPaths) {
            futures.add(CompletableFuture.supplyAsync(() -> queryGsi1(path)));
        }

        // Wait for all queries to complete and flatten the results
        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .distinct() // Ensure rules are not duplicated if they match multiple paths
                .collect(Collectors.toList());
    }

    private List<PromotionRule> queryGsi2(String productId, String hierarchyPath) {
        QueryConditional qc = QueryConditional.keyEqualTo(k -> k.partitionValue("PRODUCT_SCOPE#" + productId)
                .sortValue(hierarchyPath).build());
        // SAFE IMPLEMENTATION: Use streams to handle paginated results gracefully, including empty results.
        return gsi2.query(qc).stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    private List<PromotionRule> queryGsi1(String hierarchyPath) {
        QueryConditional qc = QueryConditional.keyEqualTo(k -> k.partitionValue(hierarchyPath));
        // SAFE IMPLEMENTATION: Use streams to handle paginated results gracefully.
        return gsi1.query(qc).stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }
}
