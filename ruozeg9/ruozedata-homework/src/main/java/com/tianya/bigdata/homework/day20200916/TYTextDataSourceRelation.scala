package com.tianya.bigdata.homework.day20200916

import java.text.{ParseException, SimpleDateFormat}
import java.util.{Calendar, Date}

import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SQLContext, SaveMode}
import org.apache.spark.sql.sources.{BaseRelation, InsertableRelation, TableScan}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

class TYTextDataSourceRelation(@transient val sqlContext: SQLContext, path: String)
  extends BaseRelation
    with TableScan
    with InsertableRelation
    with Logging {

  override def schema: StructType = {

    StructType(StructField("ip", StringType, false) ::
      StructField("proxyIp", StringType, true) ::
      StructField("responseTime", StringType, true) ::
      StructField("referer", StringType, true) ::
      StructField("method", StringType, true) ::
      StructField("url", StringType, true) ::
      StructField("httpCode", StringType, true) ::
      StructField("requestSize", StringType, true) ::
      StructField("responseSize", StringType, true) ::
      StructField("cache", StringType, true) ::
      StructField("uaHead", StringType, true) ::
      StructField("type", StringType, true) ::
      StructField("province", StringType, true) ::
      StructField("city", StringType, true) ::
      StructField("isp", StringType, true) ::
      StructField("http", StringType, true) ::
      StructField("domain", StringType, true) ::
      StructField("path", StringType, true) ::
      StructField("params", StringType, true) ::
      StructField("year", StringType, true) ::
      StructField("month", StringType, true) ::
      StructField("day", StringType, true) :: Nil)
  }

  override def buildScan(): RDD[Row] = {
    logError("这是天涯自定义的数据源实现：buildScan")
    val input: RDD[String] = sqlContext.sparkContext.textFile(path)
    val splits: RDD[Array[String]] = input.map(_.split("\t").map(_.trim))
    //[01/01/2019:04:13:38]	139.202.30.139	-	4007	-	POST	http://tianyafu992	404	701	-	MISS	Mozilla/5.0（compatible; AhrefsBot/5.0; +http://ahrefs.com/robot/）	text/html
    splits.map(arr => {
      try{
        val time = arr(0)
        val ip = arr(1)
        val proxyIp = arr(2)
        val responseTime = arr(3)
        val referer = arr(4)
        val method = arr(5)
        val url = arr(6)
        val httpCode = arr(7)
        val requestSize = arr(8)
        val responseSize = arr(9)
        if("-".equals(responseSize)){
          throw new IllegalAccessException("")
        }
        val cache = arr(10)
        val uaHead = arr(11)
        val fileType = arr(12)
        //解析ip
        val ipInfos = IpUtils.analysisIp(ip)
        val province = ipInfos(2)
        val city = ipInfos(3)
        val isp = ipInfos(4)


        //解析url
        val urlSplits = url.split("\\?")
        val urlSplits2 = urlSplits(0).split(":")

        val http = urlSplits2(0)
        val urlSpliting = urlSplits2(1).substring(2)
        var domain = urlSpliting
        var path = ""
        if (urlSpliting.contains("/")) {
          domain = urlSpliting.substring(0, urlSpliting.indexOf("/"))
          path = urlSpliting.substring(urlSpliting.indexOf("/"))
        }
        val params = if (urlSplits.length == 2) urlSplits(1) else null
        //解析time
        val simpleDateFormat: SimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy:hh:mm:ss")
        val timeInfos = DateUtils.analysistime(time, simpleDateFormat)
        val year = timeInfos(0)
        val month = timeInfos(1)
        val day = timeInfos(2)
        (ip, proxyIp, responseTime, referer, method, url, httpCode, requestSize, responseSize, cache, uaHead, fileType, province, city, isp, http, domain, path, params, year, month, day)
      }catch {
        case e:Exception => {
          e.printStackTrace()
          null
        }
      }
    }).filter(null != _).map(x => Row.fromTuple(x))

  }

  override def insert(data: DataFrame, overwrite: Boolean): Unit = {
    data.write.mode(if (overwrite) SaveMode.Overwrite else SaveMode.Append).save(path)
  }
}



















