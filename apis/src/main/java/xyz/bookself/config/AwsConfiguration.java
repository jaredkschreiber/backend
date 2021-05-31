package xyz.bookself.config;

import org.springframework.cloud.aws.context.annotation.ConditionalOnAwsCloudEnvironment;
import org.springframework.context.annotation.Import;

/**
 * This class invokes the autoconfiguraiton for CloudWatch only if running in AWS
 */
@ConditionalOnAwsCloudEnvironment
@Import(org.springframework.cloud.aws.autoconfigure.metrics.CloudWatchExportAutoConfiguration.class)
public class AwsConfiguration { }
