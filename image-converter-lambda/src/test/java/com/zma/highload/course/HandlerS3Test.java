package com.zma.highload.course;

import static org.junit.jupiter.api.Assertions.*;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;

@Disabled // need to configure aws sdk locally
class HandlerS3Test {

    @Test
    void handleRequest() {
    }

    @ParameterizedTest
    @Event(value = "s3-notification.json", type = S3Event.class)
    void testS3(S3Event event) {
        Context context = new TestContext();
        HandlerS3 handler = new HandlerS3();
        String response = handler.handleRequest(event, context);
        assertEquals("BUCKET_NAME/inbound/sample-java-s3.png", response);
    }
}