package com.tianya.bigdata.tututu.homework.tu20200916

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * 如果用户点击记录为三千万条数据，商品类目信息为二百万条记录
 */
object ClickApp3 {


  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

    val clickFilePath = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/tututu/homework/tu20200916/data/user_click.csv"
    val productCategoryFilePath = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/tututu/homework/tu20200916/data/produce_category_id.csv"

    val clickDF: DataFrame = spark.read
      .format("csv")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .option("header", "true")
      .option("sep", ",")
      .load(clickFilePath)

    val productCategoryDF: DataFrame = spark.read
      .format("csv")
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .option("header", "true")
      .option("sep", ",")
      .load(productCategoryFilePath)

    clickDF.createOrReplaceTempView("click_info")
    productCategoryDF.createOrReplaceTempView("product_category_info")


    spark.stop()


  }



}
