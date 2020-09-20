package com.tianya.bigdata.tututu.homework.tu20200916

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
 * RDD实现
 */
object StudentScoreRDDApp {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    val filePath = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/tututu/homework/tu20200916/data/student_scores.csv"
    val lineRDD: RDD[String] = spark.sparkContext.textFile(filePath)
    val studentScoreRDD: RDD[StudentScore] = lineRDD.map(x => {
      val splits: Array[String] = x.split("\t")
      if("id".equals(splits(0))){
        null
      }else{
        StudentScore(splits(0).toInt, splits(1).toInt, splits(2), splits(3).toInt)
      }
    }).filter(_ != null)

    val pairRDD: RDD[(Int, StudentScore)] = studentScoreRDD.map(x => (x.sno, x))

    val minScoreRDD: RDD[(Int, StudentScore)] = pairRDD.reduceByKey((x, y) => {
      if ((x.score - y.score >= 0)) y else x
    })

    val resultRDD: RDD[Int] = minScoreRDD.filter(x => {
      "chinese".equals(x._2.course)
    }).map(_._1)
    resultRDD.take(10).foreach(println)

    spark.stop()
  }

}

case class StudentScore(id: Int, sno: Int, course: String, score: Int)
