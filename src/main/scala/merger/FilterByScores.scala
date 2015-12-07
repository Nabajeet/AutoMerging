package main.scala.merger

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.types.{ StructType, StructField, StringType };
import org.apache.spark.storage._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.math._

object FilterByScores {
  case class DataSet(prodName: String, vcId: String, score: String, idByMerchant: String, nonDictTerms: String, dictTerms: String, categoryName: String, sellPrice: String)

  def filter(productsDF: DataFrame, sc: SparkContext, sqlContext: SQLContext, threshold: Double): DataFrame = {

    //create temporary table
    productsDF.registerTempTable("products")
    val results = sqlContext.sql("SELECT prod_name,vc_id,scores,id_by_merchant,unique_id,non_dict_terms,dict_terms,category_name,sell_price FROM products")

    //convert df to rdd
    val rdd = results.rdd

    val rowArray = rdd.map(row => {
      val prodList = row(0).toString().split("####")
      val vcIdList = row(1).toString().split("[\\,]")
      val scoresList = row(2).toString().split("[\\,]")
      val idByMerchantList = row(3).toString().split("[\\,]")
      val uniqueId = row(4).toString()
      val nonDictTermsList = row(5).toString().split("[\\,]")
      val dictTermsList = row(6).toString().split("[\\,]")
      val categoryNameList = row(7).toString().split("[\\,]")
      val sellPriceList = row(8).toString().split("[\\,]")

      val min = List(prodList, vcIdList, scoresList, idByMerchantList, nonDictTermsList, dictTermsList).map(_.size).min

      val dataSets = (0 until min) map { i => DataSet(prodList(i), vcIdList(i), scoresList(i), idByMerchantList(i), nonDictTermsList(i), dictTermsList(i), categoryNameList(i), sellPriceList(i)) }

      val filteredDatasets = dataSets.filter(x => { x.score.toDouble > threshold })

      val prodListFiltered = ListBuffer[String]()
      val vcIdListFiltered = ListBuffer[String]()
      val scoreListFiltered = ListBuffer[String]()
      val idByMerchantListFiltered = ListBuffer[String]()
      val nonDictTermsListFiltered = ListBuffer[String]()
      val dictTermsListFiltered = ListBuffer[String]()
      val categoryNameListFiltered = ListBuffer[String]()
      val sellPriceListFiltered = ListBuffer[String]()

      filteredDatasets.foreach(x => {
        prodListFiltered += x.prodName
        vcIdListFiltered += x.vcId
        scoreListFiltered += x.score
        idByMerchantListFiltered += x.idByMerchant
        nonDictTermsListFiltered += x.nonDictTerms
        dictTermsListFiltered += x.dictTerms
        categoryNameListFiltered += x.categoryName
        sellPriceListFiltered += x.sellPrice
      })

      Row(prodListFiltered.mkString("####"),
        vcIdListFiltered.mkString(","),
        scoreListFiltered.mkString(","),
        idByMerchantListFiltered.mkString(","),
        uniqueId,
        nonDictTermsListFiltered.mkString(","),
        dictTermsListFiltered.mkString(","),
        categoryNameListFiltered.mkString(","),
        sellPriceListFiltered.mkString(","))
    })

    val filteredResults = rowArray.filter(x => x(1).toString().contains(","))

    //filteredResults.foreach(x => println("row is " + x))

    //create a new schema
    val schemaString = "prod_name vc_id scores id_by_merchant unique_id non_dict_terms dict_terms category_name sell_price"

    val schema = StructType(schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

    //create a data frame
    val productDataFrame = sqlContext.createDataFrame(filteredResults, schema)

    //insert database into database
    productDataFrame
  }
}