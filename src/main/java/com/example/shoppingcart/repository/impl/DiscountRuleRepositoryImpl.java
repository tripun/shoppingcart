package com.example.shoppingcart.repository.impl;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.repository.DiscountRuleRepository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class DiscountRuleRepositoryImpl implements DiscountRuleRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<DiscountRule> discountRuleTable;

    public DiscountRuleRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.discountRuleTable = dynamoDbEnhancedClient.table("DiscountRule", TableSchema.fromBean(DiscountRule.class));
    }

    // --- Implement DynamoDBCrudRepository contract ---
    public DynamoDbEnhancedClient getEnhancedClient() {
        return this.dynamoDbEnhancedClient;
    }

    public Class<DiscountRule> getEntityClass() {
        return DiscountRule.class;
    }

    public TableSchema<DiscountRule> getTableSchema() {
        return TableSchema.fromBean(DiscountRule.class);
    }

    public DynamoDbTable<DiscountRule> getTable() {
        return this.discountRuleTable;
    }

    @Override
    public DiscountRule save(DiscountRule rule) {
        discountRuleTable.putItem(rule);
        return rule;
    }

    @Override
    public Optional<DiscountRule> findById(String ruleId) {
        return Optional.ofNullable(discountRuleTable.getItem(Key.builder().partitionValue(ruleId).build()));
    }

    public boolean existsById(String ruleId) {
        return findById(ruleId).isPresent();
    }

    @Override
    public void deleteById(String ruleId) {
        discountRuleTable.deleteItem(Key.builder().partitionValue(ruleId).build());
    }

    public void delete(DiscountRule entity) {
        deleteById(entity.getRuleId());
    }

    public void deleteAll(Iterable<? extends DiscountRule> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public Iterable<DiscountRule> findAll() {
        throw new UnsupportedOperationException("findAll is disabled to prevent full table scans.");
    }

    public Iterable<DiscountRule> findAllById(Iterable<String> strings) {
        return StreamSupport.stream(strings.spliterator(), false)
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public long count() {
        throw new UnsupportedOperationException("count is disabled to prevent full table scans.");
    }

    public void deleteAll() {
        throw new UnsupportedOperationException("deleteAll is disabled to prevent full table scans.");
    }

    // Removed duplicate non-generic methods to avoid name-clash with generic interface methods
    // For list retrieval we implement the custom methods defined on the DiscountRuleRepository interface

    // Provide a typed helper for callers that expect a List, but don't mark it as @Override to avoid name-clash
    public List<DiscountRule> findAllAsList() {
        return discountRuleTable.scan().items().stream().collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<DiscountRule> discountRules) {
        // Batch write operation for saving multiple rules
        // This is a simplified example, for production consider batchWriteItem
        discountRules.forEach(this::save);
    }

    @Override
    public List<DiscountRule> findActiveAndOrderedRules() {
        // This will require querying a GSI (Global Secondary Index) on 'activeStatus' and 'priority'
        // Assuming 'activeStatus' is set to a specific value for active rules (e.g., "ACTIVE")
        // and 'priority' is the sort key for that GSI.
        // For now, a basic scan and filter, but this should be optimized with a GSI query.
        // Fall back to a scan + in-memory filter when expression builders are not available.
        return discountRuleTable.scan().stream()
                .flatMap(page -> page.items().stream())
                .filter(DiscountRule::isActive)
                .sorted((r1, r2) -> {
                    Integer p1 = r1.getPriority();
                    Integer p2 = r2.getPriority();
                    if (p1 == null && p2 == null) return 0;
                    if (p1 == null) return 1;
                    if (p2 == null) return -1;
                    return p1.compareTo(p2);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DiscountRule> findActiveRulesByProductAndHierarchy(String productId, List<String> categoryHierarchy) {
        // This is a complex query that will likely require a scan and in-memory filtering
        // unless specific GSIs are designed for product/category based conditions.
        // For now, a basic scan and filter.
        return discountRuleTable.scan().items().stream()
                .filter(rule -> rule.isActive() &&
                        rule.getConditions() != null &&
                        rule.getConditions().stream().anyMatch(condition ->
                                ("PRODUCT".equals(condition.getType()) && productId.equals(condition.getProductId())) ||
                                ("CATEGORY".equals(condition.getType()) && categoryHierarchy.stream().anyMatch(cat -> cat.startsWith(condition.getTag())))
                        )
                )
                .collect(Collectors.toList());
    }
}
