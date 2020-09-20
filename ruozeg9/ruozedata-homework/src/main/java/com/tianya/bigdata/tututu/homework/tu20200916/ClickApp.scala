package com.tianya.bigdata.tututu.homework.tu20200916

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * * 使用spark快速求出用户点击过的商品类目。
 */
object ClickApp {


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

    val sql =
      """
        |select
        |a.user_id
        |,b.category_id
        |from click_info a left join product_category_info b
        |on a.product_id = b.product_id
        |where b.category_id is not null
        |""".stripMargin



    clickDF.printSchema()
    productCategoryDF.printSchema()

    spark.sql(sql).show()


    spark.stop()
  }

}
