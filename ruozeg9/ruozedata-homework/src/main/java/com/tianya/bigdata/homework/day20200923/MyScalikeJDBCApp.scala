package com.tianya.bigdata.homework.day20200923

import scalikejdbc.{DB, SQL}
import scalikejdbc.config.DBs


object MyScalikeJDBCApp {

  def main(args: Array[String]): Unit = {

    DBs.setupAll()
    insert()


  }

  def insert():Unit={
    DB.autoCommit {
      implicit session =>{
        SQL("insert into offsets_storage(topic,groupid,partitions,offset) vlaues (?,?,?,?)")
          .bind("pktest","test-group",3,8)
          .update().apply()
      }
    }
  }

}
