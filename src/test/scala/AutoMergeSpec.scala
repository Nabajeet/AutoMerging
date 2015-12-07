package test.scala

import main.scala.merger
import org.specs2._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.types.{ StructType, StructField, StringType };
import org.apache.spark.storage._

class AutoMergeSpec extends Specification {

  //spark config
  val conf = new SparkConf().setAppName("AutoMerging").setMaster("local[*]")
  val sc = new SparkContext(conf)

  //spark sql config
  val sqlContext = new org.apache.spark.sql.SQLContext(sc)

  val brand = "apple"

  def is = s2"""

 This is a specification to check the Automerge functioning

 The Spark Context should pass
   test1                                         $e1
            """

  def e1 = {
    val arrayRow = Array(Row(2067340, "Apple iPhone 6S Silver, 64 GB", "FKTMOBEBY3VHDGMDK9Y", "Silver", "64 GB"))

    val schemaString = "vc_id prod_name id_by_merchant color size"

    val schema = StructType(schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

    val productsDF = sqlContext.createDataFrame(arrayRow, schema)

    val resultsDF = DictionaryPartitioning.partition(productsDF, brand, sc, sqlContext)

  }
}