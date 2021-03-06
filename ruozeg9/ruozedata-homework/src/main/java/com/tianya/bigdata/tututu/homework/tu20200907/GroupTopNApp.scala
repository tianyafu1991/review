package com.tianya.bigdata.tututu.homework.tu20200907

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Hive SQL
 * select
 * a.domain,
 * a.url,
 * a.cnt
 * from
 * (select
 * t.domain,
 * t.url,
 * t.cnt,
 * row_number() over(partition by t.domain order by t.cnt desc ) rank
 * from
 * (select
 * domain,
 * url,
 * count(*) as cnt
 * from ruozedata.hive_top_n group by domain,url) t ) a
 * where a.rank <3;
 */
object GroupTopNApp extends Logging {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local[2]").setAppName(this.getClass.getSimpleName)
    val sc = new SparkContext(conf)
    val topN: Int = conf.get("spark.homework.topn", "2").toInt
    log.info(s"...........本次作业取top$topN")
    val path = "ruozeg9/ruozedata-homework/src/main/java/com/tianya/bigdata/tututu/homework/tu20200907/data/data.txt"

    val lineRDD: RDD[String] = sc.textFile(path, 2)


    val topNBrodcast: Broadcast[Int] = sc.broadcast(topN)


    val pairRDD: RDD[((String, String), Int)] = lineRDD.map(line => {
      val splits: Array[String] = line.split(",")
      val domain: String = splits(0)
      val url: String = splits(1)
      ((domain, url), 1)
    })

    val reduceByKeyRDD: RDD[((String, String), Int)] = pairRDD.reduceByKey(_ + _)

    val pairRDD2: RDD[(String, (String, Int))] = reduceByKeyRDD.map(x => {
      val domain: String = x._1._1
      val url = x._1._2
      val cnt = x._2
      (domain, (url, cnt))
    })

    val seqOp: (List[(String, Int)], (String, Int)) => List[(String, Int)] = (x, y) => x :+ y
    val combOp: (List[(String, Int)], List[(String, Int)]) => List[(String, Int)] = (x, y) => x ++ y

    val aggRDD: RDD[(String, List[(String, Int)])] = pairRDD2.aggregateByKey(List[(String, Int)]())(seqOp, combOp)

    aggRDD.map(x => {
      val domain: String = x._1
      val urlCntList: List[(String, Int)] = x._2
      //数据量大的时候这里sort可能会OOM
      val top2List: List[(String, Int)] = urlCntList.sortBy(-_._2).take(topNBrodcast.value)
      (domain, top2List)
    }).flatMap(x => {
      val domain: String = x._1
      x._2.map(tup => domain + "\t" + tup._1 + "\t" + tup._2)
    }).foreach(println)

    sc.stop()
  }

}
