package com.example.shoppingcart.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlCdcHandler {

    private static final Logger log = LoggerFactory.getLogger(SqlCdcHandler.class);
    public void handleRequest(Object event, Context context) {
        log.info("Received SQL CDC event: {}", event == null ? "<null>" : event.toString());
        // No-op placeholder: in a full SAM deployment this would parse CDC and call application endpoints or services
    }
}
