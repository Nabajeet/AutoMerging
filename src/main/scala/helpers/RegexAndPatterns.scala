package main.scala.helpers

import scala.collection.mutable._
/**
 * @author nabajeet
 */
object RegexAndPatterns {

  val uniqueIdMap = Map(
    "apple" -> Map(
      "all_Categories" -> "[a-z]+[0-9]+[a-z]+[ ]?[a-z]+[ ]?|[a-z]*[0-9]+[.-]?[0-9]*[a-z]*|air|cellular|imac|ipad|iphone|ipod|macbook|mini|plus|pro",
      "Mobiles" -> "[0-9]+[a-z]*|iphone|plus", //6|6s|32gb
      "Tablets" -> "[a-z]+[0-9]+[a-z]+[0-9]*[a-z]*|[0-9]+[a-z]*|a|b|apple|ipad|air|mini|cellular|wifi|pro", //mk6y2hn|2|32gb|4th
      "Laptops" -> "[a-z]+[0-9]+[a-z]+[0-9]*[a-z]*|[1-9][0-9][.][0-9][0-9]*|[0-9][0-9]|a|b|macbook|air|pro|mini"), //me293hn|10.10
    "nikon" -> Map("DSLR" -> "[a-z]*[0-9]*[.-]?[0-9]+[-]?[a-z]*|nikon"),
    "motorola" -> Map(
      "Mobiles" -> "[a-z]|[a-z][a-z][0-9][0-9]*|[0-9]+[.]?[0-9]*[a-z]*|moto|nexus|play|style|turbo|defy|atrix"), //x|xt900|16GB/1.6GB
    "samsung" -> Map(
      "Mobiles" -> "[a-z][0-9]+[a-z]*|[a-z]|[0-9]+|ii|iii|ace|advance|alpha|champ|chat|corby|core|deluxe|dual|duos|edge[+]?|fame|galaxy|guru|grand|gt|hero|light|lite|max|mega|metro|mini|music|neo|note|nxt|omnia|on5|on7|plus|pocket|pop|prime|primo|pro|quattro|rex|rugby|sm|star|tab|tizen|trend|wave|young|zoom",
      "Tablets" -> "[0-9]+[.]?[0-9]*|[a-z][0-9]+[a-z]*|[a-z]|[0-9]+[.]?[0-9]*inch|galaxy|neo|note|sm|tab")) //1.1|s1100s|s

  val tokenizerMap = Map(
    "nikon" -> "[ ]",
    "apple" -> "[ /]",
    "motorola" -> "[ /]",
    "samsung" -> "[ /]")

  val tokenCleanUpMapOfMap = Map(
    "apple" -> LinkedHashMap(
      "Wi-Fi" -> "wifi",
      "Wi-fi" -> "wifi",
      "[\\,+()\"\\[\\]-]" -> " ",
      "&" -> "and",
      "Cell " -> "Cellular ",
      "Gray" -> "Grey",
      "Sapce" -> "Space",
      "Golden" -> "Gold",
      """(\d+) ?(MP|GB|mm|cm)""" -> "$1$2 "),
    "motorola" -> LinkedHashMap(
      "[\\,+()\"\\[\\]-]" -> " ",
      "&" -> "and",
      "Locorice" -> "Licorice",
      """(\d+) ?(GB|MB)""" -> "$1$2 "),
    "samsung" -> LinkedHashMap(
      "WiFi" -> "wifi",
      "Wifi" -> "wifi",
      "Wi-Fi" -> "wifi",
      "Wi-fi" -> "wifi",
      """(\([0-9][0-9]*\))""" -> "$1inch",
      "[\\,()\"\\[\\]-]" -> " ",
      "Gray" -> "Grey",
      "Golden" -> "Gold",
      "&" -> "and",
      """(Edge)\+""" -> "$1 plus ",
      """(\d+) ?(GB|MB|cm|Inch|inch)""" -> "$1$2 ",
      """(Tab)(\d+)""" -> "$1 $2 ",
      """(wifi) ?\+ ?(3G|4G)""" -> "$1 $2 ",
      """(\([0-9]\))""" -> "$1inch",
      """([0-9]+[.]?[0-9]*)-(inch)""" -> "$1$2"))

  val colorCleanUpMapOfMap = Map(
    "apple" -> LinkedHashMap(
      "[\\,+()\"\\[\\]-]" -> " ",
      "&" -> " ",
      "Gray" -> "Grey",
      "Sapce" -> "Space",
      "Golden" -> "Gold"),
    "motorola" -> LinkedHashMap(
      "[\\,+()\"\\[\\]-]" -> " ",
      "&" -> " ",
      "Locorice" -> "Licorice"),
    "samsung" -> LinkedHashMap(
      "[\\,+()\"\\[\\]-]" -> " ",
      "&" -> " ",
      "Gray" -> "Grey",
      "Golden" -> "Gold"))
}