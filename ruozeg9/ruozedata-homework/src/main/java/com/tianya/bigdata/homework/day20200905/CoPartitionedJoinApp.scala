package com.tianya.bigdata.homework.day20200905

import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object CoPartitionedJoinApp {

//http://amithora.com/understanding-co-partitions-and-co-grouping-in-spark/
  //https://cloud.tencent.com/developer/article/1390312
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
//      .setMaster("local").setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(conf)

    val data = Array(1, 2, 3, 4, 5)
    val rdd1= sc.parallelize(data,10).map(x=>(x,x)).partitionBy(new HashPartitioner(2))
    val data2 = Array(5,8,9,10,2)
    val rdd2=sc.parallelize(data2,10).map(x=>(x,x)).partitionBy(new HashPartitioner(2))

    val rdd3=rdd1.join(rdd2)
    rdd3.foreach(println)

    sc.stop()
  }

}
