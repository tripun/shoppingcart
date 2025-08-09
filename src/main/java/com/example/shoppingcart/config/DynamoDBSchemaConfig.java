package com.example.shoppingcart.config;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.model.dynamo.ProductCatalogItem;
import com.example.shoppingcart.model.dynamo.ShoppingCart;
import com.example.shoppingcart.model.dynamo.User;
import com.example.shoppingcart.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

/**
 * DynamoDB Schema initialization for Shopping Cart tables.
 * Creates tables, indexes, and sample data for local development.
 */
@Configuration
public class DynamoDBSchemaConfig {

    private static final Logger log = LoggerFactory.getLogger(DynamoDBSchemaConfig.class);

    @Value("${app.dynamodb.create-tables:true}")
    private boolean createTables;

    @Bean("initializeDynamoDBSchema")
    @Profile({"dev", "test", "local"})
    public CommandLineRunner initializeDynamoDBSchema(
            DynamoDbEnhancedClient enhancedClient,
            UserService userService) {

        return args -> {
            if (!createTables) {
                log.info("Table creation disabled, skipping DynamoDB schema initialization");
                return;
            }

            log.info("Initializing DynamoDB schema for Shopping Cart application");

            try {
                createUserTable(enhancedClient);
                createShoppingCartTable(enhancedClient);
                createProductCatalogTable(enhancedClient);
                createDiscountRuleTable(enhancedClient);
                // Other table creation methods can be called here

                log.info("DynamoDB schema initialization completed successfully");

                log.info("Creating default admin user...");
                userService.createDefaultAdminIfNotExists();
                log.info("Default admin user setup complete.");

            } catch (Exception e) {
                log.error("Failed to initialize DynamoDB schema or default data: {}", e.getMessage(), e);
                throw new RuntimeException("DynamoDB initialization failed", e);
            }
        };
    }

    private void createUserTable(DynamoDbEnhancedClient enhancedClient) {
        try {
            DynamoDbTable<User> table = enhancedClient.table(User.class.getSimpleName(), TableSchema.fromBean(User.class));
            table.createTable();
            log.info("Created DynamoDB table: User");
        } catch (ResourceInUseException e) {
            log.info("DynamoDB table User already exists");
        }
    }

    private void createShoppingCartTable(DynamoDbEnhancedClient enhancedClient) {
        try {
            DynamoDbTable<ShoppingCart> table = enhancedClient.table(ShoppingCart.class.getSimpleName(), TableSchema.fromBean(ShoppingCart.class));
            EnhancedGlobalSecondaryIndex userCartIndex = EnhancedGlobalSecondaryIndex.builder()
                    .indexName("UserCartIndex")
                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                    .build();
            EnhancedGlobalSecondaryIndex sessionIndex = EnhancedGlobalSecondaryIndex.builder()
                    .indexName("SessionIndex")
                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                    .build();
            CreateTableEnhancedRequest request = CreateTableEnhancedRequest.builder()
                    .globalSecondaryIndices(userCartIndex, sessionIndex)
                    .build();
            table.createTable(request);
            log.info("Created DynamoDB table: ShoppingCart with required indexes.");
        } catch (ResourceInUseException e) {
            log.info("DynamoDB table ShoppingCart already exists.");
        }
    }

    private void createProductCatalogTable(DynamoDbEnhancedClient enhancedClient) {
        try {
            DynamoDbTable<ProductCatalogItem> table = enhancedClient.table("ProductCatalog", TableSchema.fromBean(ProductCatalogItem.class));
            EnhancedGlobalSecondaryIndex categoryIndex = EnhancedGlobalSecondaryIndex.builder()
                    .indexName("CategoryIndex")
                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                    .build();
            CreateTableEnhancedRequest request = CreateTableEnhancedRequest.builder()
                    .globalSecondaryIndices(categoryIndex)
                    .build();
            table.createTable(request);
            log.info("Created DynamoDB table: ProductCatalog");
        } catch (ResourceInUseException e) {
            log.info("DynamoDB table ProductCatalog already exists");
        }
    }

    private void createDiscountRuleTable(DynamoDbEnhancedClient enhancedClient) {
        try {
            DynamoDbTable<DiscountRule> table = enhancedClient.table(DiscountRule.class.getSimpleName(), TableSchema.fromBean(DiscountRule.class));
            EnhancedGlobalSecondaryIndex activeRulesIndex = EnhancedGlobalSecondaryIndex.builder()
                    .indexName("ActiveRulesByPriorityIndex")
                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                    .build();
            EnhancedGlobalSecondaryIndex userDiscountIndex = EnhancedGlobalSecondaryIndex.builder()
                    .indexName("UserDiscountIndex")
                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                    .build();
            EnhancedGlobalSecondaryIndex conditionIndex = EnhancedGlobalSecondaryIndex.builder()
                    .indexName("ConditionIndex")
                    .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                    .build();
            CreateTableEnhancedRequest request = CreateTableEnhancedRequest.builder()
                    .globalSecondaryIndices(activeRulesIndex, userDiscountIndex, conditionIndex)
                    .build();
            table.createTable(request);
            log.info("Created DynamoDB table: DiscountRule with all required indexes.");
        } catch (ResourceInUseException e) {
            log.info("DynamoDB table DiscountRule already exists.");
        }
    }
}
