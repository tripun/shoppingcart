package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.AppApiResponse;
import com.example.shoppingcart.dto.DiscountRuleDto;
import com.example.shoppingcart.model.dynamo.DiscountRule;
import com.example.shoppingcart.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v2/admin/discounts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Discount Rule Management", description = "Endpoints for managing discount rules")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class DiscountRuleController {

    private static final Logger log = LoggerFactory.getLogger(DiscountRuleController.class);

    private final DiscountService discountService;

    @PostMapping
    @Operation(summary = "Create a new discount rule")
    @ApiResponses({
       @ApiResponse(responseCode = "201", description = "Discount rule created successfully"),
       @ApiResponse(responseCode = "400", description = "Invalid request data"),
       @ApiResponse(responseCode = "401", description = "Unauthorized"),
       @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<AppApiResponse<DiscountRule>> createDiscountRule(@Valid @RequestBody DiscountRuleDto discountRuleDto) {
        log.info("Creating new discount rule: {}", discountRuleDto.getRuleName());
        DiscountRule createdRule = discountService.createDiscountRule(discountRuleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(AppApiResponse.success("Discount rule created successfully", createdRule));
    }

    @GetMapping("/{ruleId}")
    @Operation(summary = "Get a discount rule by its ID")
    @ApiResponses({
       @ApiResponse(responseCode = "200", description = "Discount rule retrieved successfully"),
       @ApiResponse(responseCode = "404", description = "Discount rule not found")
    })
    public ResponseEntity<AppApiResponse<DiscountRule>> getDiscountRuleById(@Parameter(description = "Discount Rule ID", example = "DR12345") @PathVariable String ruleId) {
        log.debug("Retrieving discount rule: {}", ruleId);
        return discountService.getDiscountRuleById(ruleId)
                .map(rule -> ResponseEntity.ok(AppApiResponse.success("Discount rule retrieved successfully", rule)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(AppApiResponse.error("Discount rule not found")));
    }

    // Removed getAllDiscountRules() as it would require a full table scan.
    // @GetMapping
    // @Operation(summary = "Get all discount rules")
    // public ResponseEntity<AppApiResponse<List<DiscountRule>>> getAllDiscountRules() {
    //     log.debug("Retrieving all discount rules");
    //     List<DiscountRule> rules = discountService.getAllDiscountRules();
    //     return ResponseEntity.ok(AppApiResponse.success("Discount rules retrieved successfully", rules));
    // }

    @DeleteMapping("/{ruleId}")
    @Operation(summary = "Delete a discount rule by its ID")
    @ApiResponses({
       @ApiResponse(responseCode = "200", description = "Discount rule deleted successfully"),
       @ApiResponse(responseCode = "404", description = "Discount rule not found")
    })
    public ResponseEntity<AppApiResponse<Void>> deleteDiscountRule(@Parameter(description = "Discount Rule ID", example = "DR12345") @PathVariable String ruleId) {
        log.info("Deleting discount rule: {}", ruleId);
        discountService.deleteDiscountRule(ruleId);
        return ResponseEntity.ok(AppApiResponse.success("Discount rule deleted successfully", null));
    }
}