package com.tianya.bigdata.tututu.homework.tu20200916

import org.apache.spark.sql.{Column, DataFrame, SparkSession}
import org.apache.spark.sql.functions._

/**
 * 如果用户点击记录为三千万条数据，商品类目信息为二百万条记录
 * 考虑将商品类目信息表手动广播出去
 */
object ClickApp2 {


  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
//      .config("spark.sql.autoBroadcastJoinThreshold", "1073741824")
      .master("local")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()

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


    val joinedDF: DataFrame = broadcast(spark.table("product_category_info"))
      .join(spark.table("click_info"), "product_id")

    joinedDF.select("user_id", "category_id").show()
    /*.select('user_id,'category_id)
      .show()*/



    spark.stop()


  }


}
