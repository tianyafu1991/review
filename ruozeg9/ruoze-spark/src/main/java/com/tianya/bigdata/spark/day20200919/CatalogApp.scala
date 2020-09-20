package com.tianya.bigdata.spark.day20200919

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalog.Catalog

object CatalogApp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

    import spark.implicits._


    val catalog: Catalog = spark.catalog
  //查看有哪些库
    catalog.listDatabases().show(true)
    //查看当前库
    println(catalog.currentDatabase)
    //查看库中有哪些表
    catalog.listTables("ruozedata").show(true)
    //做过滤
    catalog.listTables("ruozedata").filter(_.name.contains("emp")).show(true)

    //查看表中有哪些列
    catalog.listColumns("ruozedata","emp").show()

    //查看有哪些函数
    catalog.listFunctions("ruozedata").show()

    //注册函数再查看
    spark.udf.register("str_length",(input:String) => input.trim.length)
    catalog.listFunctions("ruozedata").filter(_.name.contains("length")).show()


    spark.stop()
  }


}
