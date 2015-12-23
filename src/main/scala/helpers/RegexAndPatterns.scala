package main.scala.helpers

import scala.collection.mutable._
/**
 * @author nabajeet
 */
object RegexAndPatterns {

  val uniqueIdMap = Map(
    "apple" -> Map(
      "Mobiles" -> "[0-9]+[a-z]*|iphone|plus", //6|6s|32gb
      "Tablets" -> "[a-z]+[0-9]+[a-z]+[0-9]*[a-z]*|[0-9]+[a-z]*|a|b|apple|ipad|air|mini|cellular|wifi|pro", //mk6y2hn|2|32gb|4th
      "Laptops" -> "[a-z]+[0-9]+[a-z]+[0-9]*[a-z]*|[1-9][0-9][.][0-9][0-9]*|[0-9][0-9]|a|b|macbook|air|pro|mini"), //me293hn|10.10
    "motorola" -> Map(
      "Mobiles" -> "[a-z]|[a-z][a-z][0-9][0-9]*|[0-9]+[.]?[0-9]*[a-z]*|moto|nexus|play|style|turbo|defy|atrix"), //x|xt900|16GB/1.6GB
    "samsung" -> Map(
      "Mobiles" -> "[a-z][0-9]+[a-z]*|[a-z]|[0-9]+|ii|iii|ace|advance|alpha|champ|chat|corby|core|deluxe|dual|duos|edge[+]?|fame|galaxy|guru|grand|gt|hero|light|lite|max|mega|metro|mini|music|neo|note|nxt|omnia|on5|on7|plus|pocket|pop|prime|primo|pro|quattro|rex|rugby|sm|star|tab|tizen|trend|wave|young|zoom",
      "Tablets" -> "[0-9]+[.]?[0-9]*|[a-z][0-9]+[a-z]*|[a-z]|[0-9]+[.]?[0-9]*inch|galaxy|neo|note|sm|tab"), //1.1|s1100s|s
    //"Laptops" -> "[\\w]+[-][\\w]+[-]?[\\w]"),
    "lenovo" -> Map(
      "Mobiles" -> "[a-z]|[a-z][0-9]+[a-z]?|ideaphone|note|music|plus|shot|vibe",
      "Tablets" -> "[a-z]|[0-9][0-9]?|[a-z][0-9]+[a-z]?|[a-z][0-9]+[-][0-9]+[a-z]?|[0-9]+[.]?[0-9]*inch|idea|miix|thinkpad|yoga"),
    "asus" -> Map(
      "Mobiles" -> "[a-z]|[0-9][.]?[0-9]?|[a-z]+[0-9]+[a-z]+|8gb|16gb|32gb|64gb|128gb|zenfone|padfone|mini|deluxe|laser|go|selfie"),
    "micromax" -> Map(
      "Mobiles" -> "[a-z]+[0-9]+[a-z]?[+]?|[0-9]+[.]?[0-9]*|[a-z]|canvas|bolt|unite|aisha|bling|joy|juice|knight|mad|ninja|nitro|selfie|superfone|yuphoria|fire|hue|beat|blade|blaze|cameo|doodle|duet|eclipse|ego|engage|fun|lite|music|pep|play|plus|power|pristine|punk|smarty|tube|turbo|viva|win|spark|entice|mmx|magnus|elanza|pace|pulse|xpress|psych|ii|hd|3d|hotknot|celkon|xl|mega|amaze"),
    "lg" -> Map(
      "Mobiles" -> "google|nexus"))

  val tokenizerMap = Map(
    "apple" -> "[ /\\,+()\"\\[\\]-]",
    "motorola" -> "[ /\\,+()\"\\[\\]-]",
    "samsung" -> "[ /\\,()\"\\[\\]-]",
    "lenovo" -> "[ \\,+()\"\\[\\]]",
    "asus" -> "[ /\\,+()\"\\[\\]-]",
    "micromax" -> "[ \\,()\"\\[\\]-]",
    "lg" -> "[ \\,()\"\\[\\]]")

  val tokenCleanUpMapOfMap = Map(
    "apple" -> LinkedHashMap(
      "WiFi|Wifi|Wi-Fi|Wi-fi" -> "wifi",
      "&" -> " ",
      "Cell " -> "cellular ",
      "Gray" -> "grey",
      "Sapce" -> "space",
      "Golden" -> "gold",
      """(\d+) ?(MP|GB|mm|cm)""" -> "$1$2 "),
    "motorola" -> LinkedHashMap(
      "&" -> "and",
      "Locorice" -> "Licorice",
      """(\d+) ?(GB|MB)""" -> "$1$2 "),
    "samsung" -> LinkedHashMap(
      "WiFi|Wifi|Wi-Fi|Wi-fi" -> "wifi",
      "Gray" -> "Grey",
      "Golden" -> "Gold",
      "&" -> "and",
      """(Edge)\+""" -> "$1 plus ",
      """(\d+) ?(GB|MB|cm|Inch|inch)|([0-9]+[.]?[0-9]*)-(inch|Inch)""" -> "$1$2 ",
      """(wifi) ?\+ ?(3G|4G)|(Tab)(\d+)""" -> "$1 $2 ",
      """(\([0-9]+[.]?[0-9]*\))""" -> "$1inch"),
    //    "samsung_laptops" -> LinkedHashMap(
    //      "[\\,()\"\\[\\]]" -> " ",
    //      "Gray" -> "Grey",
    //      "Golden" -> "Gold",
    //      "&" -> "and",
    //      """(\d+) ?(GB|MB|cm|Inch|inch)""" -> "$1$2 ",
    //      """(\([0-9]+[.]?[0-9]*\))""" -> "$1inch",
    //      """([0-9]+[.]?[0-9]*)-(inch|Inch)""" -> "$1$2"),
    "lenovo" -> LinkedHashMap(
      "WiFi|Wifi|Wi-Fi|Wi-fi" -> "wifi",
      """(\([0-9][0-9]*\))""" -> "$1inch",
      "&" -> "and",
      """(\d+) ?(GB|MB|cm)|([0-9]+[.]?[0-9]*)-(inch|Inch)""" -> "$1$2 "),
    "asus" -> LinkedHashMap(
      "Gray" -> "grey",
      "Golden" -> "gold",
      "with" -> " ",
      """([0-9]+[.]?[0-9]*) ?(GB|MB|cm|inch|GHz)""" -> "$1$2 "),
    "micromax" -> LinkedHashMap(
      "Micomax|Micormax|Micromac" -> "micromax",
      "Express|Xpress" -> "xpress ",
      "Gray" -> "grey",
      "Golden" -> "gold",
      "Sliver" -> " silver ",
      "Sliver|\\+SLIVER|\\+SILVER" -> " silver ",
      "Champange|Champaagne" -> "champagne",
      "\\+champagne|\\+Champagne" -> "champagne",
      "&" -> " ",
      """(CDMA|GSM) ?\+ ?(GSM|CDMA)""" -> "$1 $2 ",
      """([0-9]+[.]?[0-9]*) ?(GB|MB|inch)""" -> "$1$2 ",
      """( AQ|C|c|X|x)( |-){1}(\d+)""" -> "$1$3 "),
    "lg" -> LinkedHashMap(
      "" -> ""))

  val colorCleanUpMapOfMap = Map(
    "apple" -> LinkedHashMap(
      "&" -> " ",
      "Gray" -> "grey",
      "Sapce" -> "space",
      "Golden" -> "gold"),
    "motorola" -> LinkedHashMap(
      "&" -> " ",
      "Locorice" -> "licorice"),
    "samsung" -> LinkedHashMap(
      "&" -> " ",
      "Gray" -> "grey",
      "Golden" -> "gold"),
    "lenovo" -> LinkedHashMap(
      "&" -> " "),
    "asus" -> LinkedHashMap(
      "Gray" -> "grey",
      "Golden" -> "gold",
      "&" -> " "),
    "micromax" -> LinkedHashMap(
      "Gray" -> " grey ",
      "Golden" -> " gold ",
      "Sliver" -> " silver ",
      "Sliver|\\+SLIVER|\\+SILVER" -> " silver ",
      "Champange|Champaagne" -> " champagne ",
      "\\+champagne|\\+Champagne" -> " champagne ",
      "&" -> " "),
    "lg" -> LinkedHashMap(
      "Gray" -> " grey ",
      "&|:" -> " "))
}