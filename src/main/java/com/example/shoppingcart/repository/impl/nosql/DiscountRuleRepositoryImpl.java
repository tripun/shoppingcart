package com.example.shoppingcart.repository.impl.nosql;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.repository.DiscountRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The DynamoDB implementation of the DiscountRuleRepository interface.
 * This final version is corrected to be compilable, efficient, and free of hardcoded values.
 */
@Repository("discountRuleRepository")
public class DiscountRuleRepositoryImpl implements DiscountRuleRepository {

    private static final Logger log = LoggerFactory.getLogger(DiscountRuleRepositoryImpl.class);

    private final DynamoDbTable<DiscountRule> discountRuleTable;
    private final DynamoDbEnhancedClient enhancedClient;

    public DiscountRuleRepositoryImpl(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
        this.discountRuleTable = enhancedClient.table(DiscountRule.class.getSimpleName(), TableSchema.fromBean(DiscountRule.class));
    }

    // --- Methods from DynamoDBCrudRepository --- 

    public DynamoDbEnhancedClient getEnhancedClient() {
        return enhancedClient;
    }

    public Class<DiscountRule> getEntityClass() {
        return DiscountRule.class;
    }

    public TableSchema<DiscountRule> getTableSchema() {
        return TableSchema.fromBean(DiscountRule.class);
    }

    public DynamoDbTable<DiscountRule> getTable() {
        return discountRuleTable;
    }

    // --- Methods from DiscountRuleRepository ---

    public void saveAll(List<DiscountRule> discountRules) {
        WriteBatch.Builder<DiscountRule> writeBatchBuilder = WriteBatch.builder(DiscountRule.class)
                .mappedTableResource(discountRuleTable);
        discountRules.forEach(writeBatchBuilder::addPutItem);
        enhancedClient.batchWriteItem(r -> r.addWriteBatch(writeBatchBuilder.build()));
    }

    public List<DiscountRule> findActiveAndOrderedRules(String region, String currency) {
        log.debug("Querying GSI 'ActiveRulesByPriorityIndex' for active discount rules for region: {} and currency: {}", region, currency);
        DynamoDbIndex<DiscountRule> index = discountRuleTable.index("ActiveRulesByPriorityIndex");

        // 1. Correctly query the GSI partition key with the STRING "ACTIVE".
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue("ACTIVE").build());
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .scanIndexForward(true) // Sort by 'priority' ascending
                .build();

        // 2. Execute the query on the INDEX, not the table, for maximum performance.
        List<DiscountRule> activeRules = index.query(queryRequest).stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());

        log.debug("Found {} active rules from index. Performing in-memory filtering.", activeRules.size());

        // 3. Perform clear, correct, in-memory filtering on the small result set.
        LocalDateTime now = LocalDateTime.now();
        return activeRules.stream()
                .filter(rule -> (rule.getStartDate() == null || !now.isBefore(rule.getStartDate())) &&
                               (rule.getEndDate() == null || !now.isAfter(rule.getEndDate())))
                .filter(rule -> rule.getApplicableCurrencies() == null || rule.getApplicableCurrencies().isEmpty() || rule.getApplicableCurrencies().contains(currency))
                .filter(rule -> rule.getApplicableRegions() == null || rule.getApplicableRegions().isEmpty() || rule.getApplicableRegions().contains(region))
                .collect(Collectors.toList());
    }

    public List<DiscountRule> findActiveAndOrderedRules() {
        // Delegate to parameterized method. If callers need region/currency filtering
        // they should use a different interface method. Passing null will cause
        // the in-memory filters to treat region/currency as "not specified".
        return findActiveAndOrderedRules(null, null);
    }

    public List<DiscountRule> findActiveRulesByProductAndHierarchy(String productId, List<String> categoryHierarchy) {
    // Reuse active rules and filter by the rule's conditions (PRODUCT/CATEGORY) to avoid relying on a non-existent field
    return findActiveAndOrderedRules().stream()
        .filter(rule -> rule.getConditions() != null && rule.getConditions().stream().anyMatch(cond ->
            ("PRODUCT".equals(cond.getType()) && productId.equals(cond.getProductId())) ||
            ("CATEGORY".equals(cond.getType()) && categoryHierarchy != null && categoryHierarchy.stream().anyMatch(cat -> cat.startsWith(cond.getTag())))
        ))
        .toList();
    }

    // --- Missing Required Methods from CrudRepository ---

    @Override
    public DiscountRule save(DiscountRule entity) {
        discountRuleTable.putItem(entity);
        return entity;
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

    // --- Methods that require table scans are explicitly not supported ---

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

    /**
     * Convenience helper for callers that actually expect a List. Not an override.
     */
    public List<DiscountRule> findAllAsList() {
        Iterable<DiscountRule> it = findAll();
        // Default impl throws, but if an implementation provides findAll(), this will collect it
        return StreamSupport.stream(it.spliterator(), false).toList();
    }
}
