package main.scala.helpers

import scala.collection.mutable._
/**
 * @author nabajeet
 */
object RegexAndPatterns {

  val uniqueIdMap = Map(
    "nikon" -> Map("DSLR" -> "[a-z]*[0-9]*[.-]?[0-9]+[-]?[a-z]*|nikon"),
    "apple" -> Map(
      "All_Categories" -> "[a-z]+[0-9]+[a-z]+[ ]?[a-z]+[ ]?|[a-z]*[0-9]+[.-]?[0-9]*[a-z]*|apple|air|cellular|imac|ipad|iphone|ipod|macbook|mini|plus|pro",
      "Mobiles" -> "[0-9]+[a-z]*|apple|iphone|plus", //6,6s,32gb
      "Tablets" -> "[a-z]+[0-9]+[a-z]+[0-9]*[a-z]*|[0-9]+[a-z]*|apple|ipad|air|mini|cellular|wifi", //mk6y2hn,2,32gb,4th
      "Laptops" -> "[a-z]+[0-9]+[a-z]+[0-9]*[a-z]*|[1-9][0-9][.][0-9][0-9]*|[0-9][0-9]|apple|macbook|air|pro|mini")) //me293hn,10.10

  val tokenizerMap = Map(
    "nikon" -> "[ ]",
    "apple" -> "[ /]")

  val tokenCleanUpMapOfMap = Map("apple" -> LinkedHashMap(
    "Wi-Fi" -> "Wifi",
    "[\\,+()\"\\[\\]-]" -> " ",
    "&" -> "and",
    "cell"->"cellular",
    "Grey" -> "Gray",
    "Sapce" -> "Space",
    "Golden" -> "Gold",
    """(\d+) ?(MP|GB|mm|cm)""" -> "$1$2 "))

  val colorCleanUpMapOfMap = Map("apple" -> LinkedHashMap(
    "[\\,+()\"\\[\\]-]" -> " ",
    "&" -> "and",
    "Grey" -> "Gray",
    "Sapce" -> "Space",
    "Golden" -> "Gold"))

}