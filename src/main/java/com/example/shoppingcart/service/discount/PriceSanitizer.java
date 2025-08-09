package com.example.shoppingcart.service.discount;

import com.example.shoppingcart.dto.CheckoutResponseDto;
import com.example.shoppingcart.dto.AppliedDiscountDto;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Component
public class PriceSanitizer {

    // This is a placeholder. A real implementation would iterate through line items.
    /**
     * Ensures finalTotalPrice is not negative. If discounts exceed subtotal, scale down
     * discounts proportionally to keep the relative breakdown and set finalTotalPrice to zero.
     */
    /**
     * Small utility to ensure the checkout response has sane totals.
     * - Protects against null applied-discounts lists
     * - Scales down applied discounts proportionally when they exceed the subtotal
     */
    public CheckoutResponseDto sanitize(CheckoutResponseDto response) {
        if (response == null) {
            return null;
        }
        int finalTotal = response.getFinalTotalPrice();
        if (finalTotal >= 0) return response;

        int original = response.getOriginalTotalPrice();
        int totalDiscount = response.getTotalDiscount();
        if (original <= 0 || totalDiscount <= 0) {
            response.setFinalTotalPrice(0);
            response.setTotalDiscount(0);
            response.setAppliedDiscounts(Collections.emptyList());
            return response;
        }

        // Target: finalTotalPrice = 0 => allowedDiscount = original
        int allowedDiscount = original;

        // If totalDiscount > allowedDiscount, scale down each applied discount proportionally
        if (totalDiscount > allowedDiscount) {
            final double scale = (double) allowedDiscount / (double) totalDiscount;
            final List<AppliedDiscountDto> current = response.getAppliedDiscounts();
            // If there are no detailed applied discounts, treat as none and clamp totals
            if (current == null || current.isEmpty()) {
                response.setAppliedDiscounts(Collections.emptyList());
                response.setTotalDiscount(Math.min(totalDiscount, allowedDiscount));
                response.setFinalTotalPrice(Math.max(0, original - response.getTotalDiscount()));
                return response;
            }

            final List<AppliedDiscountDto> adjusted = new ArrayList<>();
            int adjustedTotal = 0;
            for (final AppliedDiscountDto d : current) {
                final int newAmt = (int) Math.round(d.getAmount() * scale);
                final AppliedDiscountDto copy = new AppliedDiscountDto();
                copy.setRuleId(d.getRuleId());
                copy.setRuleName(d.getRuleName());
                copy.setAmount(newAmt);
                copy.setDescription(d.getDescription());
                adjusted.add(copy);
                adjustedTotal += newAmt;
            }

            response.setAppliedDiscounts(adjusted);
            response.setTotalDiscount(adjustedTotal);
            response.setFinalTotalPrice(Math.max(0, original - adjustedTotal));
        } else {
            // discounts within allowed limits; ensure finalTotalPrice computed correctly
            response.setFinalTotalPrice(Math.max(0, original - totalDiscount));
        }
        return response;
    }
}
