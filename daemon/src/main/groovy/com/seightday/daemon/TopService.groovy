package com.seightday.daemon

import groovy.util.logging.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class TopService implements CommandLineRunner{
	
	@Value('${monitor.top}')
	String topVbs

	@Override
	public void run(String... arg0) throws Exception {
		log.info "topvbs is ${topVbs}"
		"wscript ${topVbs}".execute()
	}

}
