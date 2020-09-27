package com.tianya.bigdata.homework.day20200916

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources.{BaseRelation, RelationProvider}

class DefaultSource extends RelationProvider{
  override def createRelation(sqlContext: SQLContext, parameters: Map[String, String]): BaseRelation = {

    val path = parameters.get("path")

    path match {
      case Some(p) =>new TYTextDataSourceRelation(sqlContext,p)
      case _ => throw new IllegalArgumentException("path is not exists....")
    }
  }
}
