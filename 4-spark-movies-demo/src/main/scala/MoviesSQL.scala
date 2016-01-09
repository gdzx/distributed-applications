import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.sql._
import scala.util.Properties

import org.apache.spark.mllib.recommendation.Rating

object MoviesSQL {
    def main(args: Array[String]): Unit = {
        val master = Properties.envOrElse("MASTER", "local")

        // Context initialization (spark shell does that for us)
        val sc = new SparkContext(master, "movies")
        val sqlContext = new org.apache.spark.sql.SQLContext(sc)
        import sqlContext.implicits._

        val rawData = sc.textFile("example-movies/src/main/resources/u.data")
        val rawRatings = rawData.map(_.split("\t").take(3))
        val ratings = rawRatings.map { case Array(user, movie, rating) => Rating(user.toInt, movie.toInt, rating.toDouble) }

        // Let's play with some sql
        ratings.toDF().registerTempTable("ratings")
        val result = sqlContext.sql("SELECT * from ratings LIMIT 10")
        result.foreach(println)
    }
}
