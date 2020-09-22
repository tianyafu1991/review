package com.tianya.bigdata.spark.demo

import org.apache.spark.sql.SparkSession

object CreateTableApp {


  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local")
      .enableHiveSupport()
      .getOrCreate()

    spark.sql("select * from ruozedata.emp").show()


    spark.stop()
  }

}
