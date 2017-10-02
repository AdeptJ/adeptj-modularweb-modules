/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.aws.sns.internal;

import com.adeptj.modules.aws.core.AwsException;
import com.adeptj.modules.aws.core.AwsUtil;
import com.adeptj.modules.aws.sns.SmsConfig;
import com.adeptj.modules.aws.sns.SmsRequest;
import com.adeptj.modules.aws.sns.SmsResponse;
import com.adeptj.modules.aws.sns.api.AwsSnsAsyncHandler;
import com.adeptj.modules.aws.sns.api.SmsService;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * Service implementation for sending SMS via AWS SNS.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Designate(ocd = SmsConfig.class)
@Component(immediate = true, configurationPolicy = REQUIRE)
public class AwsSnsService implements SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsSnsService.class);

    private AmazonSNSAsync asyncSNS;

    private Map<String, MessageAttributeValue> smsAttributes;

    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            bind = "bindAwsSnsAsyncHandler",
            unbind = "unbindAwsSnsAsyncHandler"
    )
    private volatile AwsSnsAsyncHandler asyncHandler;

    @Override
    public SmsResponse sendSms(SmsRequest smsRequest) {
        try {
            PublishResult result = this.asyncSNS.publish(new PublishRequest()
                    .withMessage(smsRequest.getMessage())
                    .withPhoneNumber(smsRequest.getCountryCode() + smsRequest.getPhoneNumber())
                    .withMessageAttributes(this.smsAttributes));
            return new SmsResponse(result.getMessageId(), result.getSdkHttpMetadata().getHttpStatusCode(),
                    result.getSdkHttpMetadata().getHttpHeaders());
        } catch (Exception ex) {
            LOGGER.error("Exception while sending SMS!!", ex);
            throw new AwsException(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendSmsAsync(SmsRequest smsRequest) {
        try {
            this.asyncSNS.publishAsync(new PublishRequest()
                    .withMessage(smsRequest.getMessage())
                    .withPhoneNumber(smsRequest.getCountryCode() + smsRequest.getPhoneNumber())
                    .withMessageAttributes(this.smsAttributes), this.asyncHandler);
        } catch (Exception ex) {
            LOGGER.error("Exception while sending SMS asynchronously!!", ex);
            throw new AwsException(ex.getMessage(), ex);
        }
    }

    // Component Lifecycle Methods

    protected void bindAwsSnsAsyncHandler(AwsSnsAsyncHandler asyncHandler) {
        this.asyncHandler = asyncHandler;
    }

    protected void unbindAwsSnsAsyncHandler(AwsSnsAsyncHandler asyncHandler) {
        this.asyncHandler = null;
    }

    @Activate
    protected void start(SmsConfig smsConfig) {
        this.smsAttributes = new HashMap<>();
        this.smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue(smsConfig.senderId())
                .withDataType("String"));
        this.smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue(smsConfig.smsType())
                .withDataType("String"));
        try {
            this.asyncSNS = AmazonSNSAsyncClient.asyncBuilder()
                    .withEndpointConfiguration(AwsUtil.getEndpointConfig(smsConfig.serviceEndpoint(),
                            smsConfig.signingRegion()))
                    .withCredentials(AwsUtil.getCredentialsProvider(smsConfig.accessKey(), smsConfig.secretKey()))
                    .build();
        } catch (Exception ex) {
            LOGGER.error("Exception while starting SmsService!!", ex);
            throw new AwsException(ex.getMessage(), ex);
        }
    }

    @Deactivate
    protected void stop() {
        try {
            this.asyncSNS.shutdown();
        } catch (Exception ex) {
            LOGGER.error("Exception while SNS client shutdown!!", ex);
        }
    }
}
