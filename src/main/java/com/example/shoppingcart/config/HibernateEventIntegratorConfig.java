package com.example.shoppingcart.config;

import com.example.shoppingcart.service.CdcEventListener;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class HibernateEventIntegratorConfig implements ApplicationContextAware {

    private final EntityManagerFactory entityManagerFactory;
    private final CdcEventListener cdcEventListener;
    private ApplicationContext applicationContext;

    @PostConstruct
    public void registerListeners() {
        if (entityManagerFactory.unwrap(SessionFactoryImpl.class) == null) {
            throw new IllegalStateException("EntityManagerFactory is not a SessionFactoryImpl");
        }
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(cdcEventListener);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(cdcEventListener);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
