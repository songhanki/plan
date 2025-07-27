package com.board.plan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.board.plan.*.mapper")
public class PlanApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanApplication.class, args);
	}

}
