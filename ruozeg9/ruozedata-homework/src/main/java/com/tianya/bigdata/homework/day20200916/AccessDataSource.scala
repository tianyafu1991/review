package com.tianya.bigdata.homework.day20200916

import org.apache.spark.sql.SparkSession

object AccessDataSource {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[2]")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()

    val inputDf = spark.read.format("com.tianya.bigdata.homework.day20200916")
      .load("F:\\study\\workspace\\review\\ruozeg9\\data\\access_spark.log")

    inputDf.show()
    spark.close()
  }

}
