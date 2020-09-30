package com.tianya.bigdata.homework.day20200920

trait ETL {
  def etl()
}

class Access extends ETL{
  override def etl(): Unit = println("....Access ETL....")
}


class Click extends ETL{
  override def etl(): Unit = println("....Click ETL....")
}


class Video extends ETL{
  override def etl(): Unit = println("....Video ETL....")
}