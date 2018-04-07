package test

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object WordCount {
  def main(args: Array[String]) {
    val sc = new SparkContext("local","WordCount")
    val inputFile = sc.textFile("book.txt")
    val words = inputFile.flatMap(x => x.split("\\W+")).map(x => x.toLowerCase())
    val keyValueTmp = words.map(x => (x,1)).reduceByKey((x,y)=>(x+y))
    val wordSortedByKey = keyValueTmp.map(x => (x._2, x._1)).sortByKey()
    
//    wordSortedByKey.collect()
    for(result <- wordSortedByKey){
      val word = result._2
      val count = result._1
      println(s"$word: $count")
    }
  }
}