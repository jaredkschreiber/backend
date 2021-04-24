package io.bookself.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		somethingToTest("a");
	}

	/**
	 * Created solely to test CICD -- remove when we start coding in earnest
	 */
	static void somethingToTest(String input) {
		if ("a".equals(input)) {
			System.out.println("It's a match!");
		} else {
			System.out.println("Try again...");
		}
	}

}
