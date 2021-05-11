package xyz.bookself.it;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.bookself.books.repository.AuthorRepository;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = AbstractIT.DockerPostgreDataSourceInitializer.class)
public abstract class AbstractIT {

    protected static PostgreSQLContainer<?> postgreDBContainer = new PostgreSQLContainer<>("postgres:12.5");

    static {
        postgreDBContainer.start();
        Flyway.configure()
                .locations("db/migration")
                .dataSource(postgreDBContainer.getJdbcUrl(), postgreDBContainer.getUsername(), postgreDBContainer.getPassword())
                .load()
                .migrate();
    }

    public static class DockerPostgreDataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.datasource.driver-class-name=org.postgresql.Driver",
                    "spring.datasource.url=" + postgreDBContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreDBContainer.getUsername(),
                    "spring.datasource.password=" + postgreDBContainer.getPassword()
            );
        }
    }

    @Autowired
    protected AuthorRepository authorRepository;

}
