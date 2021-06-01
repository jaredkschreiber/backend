package xyz.bookself.config;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

@ConfigurationProperties(value = "bookself.aws.cloudwatch")
@Validated
class CloudWatchConfigProperties implements CloudWatchConfig {
    @NotEmpty
    private String namespace;

    @PositiveOrZero
    private int batchSize;

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public int batchSize() {
        return batchSize;
    }

    @Override
    public String get(String key) {
        return null;
    }
}