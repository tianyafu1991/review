package com.tianya.bigdata.homework.day20200916

import java.text.{ParseException, SimpleDateFormat}
import java.util.{Calendar, Date}

object DateUtils {

  def analysistime(time: String, simpleDateFormat: SimpleDateFormat): Array[String] = {
    var timeParse: Date = null
    try {
      val realtime = time.substring(1, time.length - 1)
      timeParse = simpleDateFormat.parse(realtime)
      val calendar: Calendar = Calendar.getInstance
      calendar.setTime(timeParse)
      val year: String = String.valueOf(calendar.get(Calendar.YEAR))
      val month: String = String.valueOf(calendar.get(Calendar.MONTH))
      val day: String = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))
      val realMonth = if (month.toInt < 10) "0" + month else month
      val realDay = if (day.toInt < 10) "0" + day else day
      return (year :: realMonth :: realDay :: Nil).toArray
    } catch {
      case e: ParseException => e.printStackTrace()
    }
    null
  }
}
