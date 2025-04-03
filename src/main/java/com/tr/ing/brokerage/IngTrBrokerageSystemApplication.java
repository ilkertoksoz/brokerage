package com.tr.ing.brokerage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class IngTrBrokerageSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(IngTrBrokerageSystemApplication.class, args);
    }

}