package main.scala.merger

object test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(84); 
  println("Welcome to the Scala worksheet");$skip(201); 
  val uniqueIdMap = Map(
    "apple" ->
      Map("mobiles" ->
        "[a-z]+[0-9]+[a-z]+[ ]?[a-z]+[ ]?|[a-z]*[0-9]+[.-]?[0-9]*[a-z]*|apple|air|cellular|imac|ipad|iphone|ipod|macbook|mini|plus|pro"));System.out.println("""uniqueIdMap  : scala.collection.immutable.Map[String,scala.collection.immutable.Map[String,String]] = """ + $show(uniqueIdMap ));$skip(116); 
  //val brand = uniqueIdMap.get("apple").get("mobiles")
  val category = uniqueIdMap.get("apple").get.get("camera");System.out.println("""category  : Option[String] = """ + $show(category ))}
}
