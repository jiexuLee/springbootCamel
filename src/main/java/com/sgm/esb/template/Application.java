package com.sgm.esb.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sgm.esb.fuse.core.annotation.EnableLoggerConfiguration;

@SpringBootApplication
@EnableLoggerConfiguration
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
