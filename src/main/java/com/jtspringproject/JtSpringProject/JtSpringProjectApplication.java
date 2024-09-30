package com.jtspringproject.JtSpringProject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EntityScan(basePackages = {"com.jtspringproject.JtSpringProject.models"})
public class JtSpringProjectApplication {
	private static final Logger logger = LogManager.getLogger(JtSpringProjectApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JtSpringProjectApplication.class, args);
		logger.info("JtSpringProjectApplication started successfully");
//		logger.debug("Debug message");
//		logger.error("Error message");
	}
//	@Bean
//	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//		return args -> {
//			System.out.println("Let's inspect the beans provided by Spring Boot:");
//			String[] beanNames = ctx.getBeanDefinitionNames();
//			Arrays.sort(beanNames);
//			for (String beanName : beanNames) {
//				System.out.println(beanName);
//			}
//		};


	}