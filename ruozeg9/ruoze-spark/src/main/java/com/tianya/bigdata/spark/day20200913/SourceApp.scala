package com.tianya.bigdata.spark.day20200913

import org.apache.spark.sql.{Dataset, Row, SaveMode, SparkSession}

object SourceApp {


  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").getOrCreate()

    import spark.implicits._

    val df = spark.read.format("text").load("ruozeg9/ruoze-spark/src/main/java/com/tianya/bigdata/spark/day20200913/data/emp.txt")

    val empDs: Dataset[Emp] = df.map((row: Row) => {
      val splits = row(0).toString().split("\t")
      //      println(row(0))
      Emp(splits(0), splits(1), splits(2), splits(3), splits(4), splits(5), splits(6), splits(7))
    })

    empDs.select("empno","ename")
        .where('ename === "KING")
        .write
      .option("timestampFormat", "yyyy/MM/dd HH:mm:ss ZZ")
      .mode(SaveMode.Overwrite)
      .format("json")
        .save("out")




//    empDs.show()

//    df.select("")




    spark.stop()
  }

}

case class Emp(empno:String,ename:String,job:String,mgr:String,hiredate:String,sal:String,comm:String,deptno:String)
