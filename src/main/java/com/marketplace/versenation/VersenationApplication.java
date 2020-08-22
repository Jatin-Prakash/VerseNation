package com.marketplace.versenation;

import com.marketplace.versenation.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class VersenationApplication {

	public static void main(String[] args) {
		SpringApplication.run(VersenationApplication.class, args);
	}

}
