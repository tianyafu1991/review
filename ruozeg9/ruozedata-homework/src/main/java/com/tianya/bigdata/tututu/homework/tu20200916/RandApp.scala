package com.tianya.bigdata.tututu.homework.tu20200916

import org.apache.spark.sql.SparkSession

object RandApp {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()



    val sql = "select concat(floor(100*rand()),'_','1')"

    spark.sql(sql).show()

    spark.stop()
  }

}
