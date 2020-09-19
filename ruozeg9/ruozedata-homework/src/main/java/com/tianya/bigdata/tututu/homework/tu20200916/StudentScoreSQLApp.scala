package com.tianya.bigdata.tututu.homework.tu20200916

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object StudentScoreSQLApp {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    val filePath = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/tututu/homework/tu20200916/data/student_scores.txt"
    val lineDF: DataFrame = spark.read.format("text").load(filePath)
    import spark.implicits._
    val studentDS: Dataset[StudentScore] = lineDF.map(_.getString(0)).map(line => {
      val splits: Array[String] = line.split("\t")
      StudentScore(splits(0).toInt, splits(1).toInt, splits(2), splits(3).toInt)
    })

    studentDS.show(false)


    spark.stop()
  }

}

case class StudentScore(id:Int,sno:Int,course:String,score:Int)
