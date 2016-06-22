package com.seightday.daemon

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DaemonApplication {

	static void main(String[] args) {
		SpringApplication.run DaemonApplication, args
	}
}
