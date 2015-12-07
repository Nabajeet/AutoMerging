package main.scala.merger

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.types.{ StructType, StructField, StringType };
import org.apache.spark.storage._
import scala.collection.mutable._
import main.scala.helpers._
import scala.collection.mutable.ListBuffer

object DictionaryPartitioning {

  def partition(productsDF: DataFrame, brand: String, sc: SparkContext, sqlContext: SQLContext): DataFrame = {

    //create temporary table
    productsDF.registerTempTable("products")

    val results = sqlContext.sql("SELECT vc_id, prod_name, id_by_merchant, color, size, category_name, sell_price from products")

    //convert df to rdd
    val rddRow = results.rdd

    //load dictionary
    val dicWordsRDD = sc.textFile("US.dic").persist(StorageLevel.MEMORY_AND_DISK)
    val dicWordsList = dicWordsRDD.collect().toList

    //get colors and brands
    val colors = sc.textFile("color.txt")
    val colorsList = colors.collect().toList

    val arrayRow = rddRow.map(row => {
      val colorAndBrand = ListBuffer[String]()
      colorAndBrand += brand

      if (!row.isNullAt(3)) {
        var colorString = row(3).toString()
        RegexAndPatterns.colorCleanUpMapOfMap(brand).foreach { x => (colorString = colorString.replaceAll(x._1, x._2)) }
        colorString.split(RegexAndPatterns.tokenizerMap(brand))
          .foreach(x => {
            if (!(x.equals(" ") || x.equals("")))
              colorAndBrand += x.toLowerCase
          })
      }

      //tokenize prod name
      val tokens = ListBuffer[String]()
      var tokenString = row(1).toString()
      RegexAndPatterns.tokenCleanUpMapOfMap(brand).foreach { x => (tokenString = tokenString.replaceAll(x._1, x._2)) }
      tokenString.split(RegexAndPatterns.tokenizerMap(brand))
        .foreach(x => {
          if (!(x.equals(" ") || x.equals("")))
            tokens += x.toLowerCase
        })

      val res = tokens.toList

      //convert tokens into a List
      val productNameList = res.distinct

      val uniqueIDTerms: List[String] =
        if (!row.isNullAt(5)) {
          RegexAndPatterns.uniqueIdMap(brand).get(row(5).toString) match {
            case Some(regex) => {
              //pick up probable model no.
              val probableIdsList = (res.filter((x => regex.r.pattern.matcher(x).matches)) ++ colorAndBrand).distinct.sorted

              //create a unique id using the brand and color and product_no(to be added)
              ((productNameList.intersect(colorsList)) ++ probableIdsList).distinct.sorted
            }
            case None => {
              if (!row(5).toString.equals("Uncategorized"))
                List("not_merged_category") ++ List(System.currentTimeMillis.toString())
              else
                List("un_categorized_category") ++ List(System.currentTimeMillis.toString())
            }
          }
        } else List("null_category") ++ List(System.currentTimeMillis.toString())

      //get terms which are not present in dictionary, not a brand or color
      val nonDictionaryTerms = productNameList.diff(dicWordsList).diff(uniqueIDTerms).sorted

      //get terms which are in dictionary, not a brand or color 
      val dictionaryTerms = productNameList.intersect(dicWordsList).diff(uniqueIDTerms).sorted

      //populate row
      Row(
        row(0).toString(),
        row(1).toString(),
        row(2).toString(),
        if (!row.isNullAt(5)) row(5).toString() else "uncategorized",
        row(6).toString(),
        if (nonDictionaryTerms.isEmpty) "null"
        else if (nonDictionaryTerms.length == 1 && nonDictionaryTerms.head.equals("-")) "null"
        else nonDictionaryTerms.toList.mkString("|"),
        if (dictionaryTerms.isEmpty) "null"
        else if (dictionaryTerms.length == 1 && dictionaryTerms.head.equals("-")) "null"
        else dictionaryTerms.toList.mkString("|"),
        if (!uniqueIDTerms.isEmpty) uniqueIDTerms.toList.mkString("|") else "null")
    })

    //create a new schema
    val schemaString = "vc_id prod_name id_by_merchant category_name sell_price non_dict_terms dict_terms unique_id"

    val schema = StructType(schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

    //create a data frame
    val productDataFrame = sqlContext.createDataFrame(arrayRow, schema)

    productDataFrame
  }
}
