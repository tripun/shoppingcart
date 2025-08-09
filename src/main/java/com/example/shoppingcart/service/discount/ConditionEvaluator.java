package com.example.shoppingcart.service.discount;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ConditionEvaluator {

    private static final Logger log = LoggerFactory.getLogger(ConditionEvaluator.class);

    private final Map<ConditionType, ConditionStrategy> strategyMap;

    @Autowired
    public ConditionEvaluator(List<ConditionStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(ConditionStrategy::getType, Function.identity()));
    }

    public boolean areConditionsMet(DiscountRule rule, EvaluationContext context) {
        if (rule.getConditions() == null || rule.getConditions().isEmpty()) {
            return true; // No conditions means the rule is always applicable
        }
        for (DiscountRule.Condition condition : rule.getConditions()) {
            ConditionType type = ConditionType.fromString(condition.getType());
            if (type == null) {
                log.warn("Unknown condition type found: {}", condition.getType());
                return false;
            }

            ConditionStrategy strategy = strategyMap.get(type);
            if (strategy == null) {
                log.warn("No strategy implementation found for condition type: {}", type);
                return false;
            }

            if (!strategy.evaluate(condition, context)) {
                return false;
            }
        }
        return true;
    }
}
