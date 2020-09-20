package com.tianya.bigdata.homework.day20200913

import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

object MyTextOutputApp {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().appName(this.getClass.getSimpleName).master("local").enableHiveSupport().getOrCreate()
    val csvPath = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/homework/day20200913/data/access.csv"
    val outputPath = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/homework/day20200913/out"
    val accessDF: DataFrame = spark
      .read
      .option("header", "true")
      .option("sep", ",")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .format("csv")
      .load(csvPath)

    accessDF.show(false)
    import spark.implicits._
    val accessDs: Dataset[Access] = accessDF.as[Access]
    accessDs.map(_.toString).write.mode(SaveMode.Overwrite).format("text").save(outputPath)


    spark.stop()
  }

}

case class Access(ip: String, method: String, url: String, province: String, city: String, isp: String) {
  override def toString: String = ip + "\t" + method + "\t" + url + "\t" + province + "\t" + city + "\t" + isp
}
