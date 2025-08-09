package com.example.shoppingcart.service.discount;

import com.example.shoppingcart.dto.AppliedDiscountDto;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ActionApplier {

    private static final Logger log = LoggerFactory.getLogger(ActionApplier.class);

    private final Map<ActionType, ActionStrategy> strategyMap;

    @Autowired
    public ActionApplier(List<ActionStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(ActionStrategy::getType, Function.identity()));
    }

    public List<AppliedDiscountDto> apply(List<DiscountRule> finalRules, EvaluationContext context) {
        return finalRules.stream()
                .flatMap(rule -> rule.getActions() == null ? Stream.empty() : rule.getActions().stream())
                .map(action -> {
                    ActionType type = ActionType.fromString(action.getType());
                    if (type == null) {
                        log.warn("Unknown action type found: {}", action.getType());
                        return null;
                    }

                    ActionStrategy strategy = strategyMap.get(type);
                    if (strategy == null) {
                        log.warn("No strategy implementation found for action type: {}", type);
                        return null;
                    }
                    return strategy.apply(action, context);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
