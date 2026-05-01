package com.thanhpham.smart_restaurant_analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // required for @CreatedDate / @LastModifiedDate to work
@SpringBootApplication
public class SmartRestaurantAnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartRestaurantAnalyticsApplication.class, args);
	}

}
