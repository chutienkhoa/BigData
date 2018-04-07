package sparksql

import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

object Exe1 {
  def main(args: Array[String]){
    val sc = new SparkContext("local[*]","Exe1")
    
    val sqlcontext = new SQLContext(sc)
    val dfs = sqlcontext.read.json("employee.json")
    dfs.write.format("orc").saveAsTable("employee")
    dfs.show()
    dfs.select("name").show()
    dfs.filter(dfs("age") > 23).show()

  }
}