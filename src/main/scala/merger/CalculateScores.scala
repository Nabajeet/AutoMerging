package main.scala.merger

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.types.{ StructType, StructField, StringType };
import org.apache.spark.storage._
import scala.collection.mutable

object CalculateScores {
  def calculate(productsDF: DataFrame, alpha: Double, beta: Double, gamma: Double, sc: SparkContext, sqlContext: SQLContext): DataFrame = {

    //create temporary table
    productsDF.registerTempTable("products")

    val results = sqlContext.sql("SELECT unique_id,non_dict_terms,dict_terms,prod_name,vc_id,id_by_merchant,category_name,sell_price FROM products")

    //convert df to rdd
    val rdd = results.rdd

    val rowArray = rdd.map(x => calculateScores(alpha, beta, gamma, x))

    //create a new schema
    val schemaString = "unique_id non_dict_terms dict_terms prod_name vc_id id_by_merchant scores category_name sell_price"

    val schema = StructType(schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

    //create a data frame
    val productDataFrame = sqlContext.createDataFrame(rowArray, schema)

    //insert database into database
    productDataFrame
  }
  /**
   * Calculate Scores
   */
  def calculateScores(alpha: Double, beta: Double, gamma: Double, row: Row): Row = {
    val nonDictTerms = row(1).toString()
    val dictTerms = row(2).toString()
    val nonDictTermsScore = documentFrequencyScores(nonDictTerms) //get non dictionary terms score
    val dictTermsScore = documentFrequencyScores(dictTerms) //get dictionary terms score
    val strScore = nonDictTermsScore zip dictTermsScore
    //calculate final scores of the set using (alpha*unique_id + beta*non_dict_terms + gamma*dict_terms)
    val finalScoreList = strScore.map(x => {
      alpha + beta * x._1 + gamma * x._2
    })
    val prodNameList = row(3).toString().split("####")
    //val zipList = prodNameList zip finalScoreList
    //zipList.foreach(x => println(x._1 + "-->" + x._2))
    //finalScoreList.foreach(println)
    Row(row(0).toString, row(1).toString, 
        row(2).toString, row(3).toString,
        row(4).toString, row(5).toString,
        finalScoreList.mkString(","),
        row(6).toString, row(7).toString)
  }

  def documentFrequencyScores(str: String): List[Double] = {
    val wordsArray = str.split("[\\,\\|]") //get a list of all words
    val strArray = str.split("[\\,]") //get a list of strings
    val strLen = strArray.length //get no of strings
    //create a set of unique words
    val words = mutable.Set.empty[String]
    for (word <- wordsArray)
      words += word
    //words.foreach(println)
    //get document frequencies of each word i.e (no_of_occurances)/(no_of_strings)
    val docFreq = mutable.Map.empty[String, Double]
    words.foreach(word => {
      //if (word.equals("null"))
      //  docFreq += (word -> 1.0)
      //else {
      val count = {
        var counter = 0.0
        strArray.foreach(str => {
          if (str.split("\\|").find(_.equals(word)).isDefined)
            counter += 1.0
        })
        counter
      }
      docFreq += (word -> count / strLen)
      //}
    })
    //docFreq.foreach(x => println("Key :" + x._1 + " Value :" + x._2))

    //
    val scoresList = mutable.ListBuffer.empty[Double]
    strArray.foreach(str => {
      var score = 0.0
      val termsList = str.split("[\\|]")
      val termsListLen = termsList.length
      termsList.foreach(term => {
        score += docFreq(term)
      })
      scoresList += (score / termsListLen)
    })
    //val strScore = strArray zip scoresList
    //strScore.foreach(x => println(x._1 + "===>" + x._2))
    scoresList.toList
  } //end of documentFrequencyScores
}