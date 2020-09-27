package com.tianya.bigdata.tututu.homework.tu20200925

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * 增强SparkContext的textFile方法，能够读取多个目录
 * @param sc
 */
class RichSparkContext(val sc: SparkContext) {

  def defaultMinPartitions: Int = sc.defaultMinPartitions

  def textFile(path: String, paths: String*)(minPartitions: Int): RDD[String] = {
    val pathsArray = new Array[String](paths.length + 1)
    pathsArray(0) = path
    var i = 1
    for (elem <- paths.iterator) {
      pathsArray(i) = elem
      i += 1
    }
    sc.union(pathsArray.map(x => {
      sc.textFile(x, minPartitions)
    }))
  }

  def textFile(paths: String*)(minPartitions: Int): RDD[String] = {
    val firstPath = paths(0)
    paths.slice(1, paths.length)
    textFile(firstPath, paths.slice(1, paths.length).toArray: _*)(minPartitions)
  }


  def textFile(path: String, minPartitions: Int = defaultMinPartitions): RDD[String] = {
    val paths: Array[String] = path.split(",")
    textFile(paths: _*)(minPartitions)
  }


}

object RichSparkContext {
  implicit def sc2RichSc(sc: SparkContext): RichSparkContext = new RichSparkContext(sc)

  def main(args: Array[String]): Unit = {
    val path1 = "path1"
    val path2 = "path2"
    val path3 = "path3"
    val paths = path1 :: path2 :: path3 :: Nil

    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local")
    val sc = new SparkContext(conf)
    sc.textFile(paths.mkString(",")).foreach(println)
    sc.stop()
  }
}
