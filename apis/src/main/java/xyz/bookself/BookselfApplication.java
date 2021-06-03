package xyz.bookself;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.bookself.config.BookselfCorsConfiguration;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
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
