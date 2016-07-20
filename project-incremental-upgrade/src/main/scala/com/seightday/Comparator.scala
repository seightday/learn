package com.seightday

import java.io.{File, FileInputStream}

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.boot.CommandLineRunner
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

/**
  * Created by heantai on 2016/6/21.
  */
@Component
class Comparator extends CommandLineRunner {

  val logger=LoggerFactory.getLogger(classOf[Comparator])

  @Value("${dir1}")
  val dir1:String=null

  @Value("${dir2}")
  val dir2:String=null

  @Value("${dest}")
  val dest:String=null

  @Value("${exclude.files}")
  val excludeFiles:String=null

  @Autowired
  val jdbcTemplate:JdbcTemplate=null

  override def run(strings: String*): Unit = {

    logger.info(s"dir1 is ${dir1}, dir2 is ${dir2}")

    var files=FileUtils.listFiles(new File(dir1),null,true)
    var it=files.iterator()
    jdbcTemplate.execute("truncate table dir1")
    while(it.hasNext){
      val f=it.next()
      val md5=DigestUtils.md5Hex(new FileInputStream(f))
      val path=f.getAbsolutePath()

      val p=path.replace("\\","/")
      val rpath=p.replace(dir1,"")
      logger.info(s"p is $p")
      jdbcTemplate.execute(s"insert into dir1 (path,md5,rpath) values ('$p','$md5','$rpath')")
    }

    files=FileUtils.listFiles(new File(dir2),null,true)
    it=files.iterator()
    jdbcTemplate.execute("truncate table dir2")
    while (it.hasNext){
      val f=it.next()
      val md5=DigestUtils.md5Hex(new FileInputStream(f))
      val path=f.getAbsolutePath()

      val p=path.replace("\\","/")
      val rpath=p.replace(dir2,"")
      logger.info(s"p is $p")
      jdbcTemplate.execute(s"insert into dir2 (path,md5,rpath) values ('$p','$md5','$rpath')")
    }

    val excludeSummary=new StringBuilder()
    val  summary=new File(dest+"/summary.txt")
    summary.delete()
    //增
    FileUtils.write(summary,"增\r\n",true)
    var records=jdbcTemplate.queryForList("select t1.* from dir1 t1 left join dir2 t2 on t1.rpath=t2.rpath where t2.id is null")
    var iterator=records.iterator()
    while (iterator.hasNext){
      val r=iterator.next()
      val path=r.get("path")

      val excluded=isExclude(path.toString)
      if(excluded){
        logger.info(s"${path} is excluded")
        excludeSummary.append(path).append("\r\n")
      }else{
        val p=dest+r.get("rpath")
        FileUtils.copyFile(new File(path.toString),new File(p))
        FileUtils.write(summary,r.get("rpath")+"\r\n",true)
      }
    }

    //改
    FileUtils.write(summary,"改\r\n",true)
    records=jdbcTemplate.queryForList("select t1.* from dir1 t1 left join dir2 t2 on t1.rpath=t2.rpath where t2.id is not null and t1.md5!=t2.md5")
    iterator=records.iterator()
    while (iterator.hasNext){
      val r=iterator.next()
      val path=r.get("path")

      val excluded=isExclude(path.toString)
      if(excluded){
        logger.info(s"${path} is excluded")
        excludeSummary.append(path).append("\r\n")
      }else{
        val p=dest+r.get("rpath")
        FileUtils.copyFile(new File(path.toString),new File(p))
        FileUtils.write(summary,r.get("rpath")+"\r\n",true)
      }
    }

    //删
    FileUtils.write(summary,"删\r\n",true)
    records=jdbcTemplate.queryForList("select t2.* from dir2 t2 left join dir1 t1 on t1.rpath=t2.rpath where t1.id is null")
    iterator=records.iterator()
    while (iterator.hasNext){
      val r=iterator.next()
      FileUtils.write(summary,r.get("rpath")+"\r\n",true)
    }

    //排除
    FileUtils.write(summary,"排除\r\n",true)
    FileUtils.write(summary,excludeSummary,true)
  }

  def isExclude(path:String):Boolean={
    val files=excludeFiles.split(",")
    val length=files.length

    var excluded=false
    for(i <- 0 until length){
      if(path.endsWith(files.apply(i))){
        excluded=true
      }
    }

    return excluded
  }
}
