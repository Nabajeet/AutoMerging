package main.scala.merger

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql._

/**
 * @author nabajeet
 */
object AutoMerge {

  //spark config
  val conf = new SparkConf().setAppName("AutoMerging").setMaster("local[*]")
  val sc = new SparkContext(conf)

  //spark sql config
  val sqlContext = new org.apache.spark.sql.SQLContext(sc)

  //set brand
  val brand = "apple"

  val alpha = 0.50 //strength of unique id
  val beta = 0.30 //strength of non-dictionary terms 
  val gamma = 0.20 //strength of dictionary terms

  val scoreThreshold = 0.7 //threshold for filtering products based on score
  val priceThreshold = 0.4 //threshold for filtering based on price

  def main(args: Array[String]) {
    val productsDF = DAO.getProducts(brand, sc, sqlContext)
    //DAO.saveProducts(productsDF, brand + "_products", sc, sqlContext)

    val dictionaryPartitionedDF = DictionaryPartitioning.partition(productsDF, brand, sc, sqlContext)
    //DAO.saveProducts(dictionaryPartitionedDF, brand + "_dict_partitioned", sc, sqlContext)

    val reducedDF = ReduceByUniqueID.reduce(dictionaryPartitionedDF, sc, sqlContext)
    //DAO.saveProducts(reducedDF, brand + "_reduced", sc, sqlContext)

    val scoredDF = CalculateScores.calculate(reducedDF, alpha, beta, gamma, sc, sqlContext)
    //DAO.saveProducts(scoredDF, brand + "_scored", sc, sqlContext)

    val scoreFilteredDF = FilterByScores.filter(scoredDF, sc, sqlContext, scoreThreshold)
    //DAO.saveProducts(scoreFilteredDF, brand + "_scoreFiltered", sc, sqlContext)

    val priceFilteredDF = FilterByPrices.filter(scoreFilteredDF, sc, sqlContext, priceThreshold)
    DAO.saveProducts(priceFilteredDF, brand, sc, sqlContext)
  }
}