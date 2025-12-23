package com.ecommerce.product;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@SpringBootApplication(
		scanBasePackages = {
				"com.ecommerce.product", "com.common_logging.logging.library"
		}
)
@EnableScheduling
@Slf4j
public class ProductServiceApplication {
	public static void main(String[] args) {
		long maxMemory = Runtime.getRuntime().maxMemory();       // bytes
		long totalMemory = Runtime.getRuntime().totalMemory();   // current heap size
		long freeMemory = Runtime.getRuntime().freeMemory();     // free in current heap
		int cores = Runtime.getRuntime().availableProcessors();

		System.out.println("Max heap (MB): " + (maxMemory / 1024 / 1024));
		System.out.println("Total heap (MB): " + (totalMemory / 1024 / 1024));
		System.out.println("Free heap (MB): " + (freeMemory / 1024 / 1024));
		System.out.println("Available CPU cores: " + cores);
		SpringApplication.run(ProductServiceApplication.class, args);
	}

	@PostConstruct
	public void logStartup() {
		log.info(">>> JVM STARTED AT: {} <<<", LocalDateTime.now());
	}
}
