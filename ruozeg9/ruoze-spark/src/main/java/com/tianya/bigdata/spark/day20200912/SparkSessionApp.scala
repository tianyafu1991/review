package com.tianya.bigdata.spark.day20200912

import org.apache.spark.sql.SparkSession

object SparkSessionApp {


  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local[2]")
      .getOrCreate()


    println(spark.sparkContext.appName)

    spark.sql("show databases").show

    spark.stop()
  }

}
