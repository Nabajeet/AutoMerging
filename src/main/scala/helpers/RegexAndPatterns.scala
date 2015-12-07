package main.scala.helpers

import scala.collection.mutable._
/**
 * @author nabajeet
 */
object RegexAndPatterns {

  val uniqueIdMap = Map(
    "nikon" -> "[a-z]*[0-9]*[.-]?[0-9]+[-]?[a-z]*|nikon",
    "apple" -> "[a-z]+[0-9]+[a-z]+[ ]?[a-z]+[ ]?|[a-z]*[0-9]+[.-]?[0-9]*[a-z]*|apple|air|cellular|imac|ipad|iphone|ipod|macbook|mini|plus|pro")

  val tokenizerMap = Map(
    "nikon" -> "[ ]",
    "apple" -> "[ /]")

  val tokenCleanUpMapOfMap = Map("apple" -> LinkedHashMap(
    "Wi-Fi" -> "Wifi",
    "[\\,+()\"\\[\\]-]" -> " ",
    "&" -> "and",
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