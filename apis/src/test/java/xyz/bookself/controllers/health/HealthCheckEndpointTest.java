package xyz.bookself.controllers.health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HealthCheckEndpointTest {

    @Autowired
    private HealthCheckController healthCheckController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void pingOfHealthCheckControllerShouldReturnTheStringPong() {
        assertThat(healthCheckController.ping()).isEqualTo("pong");
    }

    @Test
    public void endPointPingShouldExistAndItShouldRespondWithTheStringPong() throws Exception {
        this.mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }
}
