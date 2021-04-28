package io.bookself.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

	@Test
	@DisplayName("Remove this when we start coding...")
	void testTheTest() {
		Assertions.assertDoesNotThrow(() -> BackendApplication.main(new String[]{}));
		Assertions.assertDoesNotThrow(() -> BackendApplication.somethingToTest("a"));
		Assertions.assertDoesNotThrow(() -> BackendApplication.somethingToTest("b"));
	}

}
