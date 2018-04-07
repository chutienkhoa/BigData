package session4

//import java.io.PrintWriter

import java.io.{BufferedWriter, File, FileWriter, PrintWriter}
import java.nio.charset.CodingErrorAction

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

import scala.io.{Codec, Source}

object PopularMovie {
  def loadMovieNames(): Map[Int, String]={
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    var movieNames: Map[Int, String] = Map()
    val lines = Source.fromFile("ml-100k/u.item").getLines()
    for(line <- lines){
      var fields = line.split('|')
      if(fields.length>1){
        movieNames+=(fields(0).toInt -> fields(1))
      }
    }
    return movieNames
  }
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc = new SparkContext("local[*]", "PopularMovie")
    val inputFile = sc.textFile("ml-100k/u.data")
    val movie = inputFile.map(x => x.split("\t")(1).toInt)
    val createKeyValueMovie = movie.map(x => (x, 1)).reduceByKey((x,y)=>(x+y)).map(x => (x._2, x._1))
    val sortedByValue = createKeyValueMovie.sortByKey(ascending = false)

    val nameDict = sc.broadcast(loadMovieNames)
    val sortedMovieWithName = sortedByValue.map(x => (nameDict.value(x._2), x._1))
    val result = sortedMovieWithName.collect()
    result.foreach(println)
//     write file
    val changeToList = sortedMovieWithName.collect().toList
    val n = changeToList.length
    val writeFile = new PrintWriter(new File("result.txt"))

    for(i <- 0 to n-1){
      val movieName = changeToList(i)._1
      val counter = changeToList(i)._2
      writeFile.write(s"MovieName:$movieName - counter:$counter\n")
    }
    writeFile.close()
  }
}
