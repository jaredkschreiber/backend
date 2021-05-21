package xyz.bookself;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.bookself.config.BookselfCorsConfiguration;

@SpringBootApplication
@ConfigurationPropertiesScan({ "xyz.bookself.config" })
@Slf4j
public class BookselfApplication {

	private final BookselfCorsConfiguration corsConfiguration;

	public BookselfApplication(BookselfCorsConfiguration configuration) {
		this.corsConfiguration = configuration;
	}

	public static void main(String[] args) {
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
