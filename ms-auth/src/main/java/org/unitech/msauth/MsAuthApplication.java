package org.unitech.msauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MsAuthApplication {

    public static void main(String[] args) {
        System.out.println("MsAuthApplication started");
        SpringApplication.run(MsAuthApplication.class, args);
    }
}