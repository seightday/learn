package com.seightday.daemon

import groovy.util.logging.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
//绑定端口、文件锁定
@Slf4j
@Component
class SingleApplicationService implements CommandLineRunner{
	
	@Value('${monitor.port}')
	private int port;

	@Override
	public void run(String... arg0) throws Exception {
		try {
			log.info("start serversocket")
			new ServerSocket(port)
		} catch (Exception e) {
			log.error("start serversocket failed", e)
			System.exit(0)
		}
	}

}
