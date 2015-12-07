package main.scala.merger

object test {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val uniqueIdMap = Map(
    "apple" ->
      Map("mobiles" ->
        "[a-z]+[0-9]+[a-z]+[ ]?[a-z]+[ ]?|[a-z]*[0-9]+[.-]?[0-9]*[a-z]*|apple|air|cellular|imac|ipad|iphone|ipod|macbook|mini|plus|pro"))
                                                  //> uniqueIdMap  : scala.collection.immutable.Map[String,scala.collection.immuta
                                                  //| ble.Map[String,String]] = Map(apple -> Map(mobiles -> [a-z]+[0-9]+[a-z]+[ ]?
                                                  //| [a-z]+[ ]?|[a-z]*[0-9]+[.-]?[0-9]*[a-z]*|apple|air|cellular|imac|ipad|iphone
                                                  //| |ipod|macbook|mini|plus|pro))
  //val brand = uniqueIdMap.get("apple").get("mobiles")
  val category = uniqueIdMap.get("apple").get.get("camera")
                                                  //> category  : Option[String] = None
}