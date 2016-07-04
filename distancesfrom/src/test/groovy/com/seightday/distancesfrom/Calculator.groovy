package com.seightday.distancesfrom

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.util.logging.Slf4j

import java.util.concurrent.TimeUnit

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.NoAlertPresentException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.interactions.Action
import org.openqa.selenium.interactions.Actions

@Slf4j
class Calculator {
	  private WebDriver driver;
	  private String baseUrl;
	  private boolean acceptNextAlert = true;
	  private StringBuffer verificationErrors = new StringBuffer();
	  
	  Action tab
	  static Sql db=Sql.newInstance('jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8',
		  'root', '123456', 'com.mysql.jdbc.Driver')
	  
	
	  Calculator(){
		  //使用adblock去除广告，以免广告影响页面加载
		  def ffp=new FirefoxProfile(new File('E:\\adblockonly_firefoxprofile'))
		  driver = new FirefoxDriver(ffp);
		 // driver = new FirefoxDriver();
		  baseUrl = "http://www.distancesfrom.com/";
		  driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		  tab=new Actions(driver).sendKeys(Keys.TAB).build()
	  }
	  String[] calculateDistance(String start,String end){
		  log.info "calculate distance from $start to $end"
		  
		  driver.manage().deleteAllCookies()
		  driver.get(baseUrl + "/");
		  
		  
		  driver.findElement(By.id("Source")).clear();
		  driver.findElement(By.id("Source")).sendKeys(start);
		  tab.perform()
	
		  driver.findElement(By.id("Destination")).clear();
		  driver.findElement(By.id("Destination")).sendKeys(end);
		  tab.perform()
	
		  driver.findElement(By.id("CalculateFare")).click();
		  
		  def getCurrentUrl = driver.getCurrentUrl()
		  log.info "url is $getCurrentUrl"
	
		  def findElement = driver.findElement(By.id('MC_GMD_ctl00_GV_spanKm_0'))
		  def distance=findElement.getText()
		  log.info "distance is $distance"
	
		  def split = distance.split('\n')
		  log.info "split is $split"
		  log.info "driving distance is ${split[0]}"
		  def drivingDistance=split[0]
		  
//		  driver.findElement(By.id('btnRail2')).click()
		  driver.findElement(By.id('btnTrain2')).click()
		  
		  
		  findElement = driver.findElement(By.id('MC_GMD_ctl00_GV_spanKm_0'))
		  distance=findElement.getText()
		  log.info "distance is $distance"
	
		  split = distance.split('\n')
		  log.info "split is $split"
		  log.info "rails distance is ${split[0]}"
		  def railDistance=split[0]
		  
		  [drivingDistance,railDistance]
	  }
	  
	  static List<GroovyRowResult> allRows;
	  
	  
	  
	  static void main(String... args){
		  allRows = db.rows("select * from distance order by id")
//		  allRows = db.rows("select * from distance where  driving is null or driving='' or rail ='' or rail is null  order by id ")
//		  allRows = db.rows("select * from distance where id>=1570 order by id desc ")
		  log.info("rows size is ${allRows.size()}")
		  
		  
		  3.times {
			  new Thread(){
				  void run() {
					  new Calculator().getDistances()
				  };
			  }.start()
		  }
		  
		  //export data
//		  File exportedData=new File('/exportedData.txt')
//		  
//		  allRows.each {
//			  def driving=it.get('driving')
//			  driving=(driving==null?'':driving)
//			  def rail=it.get('rail')
//			  rail=(rail==null?'':rail)
//			  rail=(rail.equals(driving)?'':rail)
//			  
//			  def line=driving+'\t'+rail+'\r\n'
//			  FileUtils.write(exportedData, line, true)
//		  }
	  }
	  
	  void importData(){
		  //将原始数据复制粘贴到文本文档
		  def lines = FileUtils.readLines(new File('/distance/datas.txt'),'utf-8')
		  
		  lines.each {
			  log.info(it)
			  def split = it.split('\t')
			  db.execute("insert into distance (`from`, `to`) values ( ${split[2]},${split[5]} )")
		  }
	  }
	
		public void getDistances() throws Exception {
			
			GroovyRowResult it=null
				
				while(true){
					try {
						it=allRows.pop()
					} catch (Exception e) {
						log.error(null,e)
						driver.quit();
						return
					}
					
					
					log.info(Thread.currentThread().getName())
					log.info("row is $it")
					
					def id=it.get('id')
					def start = it.get('from')
					def end=it.get('to')
					try {
						def (drivingDistance,railDistance)=calculateDistance(start,end)
						db.execute("update distance set driving=$drivingDistance,rail=$railDistance where id=$id")
					} catch (Exception e) {
						log.error(null, e)
					}
				}
			
			
		}
		
	  private boolean isElementPresent(By by) {
		try {
		  driver.findElement(by);
		  return true;
		} catch (NoSuchElementException e) {
		  return false;
		}
	  }
	
	  private boolean isAlertPresent() {
		try {
		  driver.switchTo().alert();
		  return true;
		} catch (NoAlertPresentException e) {
		  return false;
		}
	  }
	
	  private String closeAlertAndGetItsText() {
		try {
		  Alert alert = driver.switchTo().alert();
		  String alertText = alert.getText();
		  if (acceptNextAlert) {
			alert.accept();
		  } else {
			alert.dismiss();
		  }
		  return alertText;
		} finally {
		  acceptNextAlert = true;
		}
	  }

}
