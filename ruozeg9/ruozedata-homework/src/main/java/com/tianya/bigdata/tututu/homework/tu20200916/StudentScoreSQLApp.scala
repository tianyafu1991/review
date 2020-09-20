package com.tianya.bigdata.tututu.homework.tu20200916

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object StudentScoreSQLApp {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    val filePath = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/tututu/homework/tu20200916/data/student_scores.csv"
    val studentScoreDF: DataFrame = spark.read
      .format("csv")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .option("header", "true")
      .option("sep", "\t")
      .load(filePath)

    studentScoreDF.createOrReplaceTempView("student_score")

    val sql =
      """
        | select
        | a.sno
        | from
        | student_score a
        | join
        | (select
        |sno,
        |min(score) min_score
        | from
        | student_score
        | group by sno ) b
        | on a.sno = b.sno and a.score = b.min_score
        | where a.course='chinese'
        |""".stripMargin

    spark.sql(sql).show()



    //    studentScoreDF.show()


    spark.stop()
  }

}

case class StudentScore(id: Int, sno: Int, course: String, score: Int)
