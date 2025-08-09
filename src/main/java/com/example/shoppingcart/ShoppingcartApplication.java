package com.example.shoppingcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
// This annotation is crucial for Spring to find and implement the JPA repositories for the RDBMS.
@EnableJpaRepositories(basePackages = "com.example.shoppingcart.repository")
// This annotation is crucial for Spring to find and implement the Redis repositories.
@EnableRedisRepositories(basePackages = "com.example.shoppingcart.repository")
public class ShoppingcartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingcartApplication.class, args);
    }
}
