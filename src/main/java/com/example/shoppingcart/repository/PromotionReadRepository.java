package com.example.shoppingcart.repository;

import com.example.shoppingcart.model.dynamo.PromotionRule;
import java.util.List;

public interface PromotionReadRepository {
    List<PromotionRule> findProductAndHierarchyRules(String productId, List<String> hierarchyPaths);
}