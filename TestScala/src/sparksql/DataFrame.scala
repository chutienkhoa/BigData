package sparksql

import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.log4j.Level


object DataFrame {
  case class Person(ID:Int, name:String, age:Int, numFriends:Int)
  def mapper(line: String):Person={
    val field = line.split(",")
    val person:Person = Person(field(0).toInt, field(1), field(2).toInt, field(3).toInt)
    return person
  }
  
  def main(args: Array[String]){
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val spark = SparkSession
    .builder
    .appName("SparkSQL")
    .master("local[*]")
    .getOrCreate()
    
    // convert our csv file to a dataset, using our Person case
    import spark.implicits._
    val lines = spark.sparkContext.textFile("fakefriends.csv")
    val people = lines.map(mapper).toDS().cache()
    
    println("Here is our inferred schema")
    people.printSchema()
    
    println("Let's select the name column")
    people.filter(people("age")<21).show()
    
    println("group by age")
    people.groupBy("age").count().show()
    
    println("make everyone 10 years older")
    people.select(people("name"), people("age")+10).show()
    
    spark.stop()
  }
}






