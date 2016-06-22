package com.seightday.daemon

import groovy.util.logging.Slf4j

import java.util.concurrent.TimeUnit

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class DaemonService {
	
	@Value('${monitor.exe}')
	String moniterExe
	
	@Value('${monitor.restart}')
	String restart
	
	@Value('${monitor.cmd.volume}')
	String cmdVolume
	
	@Scheduled(initialDelayString='${monitor.initialDelay}',fixedDelayString='${monitor.fixedDelay}')
	void monitor(){
		def execute = "tasklist /fi \"imagename eq ${moniterExe}\"".execute()
		def result=printProcess(execute)
		def split = result.split('\r\n')
		
		if(!split){
			return
		}
		def running=false
		for (s in split) {
			if(s.contains(moniterExe)){
				running=true
			}
		}
		if(running){
			log.info "${moniterExe} is running"
			return
		}
		
		log.warn "${moniterExe} is not running, restart it"
		
		"cmd /c \"${cmdVolume}\" & start ${restart}".execute()
		
	}
	
	String printProcess(Process p){
		p.waitFor(1, TimeUnit.SECONDS)
		def result=new StringBuffer()
		log.debug("printProcess")
		def reader = new BufferedReader(new InputStreamReader(p.getErrorStream(), 'gbk'))
		def line=null
		while((line=reader.readLine())!=null){
			log.debug(line)
		}
		
		reader = new BufferedReader(new InputStreamReader(p.getInputStream(), 'gbk'))
		while((line=reader.readLine())!=null){
			log.debug(line)
			result.append(line)
		}
		result.toString()
	}
	
	

}
