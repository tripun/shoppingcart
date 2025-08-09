package com.example.shoppingcart.service;

import com.example.shoppingcart.model.postgres.Category;
import com.example.shoppingcart.model.postgres.DiscountRule;
import com.example.shoppingcart.model.postgres.Inventory;
import com.example.shoppingcart.model.postgres.Product;
import com.example.shoppingcart.model.postgres.ProductPrice;
import com.example.shoppingcart.model.postgres.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CdcEventListener implements PostInsertEventListener, PostUpdateEventListener {

    private static final Logger log = LoggerFactory.getLogger(CdcEventListener.class);

    private final CdcService cdcService;

    public void onPostInsert(PostInsertEvent event) {
        log.info("JPA PostInsertEvent for entity: {}", event.getEntity().getClass().getSimpleName());
        processCdcEvent(event.getEntity());
    }

    public void onPostUpdate(PostUpdateEvent event) {
        log.info("JPA PostUpdateEvent for entity: {}", event.getEntity().getClass().getSimpleName());
        processCdcEvent(event.getEntity());
    }

    private void processCdcEvent(Object entity) {
        if (entity instanceof Product) {
            cdcService.syncProductToDynamoDB((Product) entity);
        } else if (entity instanceof Inventory) {
            cdcService.syncInventoryToDynamoDB((Inventory) entity);
        } else if (entity instanceof ProductPrice) {
            cdcService.syncProductPriceToDynamoDB((ProductPrice) entity);
        } else if (entity instanceof DiscountRule) {
            cdcService.syncDiscountRuleToDynamoDB((DiscountRule) entity);
        } else if (entity instanceof User) {
            cdcService.syncUserToDynamoDB((User) entity);
        } else if (entity instanceof Category) {
            cdcService.syncCategoryToDynamoDB((Category) entity);
        } else {
            log.warn("Unhandled JPA entity for CDC: {}", entity.getClass().getSimpleName());
        }
    }

    public boolean requiresPostInsertHandling(EntityPersister persister) {
        return true;
    }
    public boolean requiresPostUpdateHandling(EntityPersister persister) {
        return true;
    }
    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        // Not handling post-commit specifically; return false unless needed
        return false;
    }
}