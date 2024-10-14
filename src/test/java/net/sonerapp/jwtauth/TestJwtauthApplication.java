package net.sonerapp.jwtauth;

import org.springframework.boot.SpringApplication;

public class TestJwtauthApplication {

	public static void main(String[] args) {
		SpringApplication.from(JwtauthApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
