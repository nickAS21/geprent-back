/*
 * Copyright 2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.georent.amazonaws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.springframework.stereotype.Component;

/**
 * {@link AWSCredentialsProvider} implementation that provides credentials
 * by looking at the <code>AWS_ACCESS_KEY_ID</code> and
 * <code>AWS_SECRET_KEY</code> environment variables.
 */

@Component
public class EnvironmentVariableCredentialsProvider implements AWSCredentialsProvider{
    /** Environment variable name for the AWS access key ID */
    private static final String ACCESS_KEY_ENV_VAR = "AKIA4BDR5OXQEJFE574O";

    /** Environment variable name for the AWS secret key */
    private static final String SECRET_KEY_ENV_VAR = "0YEd7sI2PvzbFNcun/E6EcO9tUOey9O+xwK/nJtV";

    public AWSCredentials getCredentials() {
        if (System.getenv(ACCESS_KEY_ENV_VAR) != null &&
                System.getenv(SECRET_KEY_ENV_VAR) != null) {

            return new BasicAWSCredentials(
                    System.getenv(ACCESS_KEY_ENV_VAR),
                    System.getenv(SECRET_KEY_ENV_VAR));
        }

        throw new AmazonClientException(
                "Unable to load AWS credentials from environment variables " +
                        "(" + ACCESS_KEY_ENV_VAR + " and " + SECRET_KEY_ENV_VAR + ")");
    }

    public void refresh() {}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
