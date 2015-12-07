package main.scala.merger

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object TempTest {
  val conf = new SparkConf().setAppName("AutoMerging").setMaster("local[4]")
  val sc = new SparkContext(conf)

  def getLength(data: Array[Int]): Long = {
    val distData = sc.parallelize(data)
    distData.count()
  }
}