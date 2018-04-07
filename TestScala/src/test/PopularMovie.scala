package test

import org.apache.spark.SparkContext

object PopularMovie {
  def main(args: Array[String]){
    val sc = new SparkContext("local[*]", "PopularMovie")
    val inputFile = sc.textFile("ml-100k/u.data")
    val movieCounter = inputFile.map(x => (x.split("\t")(1), 1)).reduceByKey((x,y) => (x+y))
    val sortedPopular = movieCounter.map(x => (x._2, x._1)).sortByKey()
    
    val result = sortedPopular.collect()
    for(eachResult <- result){
      val movie = eachResult._2
      val counter = eachResult._1
      println(s"movieID:$movie - counter:$counter")
    }
    
  }
}