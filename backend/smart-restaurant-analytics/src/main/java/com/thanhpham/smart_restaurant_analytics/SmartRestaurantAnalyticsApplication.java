package com.thanhpham.smart_restaurant_analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.thanhpham.smart_restaurant_analytics.auth.config.JwtConfig;

@SpringBootApplication
@EnableJpaAuditing // required for @CreatedDate / @LastModifiedDate to work
@EnableConfigurationProperties(JwtConfig.class)
@EnableScheduling
public class SmartRestaurantAnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartRestaurantAnalyticsApplication.class, args);

	}

}
