package main.scala.merger

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.storage._
import org.apache.spark.sql.types.{ StructType, StructField, StringType };
import org.apache.spark.sql._
import scala.collection.mutable.ListBuffer
import org.apache.log4j.Logger

object DAO {
  //set db properties
  val prop: java.util.Properties = new java.util.Properties()
  prop.setProperty("user", "tdp_user")
  prop.setProperty("password", "tdp_passwd")
  val url = "jdbc:mysql://localhost:3306/merge_test"

  def getProducts(brand: String, sc: SparkContext, sqlContext: SQLContext): DataFrame = {
    //get data from products table
    val productsDF = sqlContext.read.jdbc(url, "pc_products_test", //pc_products_test
      Array("brand='" + brand + "'"), prop)
    //****

    //get data from product details table for selected vc_ids
    productsDF.registerTempTable("products")
    val vcIdList = ListBuffer[String]()
    val vcIdListDF = sqlContext.sql("SELECT vc_id FROM products")
    vcIdListDF.collect().foreach(t => (vcIdList += t(0).toString()))
    val productDetailsDF = sqlContext.read.jdbc(url, "pc_prod_detail_test", //pc_prod_detail_test
      Array("vc_id in (" + vcIdList.mkString(",") + ")"), prop)
    productDetailsDF.registerTempTable("product_details")
    val productDetailsFilteredDF = sqlContext.sql("SELECT vc_id, category_path FROM product_details")
    //saveProducts(productDetailsFilteredDF, "spark_product_details", sc, sqlContext)
    //****

    //get category path list from product details  
    val categoryPathList = ListBuffer[String]()
    productDetailsFilteredDF.collect().foreach(x => {
      categoryPathList += x(1).toString
    })
    //****

    //get data from merchant_category table for the category_paths
    val merchantCategoryDF = sqlContext.read.jdbc(url, "pc_merchant_category", //pc_merchant_category
      Array("category_path in ('" + categoryPathList.distinct.mkString("\',\'") + "')"), prop)
    merchantCategoryDF.registerTempTable("merchant_category")
    val merchantCategoryFilteredDF = sqlContext.sql("SELECT category_path,mapped_id FROM merchant_category")
    //***

    //join product details with mapped_id
    val productDeatilsWithMappedIdDF = merchantCategoryFilteredDF.join(productDetailsFilteredDF,
      "category_path")
    //saveProducts(productDeatilsWithMappedIdDF, "spark_product_details_with_mapped_id", sc, sqlContext)

    //extract distinct mapped_id from merchant_category 
    val mappedIdList = ListBuffer[String]()
    productDeatilsWithMappedIdDF.collect().foreach(x => {
      mappedIdList += x(1).toString
    })
    //***

    //extract data from vc_menu based on mapped_id
    val vcMenuDF = sqlContext.read.jdbc(url, "pc_vc_menu",
      Array("slug in ('" + mappedIdList.distinct.mkString("\',\'") + "')"), prop)
    vcMenuDF.registerTempTable("vc_menu")
    val vcMenuFilteredDF = sqlContext.sql("SELECT name,slug FROM vc_menu")
    //****

    //Join product details and category name
    val productDetailsWithCategoryNameDF = vcMenuFilteredDF.join(productDeatilsWithMappedIdDF, vcMenuDF("slug") === productDeatilsWithMappedIdDF("mapped_id"))
    //saveProducts(productDetailsWithCategoryNameDF, "spark_product_details_with_name", sc, sqlContext)
    //***

    //take vc_id,category_name,sell_price from the data
    productDetailsWithCategoryNameDF.registerTempTable("product_id_categoryname")
    val productIdWithCategoryName = sqlContext.sql("SELECT vc_id as id,name as category_name FROM product_id_categoryname")
    //****

    //join with products table based on vc_id 
    val productAndDetailsDF = productIdWithCategoryName.join(productsDF,
      productIdWithCategoryName("id") === productsDF("vc_id"), "right_outer")
    //****

    //filter out unnecessary data
    productAndDetailsDF.registerTempTable("products_and_details")
    val productAndDetailsFilteredDF = sqlContext.sql(
      "SELECT vc_id, prod_name, color, size, category_name FROM products_and_details")
    //saveProducts(productAndDetailsFilteredDF, "spark_product_details", sc, sqlContext)
    //****

    //get sell_price
    val resultDF = productAndDetailsFilteredDF.join(productDetailsDF, "vc_id")
    resultDF.registerTempTable("results")
    val resultsFilteredDF = sqlContext.sql(
      "SELECT vc_id, prod_name, id_by_merchant, color, size, category_name, sell_price FROM results")
    //saveProducts(resultsFilteredDF, "spark_products", sc, sqlContext)
    resultsFilteredDF
    //****
  }

  //  def getPrices(vc_ids: String, sc: SparkContext, sqlContext: SQLContext): String = {
  //    val productDF = sqlContext.read.jdbc(url, "pc_prod_detail_test", Array("vc_id in (" + vc_ids + ")"), prop)
  //    val priceDF = productDF.select("sell_price")
  //    val prodListFiltered = ListBuffer[String]()
  //    priceDF.collect().foreach(x => prodListFiltered += x(0).toString)
  //    prodListFiltered.mkString(",")
  //  }

  def saveProducts(productsDF: DataFrame, brand: String, sc: SparkContext, sqlContext: SQLContext) {
    productsDF.write.mode("overwrite").jdbc(url, "spark_auto_merge_" + brand, prop)
  }
}

