package xyz.bookself;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.bookself.config.BookselfCorsConfiguration;

import java.util.TimeZone;

/**
 * Runs the application with all AWS auto configuration resources disabled.
 * They will be enabled via {@link xyz.bookself.config.AwsConfiguration} when this app is ran on AWS.
 */
@SpringBootApplication(exclude = {
		org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration.class,
		org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration.class,
		org.springframework.cloud.aws.autoconfigure.context.ContextResourceLoaderAutoConfiguration.class,
		org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration.class,
		org.springframework.cloud.aws.autoconfigure.mail.MailSenderAutoConfiguration.class,
		org.springframework.cloud.aws.autoconfigure.cache.ElastiCacheAutoConfiguration.class,
		org.springframework.cloud.aws.autoconfigure.messaging.MessagingAutoConfiguration.class,
		org.springframework.cloud.aws.autoconfigure.jdbc.AmazonRdsDatabaseAutoConfiguration.class,
		org.springframework.cloud.aws.autoconfigure.metrics.CloudWatchExportAutoConfiguration.class
})
@ConfigurationPropertiesScan({ "xyz.bookself.config" })
@Slf4j
public class BookselfApplication {

	private final BookselfCorsConfiguration corsConfiguration;

	public BookselfApplication(BookselfCorsConfiguration configuration) {
		this.corsConfiguration = configuration;
	}

	public static void main(String[] args) {
		// Make sure the server always runs in UTC
		// This is redundant when running on the server, but helpful when working locally
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(BookselfApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("*").allowedOrigins(corsConfiguration.getAllowedOrigins());
			}
		};
	}
}
