package com.example.shoppingcart.service.impl.nosql;

import com.example.shoppingcart.dto.DiscountRuleDto;
import com.example.shoppingcart.model.dynamo.DiscountRule; // Corrected import
import com.example.shoppingcart.model.dynamo.ShoppingCart; // Corrected import
import com.example.shoppingcart.repository.DiscountRuleRepository;
import com.example.shoppingcart.service.DiscountService;
import com.example.shoppingcart.service.discount.DiscountStrategy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private static final Logger log = LoggerFactory.getLogger(DiscountServiceImpl.class);
    private final DiscountRuleRepository discountRuleRepository;
    private final Map<String, DiscountStrategy> discountStrategies; // Injects all DiscountStrategy beans

    @Override
    public BigDecimal calculateTotalDiscount(ShoppingCart cart) {
        List<DiscountRule> activeRules = getActiveRules();
        log.debug("Found {} active discount rules.", activeRules.size());

        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (DiscountRule rule : activeRules) {
            DiscountStrategy strategy = discountStrategies.get(rule.getStrategyBeanName());
            if (strategy != null) {
                try {
                    BigDecimal discountAmount = strategy.apply(cart, rule);
                    if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                        log.info("Applied discount \"{}\"", rule.getRuleName());
                        totalDiscount = totalDiscount.add(discountAmount);
                    } 
                } catch (Exception e) {
                    log.error("Error applying discount rule \"{}\"", rule.getRuleName(), e);
                }
            }
        }
        return totalDiscount;
    }

    private List<DiscountRule> getActiveRules() {
    // Use repository method (no args) and rely on repository to filter by defaults/GSI
    return discountRuleRepository.findActiveAndOrderedRules();
    }

    @Override
    public DiscountRule create(DiscountRuleDto dto) {
        DiscountRule discountRule = new DiscountRule();
        discountRule.setRuleId(dto.getRuleId() != null ? dto.getRuleId() : UUID.randomUUID().toString());
        discountRule.setRuleName(dto.getRuleName());
        if (dto.getDiscountType() != null) {
            try {
                discountRule.setDiscountType(DiscountRule.DiscountType.valueOf(dto.getDiscountType().name()));
            } catch (Exception ignored) {}
        }
        discountRule.setDiscountValue(dto.getDiscountValue());
        if (dto.getApplicableTo() != null) {
            try {
                discountRule.setApplicableTo(DiscountRule.ApplicableTo.valueOf(dto.getApplicableTo().name()));
            } catch (Exception ignored) {}
        }
        discountRule.setApplicableValue(dto.getApplicableValue());
        discountRule.setStartDate(dto.getStartDate());
        discountRule.setEndDate(dto.getEndDate());
        discountRule.setActive(dto.isActive());
        discountRule.setBuyQuantity(dto.getBuyQuantity());
        discountRule.setFreeQuantity(dto.getFreeQuantity());
        discountRule.setApplicableCategories(dto.getApplicableCategories());
        discountRule.setStrategyBeanName(dto.getStrategyBeanName());
        discountRuleRepository.save(discountRule);
        return discountRule;
    }

    @Override
    public Optional<DiscountRule> getById(String id) {
        return discountRuleRepository.findById(id);
    }

    @Override
    public DiscountRule update(String id, DiscountRuleDto dto) {
        // Assuming 'id' is the ruleId for the update operation
        // You might want to add more robust update logic here, e.g., checking if the rule exists
        DiscountRule existingRule = discountRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount rule not found with ID: " + id));

        existingRule.setRuleName(dto.getRuleName());
        if (dto.getDiscountType() != null) {
            try {
                existingRule.setDiscountType(DiscountRule.DiscountType.valueOf(dto.getDiscountType().name()));
            } catch (Exception ignored) {}
        }
        existingRule.setDiscountValue(dto.getDiscountValue());
        if (dto.getApplicableTo() != null) {
            try {
                existingRule.setApplicableTo(DiscountRule.ApplicableTo.valueOf(dto.getApplicableTo().name()));
            } catch (Exception ignored) {}
        }
        existingRule.setApplicableValue(dto.getApplicableValue());
        existingRule.setStartDate(dto.getStartDate());
        existingRule.setEndDate(dto.getEndDate());
        existingRule.setActive(dto.isActive());
        existingRule.setBuyQuantity(dto.getBuyQuantity());
        existingRule.setFreeQuantity(dto.getFreeQuantity());
        existingRule.setApplicableCategories(dto.getApplicableCategories());
        existingRule.setStrategyBeanName(dto.getStrategyBeanName());

        discountRuleRepository.save(existingRule);
        return existingRule;
    }

    @Override
    public void delete(String id) {
        discountRuleRepository.deleteById(id);
    }

    @Override
    public DiscountRule createDiscountRule(DiscountRuleDto discountRuleDto) {
        return create(discountRuleDto);
    }

    @Override
    public Optional<DiscountRule> getDiscountRuleById(String ruleId) {
        return getById(ruleId);
    }

    @Override
    public void deleteDiscountRule(String ruleId) {
        delete(ruleId);
    }
}