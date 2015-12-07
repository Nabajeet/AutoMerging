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

object FilterByPrices {
  case class DataSet(prodName: String, vcId: String, score: String, sellPrice: String, idByMerchant: String, nonDictTerms: String, dictTerms: String, categoryName: String)

  def filter(productsDF: DataFrame, sc: SparkContext, sqlContext: SQLContext, priceThreshold: Double) = {

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
      var sum = 0.0
      sellPriceList.foreach(sum += _.toFloat)
      val avg = sum / sellPriceList.size

      val min = List(prodList, vcIdList, scoresList).map(_.size).min

      val dataSets = (0 until min) map { i => DataSet(prodList(i), vcIdList(i), scoresList(i), sellPriceList(i), idByMerchantList(i), nonDictTermsList(i), dictTermsList(i), categoryNameList(i)) }

      val filteredDatasets = dataSets.filter(x => { priceCheck(x, priceThreshold, avg) })

      val prodListFiltered = ListBuffer[String]()
      val vcIdListFiltered = ListBuffer[String]()
      val scoreListFiltered = ListBuffer[String]()
      val sellPriceListFiltered = ListBuffer[String]()
      val idByMerchantListFiltered = ListBuffer[String]()
      val nonDictTermsListFiltered = ListBuffer[String]()
      val dictTermsListFiltered = ListBuffer[String]()
      val categoryNameListFiltered = ListBuffer[String]()

      filteredDatasets.foreach(x => {
        prodListFiltered += x.prodName
        vcIdListFiltered += x.vcId
        scoreListFiltered += x.score
        sellPriceListFiltered += x.sellPrice
        idByMerchantListFiltered += x.idByMerchant
        nonDictTermsListFiltered += x.nonDictTerms
        dictTermsListFiltered += x.dictTerms
        categoryNameListFiltered += x.categoryName
      })

      Row(prodListFiltered.mkString("####"),
        vcIdListFiltered.mkString(","),
        scoreListFiltered.mkString(","),
        sellPriceListFiltered.mkString(","),
        idByMerchantListFiltered.mkString(","),
        uniqueId,
        nonDictTermsListFiltered.mkString(","),
        dictTermsListFiltered.mkString(","),
        categoryNameListFiltered.mkString(","))
    })

    val filteredResults = rowArray.filter(x => x(1).toString().contains(","))

    //create a new schema
    val schemaString = "prod_name vc_id scores sell_price id_by_merchant unique_id non_dict_terms dict_terms category_name"

    val schema = StructType(schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

    //create a data frame
    val productDataFrame = sqlContext.createDataFrame(filteredResults, schema)

    //insert database into database
    productDataFrame
  }
  def priceCheck(x: DataSet, priceThreshold: Double, avg: Double) = {
    val deviation = abs((avg - x.sellPrice.toDouble) / avg)
    deviation < priceThreshold
  }
}