package com.mmonti.consulsample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConsulSampleApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(ConsulSampleApplication.class);
//		application.addListeners(new ContextRefreshedListener());
//		application.getListeners().removeIf(x->x instanceof ConfigFileApplicationListener);
		application.run(args);
	}

	@Value("${appProperties.key1}")
	private String key1;
	@Value("${appProperties.key2}")
	private String key2;
	@Bean
	CommandLineRunner commandLineRunner() {
		return (args) -> {
			System.out.println(key1);
			System.out.println(key2);
		};
	}
}
