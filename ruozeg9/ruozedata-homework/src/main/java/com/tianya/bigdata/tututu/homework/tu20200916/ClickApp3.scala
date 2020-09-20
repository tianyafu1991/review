package com.tianya.bigdata.tututu.homework.tu20200916

import org.apache.spark.sql.{Column, DataFrame, SaveMode, SparkSession}

/**
 * 如果用户点击记录和商品类目信息记录都为三千万条记录
 * 通过将两张表先进行repartition，把相同的key shuffle到一起
 */
object ClickApp3 {


  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()

    val clickFilePath = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/tututu/homework/tu20200916/data/user_click.csv"
    val productCategoryFilePath = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/tututu/homework/tu20200916/data/produce_category_id.csv"

    val outputPath = "hdfs://hadoop:9000/ruozedata/hive/join_table"

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

    import org.apache.spark.sql.functions._
    clickDF.repartition(400,col("product_id"))
    productCategoryDF.repartition(400,col("product_id"))

    clickDF.createOrReplaceTempView("click_info")
    productCategoryDF.createOrReplaceTempView("product_category_info")

    clickDF.printSchema()
    productCategoryDF.printSchema()

    val sql =
      """
        |select
        |a.user_id
        |,b.category_id
        |from click_info a left join product_category_info b
        |on a.product_id = b.product_id
        |where b.category_id is not null
        |""".stripMargin

    spark.sql(sql).write.mode(SaveMode.Overwrite).format("parquet").save(outputPath)

    spark.stop()

  }
}
