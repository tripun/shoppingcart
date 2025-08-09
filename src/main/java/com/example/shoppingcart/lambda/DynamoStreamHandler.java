package com.example.shoppingcart.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamoStreamHandler {

    private static final Logger log = LoggerFactory.getLogger(DynamoStreamHandler.class);
    public void handleRequest(DynamodbEvent event, Context context) {
        log.info("Received DynamoDB stream event with {} records", event.getRecords().size());
        // For local SAM usage, this can be extended to call CdcService via Spring Cloud Function
    }
}
