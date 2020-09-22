package com.tianya.bigdata.tututu.homework.tu20200916

import org.apache.spark.sql.{Column, DataFrame, SaveMode, SparkSession}

/**
 * 如果用户点击记录和商品类目信息记录都为三千万条记录
 * 通过将两张表先进行repartition，把相同的key shuffle到一起
 * https://github.com/apache/spark/blob/95f1e9549bb741db6c285390a71c609a9e5d3b02/sql/core/src/main/scala/org/apache/spark/sql/execution/SparkStrategies.scala
 *
 * https://stackoverflow.com/questions/40373577/skewed-dataset-join-in-spark
 */
object ClickApp3 {


  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local")
      .appName(this.getClass.getSimpleName)
      .getOrCreate()
    //不限定小表的大小
    spark.conf.set("spark.sql.autoBroadcastJoinThreshold", -1)
    // 每个分区的平均大小不超过spark.sql.autoBroadcastJoinThreshold设定的值
    spark.conf.set("spark.sql.join.preferSortMergeJoin", true)

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
    //如果key有倾斜，repartition可能导致OOM
    /*clickDF.repartition(400,col("product_id"))
    productCategoryDF.repartition(400,col("product_id"))*/

    clickDF.createOrReplaceTempView("click_info")
    productCategoryDF.createOrReplaceTempView("product_category_info")


    clickDF.printSchema()
    productCategoryDF.printSchema()

    /*val sql =
      """
        |select
        |a.user_id
        |,b.category_id
        |
        |from click_info a left join product_category_info b
        |on a.product_id = b.product_id
        |where b.category_id is not null
        |""".stripMargin*/


    val sql =
      """
        |select a.user_id,a.a_rand_product_id, b.category_id,b.b_rand_product_id from
        |(select concat(ceil(1*rand()),'_',product_id) as a_rand_product_id,user_id from click_info) a
        |full outer join
        |(select concat(ceil(1*rand()),'_',product_id) as b_rand_product_id,category_id from product_category_info) b
        | on a.a_rand_product_id=b.b_rand_product_id
        |""".stripMargin

    val result: DataFrame = spark.sql(sql)
    result.explain()
    result.show()
//    result.write.mode(SaveMode.Overwrite).format("parquet").save(outputPath)

    spark.stop()

  }
}
