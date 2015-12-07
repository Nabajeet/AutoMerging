package main.scala.merger

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.types.{ StructType, StructField, StringType };
import org.apache.spark.storage._
import scala.collection.mutable.ListBuffer

object ReduceByUniqueID {
  def reduce(productsDF: DataFrame, sc: SparkContext, sqlContext: SQLContext): DataFrame = {
    //create temporary table
    productsDF.registerTempTable("products")
    val results = sqlContext.sql("SELECT unique_id,non_dict_terms,dict_terms,prod_name,vc_id,id_by_merchant,category_name,sell_price FROM products")

    //convert df to rdd
    val rdd = results.rdd

    //make key-value pair
    val productPair = rdd.keyBy(t => t.getAs[String]("unique_id")).mapValues(x =>
      Row(x(0).toString(), x(1).toString(),
        x(2).toString(), x(3).toString(),
        x(4), x(5).toString(), x(6).toString(),
        x(7).toString()))

    //reduce by key
    val reducedProduct = productPair.reduceByKey((x, y) => this.combineRow(x, y))

    //val filteredResults = reducedProduct.values.filter(x => x(4).toString().contains(","))

    //create a new schema
    val schemaString = "unique_id non_dict_terms dict_terms prod_name vc_id id_by_merchant category_name sell_price"

    val schema = StructType(schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

    //create a data frame
    val productDataFrame = sqlContext.createDataFrame(reducedProduct.values, schema)

    productDataFrame
  }
  /**
   * Combine values whose product name is same
   */
  def combineRow(x: Row, y: Row): Row = {
    Row(x(0), x(1) + "," + y(1), x(2) + "," + y(2),
      x(3) + "####" + y(3), x(4) + "," + y(4),
      x(5) + "," + y(5), x(6) + "," + y(6),
      x(7) + "," + y(7))
  }
}