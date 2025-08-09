package com.example.shoppingcart.service.discount;

import com.example.shoppingcart.model.dynamo.DiscountRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConflictResolver {

    /**
     * Resolves conflicts among a list of non-stackable rules.
     * It groups the rules by their exclusivity group and selects the one with the highest priority from each group.
     * @param nonStackableRules A list of applicable, non-stackable promotion rules.
     * @return A list containing only the single best rule from each exclusivity group.
     */
    public List<DiscountRule> resolveNonStackable(List<DiscountRule> nonStackableRules) {
        if (nonStackableRules == null || nonStackableRules.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, DiscountRule> bestRulesByGroup = nonStackableRules.stream()
                .collect(Collectors.toMap(
                        DiscountRule::getExclusivityGroup,
                        rule -> rule,
                        // If two rules are in the same group, the one with the higher priority wins.
                        (rule1, rule2) -> {
                            if (rule1.getPriority() == null) return rule2;
                            if (rule2.getPriority() == null) return rule1;
                            return rule1.getPriority() > rule2.getPriority() ? rule1 : rule2;
                        }
                ));

        return new ArrayList<>(bestRulesByGroup.values());
    }
}
