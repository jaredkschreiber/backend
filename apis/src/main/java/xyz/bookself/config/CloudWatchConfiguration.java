package xyz.bookself.config;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

import java.util.List;
import java.util.Optional;

@Configuration
@ConditionalOnProperty(value = "bookself.aws.cloudwatch.enabled", havingValue = "true")
@EnableConfigurationProperties(CloudWatchConfigProperties.class)
public class CloudWatchConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudWatchConfiguration.class);

    @Value("${bookself.aws.access-key}")
    private String awsAccessKey;
    @Value("${bookself.aws.secret-key}")
    private String awsSecretKey;

    @Bean
    AwsCredentialsProvider credentialsProvider() {
        LOGGER.info("Creating credential provider with {}*** and {}***", awsAccessKey.charAt(0), awsSecretKey.charAt(0));
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey));
    }

    @Bean
    CloudWatchAsyncClient amazonCloudWatchAsync(AwsCredentialsProvider credentialsProvider) {
        LOGGER.info("Creating cloud watch async client with {}*** and {}***",
                credentialsProvider.resolveCredentials().accessKeyId().charAt(0),
                credentialsProvider.resolveCredentials().secretAccessKey().charAt(0));
        return CloudWatchAsyncClient.builder().credentialsProvider(credentialsProvider).region(Region.US_EAST_2).build();
    }

    @Bean
    CloudWatchMeterRegistry cloudWatchMeterRegistry(CloudWatchConfig config, CloudWatchAsyncClient client) {
        var registry = new CloudWatchMeterRegistry(config, Clock.SYSTEM, client);
        registry.config().commonTags(commonTags());
        return registry;
    }

    private Iterable<Tag> commonTags() {
        var hostname = Optional.ofNullable(System.getenv("HOSTNAME")).orElse("UNKNOWN");
        return List.of(Tag.of("hostname", hostname));
    }
}
