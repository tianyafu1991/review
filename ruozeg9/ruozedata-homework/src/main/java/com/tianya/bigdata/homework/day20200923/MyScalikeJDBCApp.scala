package com.tianya.bigdata.homework.day20200923

import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scalikejdbc.{ConnectionPool, DB, SQL, scalikejdbcSQLInterpolationImplicitDef}
import scalikejdbc.config.DBs


object MyScalikeJDBCApp extends Logging{

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName(getClass.getSimpleName)
    val ssc = new StreamingContext(conf, Seconds(5))

    DBs.setupAll()
    val lines: ReceiverInputDStream[String] = ssc.socketTextStream("hadoop01", 9527)
    val result: DStream[(String, Long)] = lines.flatMap(_.split(",")).countByValue()
    save2MySQL06(result)


    ssc.start()
    ssc.awaitTermination()
  }

  /**
   * 最优的一种方式，使用了ConnectionPool+prepareStatement+batch+batch控制数据量的方式
   *
   * @param result
   */
  def save2MySQL06(result: DStream[(String, Long)]): Unit = {
    result.foreachRDD(
      rdd => {
        if (!rdd.isEmpty()) {

          rdd.foreachPartition(partition => {
            DB.using(ConnectionPool.borrow()) {
              conn: java.sql.Connection =>
              logError(s"...............${conn}..............")
              val db: DB = DB(conn)

              // set as auto-close disabled
              db.autoClose(false)

              val index: Iterator[((String, Long), Int)] = partition.zipWithIndex

              var batchInsertParams: List[List[Any]] = Nil
              index.foreach {
                case ((word, cnt), index) => {
                  batchInsertParams = batchInsertParams :+ List(word, cnt)
                  if (0 != index && index % 5 == 0) {
                    db.autoCommit { implicit session =>
                      sql"insert into wc(word,cnt) values (?,?)".batch(batchInsertParams: _*).apply()
                      logError(s"插入数据，索引数据为${index}")
                      batchInsertParams = Nil
                    }
                  }
                }
              }
              if(!batchInsertParams.isEmpty){
                db.autoCommit { implicit session =>
                  sql"insert into wc(word,cnt) values (?,?)".batch(batchInsertParams: _*).apply()
                  logError(s"这个是最后一次提交数据，这批数据的条数为${batchInsertParams.size}")
                  batchInsertParams = Nil
                }
              }
              logError(s"关闭连接.....${conn}")

//              conn.close()
            }

          })
        }
      }
    )
  }

  def save2MySQLUseScalikeJDBC() = {
    DB.using(ConnectionPool.borrow()) { conn: java.sql.Connection =>
      val db: DB = DB(conn)

      // set as auto-close disabled
      db.autoClose(false)

      val offsetses: List[Offsets] = Offsets("tianya-test", "tianya_group", 2, 3L) :: Offsets("tianya-test", "tianya_group", 2, 3L) :: Nil

      val batchInsertParams: List[List[Any]] = for (el <- offsetses) yield (List(el.topic, el.groupid, el.partitions, el.offset))

      db.localTx { implicit session =>
        sql"insert into offsets_storage(topic,groupid,partitions,offset) values (?,?,?,?)".batch(batchInsertParams: _*).apply()
      } // localTx won't close the current Connection
    }

  }

  def insert(result: DStream[(String, Long)]): Unit = {
    DB.autoCommit {
      implicit session => {
        SQL("insert into offsets_storage(topic,groupid,partitions,offset) values (?,?,?,?)")
          .bind("pktest", "test-group", 3, 8)
          .update().apply()
      }
    }
  }

}

case class Offsets(topic: String, groupid: String, partitions: Int, offset: Long)

