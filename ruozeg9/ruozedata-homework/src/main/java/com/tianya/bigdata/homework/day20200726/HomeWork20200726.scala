package com.tianya.bigdata.homework.day20200726

object HomeWork20200726 {

  def main(args: Array[String]): Unit = {
    /*println(fib(10))
    println(eatPeach)*/

    println(fib2(700))
  }

  /**
   * 输入一个数字，求这个数字对应的斐波那契数列
   * @param x
   * @return
   */
  def fib(x:Int):Int ={
    if(x<=0){return 0}
    if(x ==1 || x==2){return 1}
    fib(x-1)+fib(x-2)
  }

  def fib2(x:Int):Long ={
    var last1 = 1L
    var last2 = 1L
    var tmp = 0L

    if(x <= 0){
      0
    } else if(x ==1 || x==2){
      last1
    } else {
      for(i <- 3 to x){
        tmp = last1
        last1 = last2
        last2 = tmp + last2
      }
      last2
    }
  }

  /**
   * 2) 一堆桃子，猴子第一天吃了其中一半，并再多吃了一个
   * 以后每天猴子都吃其中的一半+一个
   * 当吃到第十天(还没吃) 发现只有一个桃子
   * 问：最初有多少个桃子
   *
   * @return
   */
  def eatPeach():Int={
    //第9天吃完只剩1个桃
    var x = 1
    //这堆桃子总共吃了9天，所以 1 to 9
    for (i <- 1 to 9){
      x = (x +1)*2
    }
    x
  }

}
