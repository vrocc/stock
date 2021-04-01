package com.roc.financial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Roc
 */
@SpringBootApplication(scanBasePackages = {"com.roc.spring.crud", "com.roc.financial"})
@RestController
@MapperScan({"com.roc.financial.dao.mapper"})
@EnableScheduling
public class RocApplication {
    public static void main(String[] args) {
        SpringApplication.run(RocApplication.class, args);
    }
}