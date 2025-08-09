package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.model.postgres.DiscountRule;
import com.example.shoppingcart.repository.DiscountRuleWriteRepository;
import com.example.shoppingcart.service.DiscountRuleWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscountRuleWriteServiceImpl implements DiscountRuleWriteService {

    private static final Logger log = LoggerFactory.getLogger(DiscountRuleWriteServiceImpl.class);

    private final DiscountRuleWriteRepository discountRuleWriteRepository;

    @Autowired
    public DiscountRuleWriteServiceImpl(DiscountRuleWriteRepository discountRuleWriteRepository) {
        this.discountRuleWriteRepository = discountRuleWriteRepository;
    }

    @Override
    @Transactional
    public DiscountRule createOrUpdateDiscountRule(DiscountRule discountRule) {
        log.info("WRITING to RDBMS: Creating/updating discount rule with ID: {}", discountRule.getId());
        return discountRuleWriteRepository.save(discountRule);
    }
}
