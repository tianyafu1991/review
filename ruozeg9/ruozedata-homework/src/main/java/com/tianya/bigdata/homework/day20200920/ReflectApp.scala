package com.tianya.bigdata.homework.day20200920

import scala.reflect.runtime.{universe => ru}

object ReflectApp {

  /**
   * 总结：
   * 从以下过程中可以得出，编程套路就是通过一个symbol获取一个mirror
   *
   * 1.通过类的全限定名，使用类加载器的mirror获取 class symbol
   * 2.根据class symbol 获取 class type和 class mirror
   * 3.通过class type 获取到 构造器的method symbol
   * 4.使用 class mirror 通过 构造器的method symbol 获取 构造器的 method mirror
   * 5.通过class type 获取到 etl方法的method symbol
   * 6.使用构造器的 method mirror用类加载器获取类的实例镜像
   * 7.使用实例镜像通过etl的method symbol获取etl的method mirror
   * 8.调用etl方法
   * @param args
   */
  def main(args: Array[String]): Unit = {
    val classFullName = "com.tianya.bigdata.homework.day20200920.Video"
    //获取类加载器镜像
    val m: ru.Mirror = ru.runtimeMirror(getClass.getClassLoader)
    //类加载器通过类的全限定名获取class Symbol
    val classSymbol: ru.ClassSymbol = m.staticClass(classFullName)
    //通过class Symbol 获取class Type
    val classType: ru.Type = classSymbol.toType
    //通过class Symbol获取class Mirror
    val cm: ru.ClassMirror = m.reflectClass(classSymbol)
    //通过class Type获取class constructor Symbol
    val ctor: ru.MethodSymbol = classType.decl(ru.termNames.CONSTRUCTOR).asMethod
    //使用class Mirror通过class constructor Symbol 获取class constructor
    val ctorm: ru.MethodMirror = cm.reflectConstructor(ctor)
    //通过class Type获取method Symbol
    val methodSymbol: ru.MethodSymbol = classType.decl(ru.TermName("etl")).asMethod
    //通过class constructor获取instance Mirror
    val im: ru.InstanceMirror = m.reflect(ctorm())
    ////使用instance Mirror通过实例镜像获取method Mirror
    val mm: ru.MethodMirror = im.reflectMethod(methodSymbol)
    //调用方法
    mm()
  }

}
