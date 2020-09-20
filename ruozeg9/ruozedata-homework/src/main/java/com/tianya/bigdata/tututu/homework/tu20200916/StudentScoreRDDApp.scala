package com.tianya.bigdata.tututu.homework.tu20200916

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object StudentScoreRDDApp {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    val filePath = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/tututu/homework/tu20200916/data/student_scores.csv"

    val lineRDD: RDD[String] = spark.sparkContext.textFile(filePath)

    lineRDD.take(10).foreach(println)



    spark.stop()
  }

}
