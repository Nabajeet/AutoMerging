package test.scala

import main.scala.merger.TempTest
import org.specs2._

class QuickStartSpec extends Specification {
  def is = s2"""

 This is a specification to check the Spark functioning

 The Spark Context should pass
   test1                                         $e1
   test2                                            $e2
   test3                                              $e3
                                                                 """

  def e1 = {
    val len = TempTest.getLength(Array(1, 2, 3))
    len must beEqualTo(3)
  }
  def e2 = {
    val len = TempTest.getLength(Array(1, 2, 3, 4))
    len must beEqualTo(4)

  }
  def e3 = {
    val len = TempTest.getLength(Array())
    len must beEqualTo(0)
  }
}