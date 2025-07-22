package org.unitech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsCurrencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCurrencyApplication.class, args);
    }
}