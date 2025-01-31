/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazon.sqs.javamessaging;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.payloadoffloading.ServerSideEncryptionFactory;
import software.amazon.payloadoffloading.ServerSideEncryptionStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.mock;


/**
 * Tests the ExtendedClientConfiguration class.
 */
public class ExtendedClientConfigurationTest {

    private static final String s3BucketName = "test-bucket-name";
    private static final String s3ServerSideEncryptionKMSKeyId = "test-customer-managed-kms-key-id";
    private static final ServerSideEncryptionStrategy serverSideEncryptionStrategy = ServerSideEncryptionFactory.customerKey(s3ServerSideEncryptionKMSKeyId);

    @Test
    public void testCopyConstructor() {
        S3Client s3 = mock(S3Client.class);

        boolean alwaysThroughS3 = true;
        int messageSizeThreshold = 500;
        boolean doesCleanupS3Payload = false;

        ExtendedClientConfiguration extendedClientConfig = new ExtendedClientConfiguration();

        extendedClientConfig.withPayloadSupportEnabled(s3, s3BucketName, doesCleanupS3Payload)
                .withAlwaysThroughS3(alwaysThroughS3).withPayloadSizeThreshold(messageSizeThreshold)
                .withServerSideEncryption(serverSideEncryptionStrategy);

        ExtendedClientConfiguration newExtendedClientConfig = new ExtendedClientConfiguration(extendedClientConfig);

        assertEquals(s3, newExtendedClientConfig.getS3Client());
        assertEquals(s3BucketName, newExtendedClientConfig.getS3BucketName());
        assertEquals(serverSideEncryptionStrategy, newExtendedClientConfig.getServerSideEncryptionStrategy());
        assertTrue(newExtendedClientConfig.isPayloadSupportEnabled());
        assertEquals(doesCleanupS3Payload, newExtendedClientConfig.doesCleanupS3Payload());
        assertEquals(alwaysThroughS3, newExtendedClientConfig.isAlwaysThroughS3());
        assertEquals(messageSizeThreshold, newExtendedClientConfig.getPayloadSizeThreshold());

        assertNotSame(newExtendedClientConfig, extendedClientConfig);
    }

    @Test
    public void testLargePayloadSupportEnabledWithDefaultDeleteFromS3Config() {
        S3Client s3 = mock(S3Client.class);
        ExtendedClientConfiguration extendedClientConfiguration = new ExtendedClientConfiguration();
        extendedClientConfiguration.setPayloadSupportEnabled(s3, s3BucketName);

        assertTrue(extendedClientConfiguration.isPayloadSupportEnabled());
        assertTrue(extendedClientConfiguration.doesCleanupS3Payload());
        assertNotNull(extendedClientConfiguration.getS3Client());
        assertEquals(s3BucketName, extendedClientConfiguration.getS3BucketName());

    }

    @Test
    public void testLargePayloadSupportEnabledWithDeleteFromS3Enabled() {

        S3Client s3 = mock(S3Client.class);
        ExtendedClientConfiguration extendedClientConfiguration = new ExtendedClientConfiguration();
        extendedClientConfiguration.setPayloadSupportEnabled(s3, s3BucketName, true);

        assertTrue(extendedClientConfiguration.isPayloadSupportEnabled());
        assertTrue(extendedClientConfiguration.doesCleanupS3Payload());
        assertNotNull(extendedClientConfiguration.getS3Client());
        assertEquals(s3BucketName, extendedClientConfiguration.getS3BucketName());
    }

    @Test
    public void testLargePayloadSupportEnabledWithDeleteFromS3Disabled() {
        S3Client s3 = mock(S3Client.class);
        ExtendedClientConfiguration extendedClientConfiguration = new ExtendedClientConfiguration();
        extendedClientConfiguration.setPayloadSupportEnabled(s3, s3BucketName, false);

        assertTrue(extendedClientConfiguration.isPayloadSupportEnabled());
        assertFalse(extendedClientConfiguration.doesCleanupS3Payload());
        assertNotNull(extendedClientConfiguration.getS3Client());
        assertEquals(s3BucketName, extendedClientConfiguration.getS3BucketName());
    }

    @Test
    public void testCopyConstructorDeprecated() {
        S3Client s3 = mock(S3Client.class);

        boolean alwaysThroughS3 = true;
        int messageSizeThreshold = 500;

        ExtendedClientConfiguration extendedClientConfig = new ExtendedClientConfiguration();

        extendedClientConfig.withPayloadSupportEnabled(s3, s3BucketName)
                .withAlwaysThroughS3(alwaysThroughS3).withPayloadSizeThreshold(messageSizeThreshold);

        ExtendedClientConfiguration newExtendedClientConfig = new ExtendedClientConfiguration(extendedClientConfig);

        assertEquals(s3, newExtendedClientConfig.getS3Client());
        assertEquals(s3BucketName, newExtendedClientConfig.getS3BucketName());
        assertTrue(newExtendedClientConfig.isPayloadSupportEnabled());
        assertEquals(alwaysThroughS3, newExtendedClientConfig.isAlwaysThroughS3());
        assertEquals(messageSizeThreshold, newExtendedClientConfig.getPayloadSizeThreshold());

        assertNotSame(newExtendedClientConfig, extendedClientConfig);
    }

    @Test
    public void testLargePayloadSupportEnabled() {

        S3Client s3 = mock(S3Client.class);

        ExtendedClientConfiguration extendedClientConfiguration = new ExtendedClientConfiguration();
        extendedClientConfiguration.setPayloadSupportEnabled(s3, s3BucketName);

        assertTrue(extendedClientConfiguration.isPayloadSupportEnabled());
        assertNotNull(extendedClientConfiguration.getS3Client());
        assertEquals(s3BucketName, extendedClientConfiguration.getS3BucketName());

    }

    @Test
    public void testDisableLargePayloadSupport() {
        S3Client s3 = mock(S3Client.class);

        ExtendedClientConfiguration extendedClientConfiguration = new ExtendedClientConfiguration();
        extendedClientConfiguration.setPayloadSupportDisabled();

        assertNull(extendedClientConfiguration.getS3Client());
        assertNull(extendedClientConfiguration.getS3BucketName());
    }

    @Test
    public void testMessageSizeThreshold() {

        ExtendedClientConfiguration extendedClientConfiguration = new ExtendedClientConfiguration();

        assertEquals(SQSExtendedClientConstants.DEFAULT_MESSAGE_SIZE_THRESHOLD,
                extendedClientConfiguration.getPayloadSizeThreshold());

        int messageLength = 1000;
        extendedClientConfiguration.setPayloadSizeThreshold(messageLength);
        assertEquals(messageLength, extendedClientConfiguration.getPayloadSizeThreshold());

    }
}
