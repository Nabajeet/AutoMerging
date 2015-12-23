package main.scala.merger

object test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(84); 
  println("Welcome to the Scala worksheet");$skip(201); 
  val uniqueIdMap = Map(
    "apple" ->
      Map("mobiles" ->
        "[a-z]+[0-9]+[a-z]+[ ]?[a-z]+[ ]?|[a-z]*[0-9]+[.-]?[0-9]*[a-z]*|apple|air|cellular|imac|ipad|iphone|ipod|macbook|mini|plus|pro"));System.out.println("""uniqueIdMap  : scala.collection.immutable.Map[String,scala.collection.immutable.Map[String,String]] = """ + $show(uniqueIdMap ));$skip(116); 
  //val brand = uniqueIdMap.get("apple").get("mobiles")
  val category = uniqueIdMap.get("apple").get.get("camera");System.out.println("""category  : Option[String] = """ + $show(category ));$skip(26); 
  val brandInfo="samsung";System.out.println("""brandInfo  : String = """ + $show(brandInfo ));$skip(38); 
  val brand=brandInfo.split("_").head;System.out.println("""brand  : String = """ + $show(brand ));$skip(36); 
  val cat=brandInfo.split("_").last;System.out.println("""cat  : String = """ + $show(cat ));$skip(32); 
  
  val list=List("1","2","_");System.out.println("""list  : List[String] = """ + $show(list ));$skip(37); val res$0 = 
	list.filter { x => !x.equals("_") };System.out.println("""res0: List[String] = """ + $show(res$0))}
}
