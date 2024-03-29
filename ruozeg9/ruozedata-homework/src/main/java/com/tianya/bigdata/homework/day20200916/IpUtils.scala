package com.tianya.bigdata.homework.day20200916

import java.lang.reflect.Method

import org.lionsoul.ip2region.{DataBlock, DbConfig, DbSearcher}

object IpUtils {

  def getMethod(searcher: DbSearcher): Method = searcher.getClass.getMethod("btreeSearch", classOf[String])

  def getDbSearcher(config: DbConfig, dbPath: String): DbSearcher = new DbSearcher(config, dbPath)

  def getDbConfig(): DbConfig = new DbConfig

  /**
   * province = arr(2)
   * city = arr(3)
   * isp = arr(4)
   *
   * @param ip
   * @return
   */
  def analysisIp(ip:String):Array[String] ={
    val dbPath = "F:\\study\\workspace\\review\\ruozeg9\\db\\ip2region.db"
    val dbConfig = getDbConfig()
    val dbSearcher = getDbSearcher(dbConfig, dbPath)
    val method = getMethod(dbSearcher)
    val block: DataBlock = method.invoke(dbSearcher, ip).asInstanceOf[DataBlock]
    block.getRegion.split("\\|")
  }

}
