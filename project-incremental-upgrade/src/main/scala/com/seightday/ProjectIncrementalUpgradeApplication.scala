package com.seightday
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
  * Created by heantai on 2016/6/21.
  */

@SpringBootApplication
class ProjectIncrementalUpgradeApplication{

}

object ProjectIncrementalUpgradeApplication {

  def main(args: Array[String]) {
    SpringApplication.run(classOf[ProjectIncrementalUpgradeApplication],args:_*)
  }
}
