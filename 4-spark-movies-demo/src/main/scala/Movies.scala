import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import scala.util.Properties

import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating

object Movies {
    val usage = """
Usage: ./movies <userId> (try 100 for instance)
    """

    def main(args: Array[String]): Unit = {
        if (args.length == 0) {
            println(usage)
            return
        }
        val master = Properties.envOrElse("MASTER", "local")

        // Context initialization (spark shell does that for us)
        val sc = new SparkContext(master, "movies")

        val rawData = sc.textFile("example-movies/src/main/resources/u.data")
        val rawRatings = rawData.map(_.split("\t").take(3))
        val ratings = rawRatings.map { case Array(user, movie, rating) => Rating(user.toInt, movie.toInt, rating.toDouble) }

        // Model training with ALS
        val model = ALS.train(ratings, 50, 10, 0.01)

        // We'll build K recommendations for user userId 
        val userId = args(0).toInt
        val K = 10
        val topKRecs = model.recommendProducts(userId, K)

        // Getting the titles for a pretty output
        val movies = sc.textFile("example-movies/src/main/resources/u.item")
        val titles = movies.map(line => line.split("\\|").take(2)).map(array => (array(0).toInt,array(1))).collectAsMap()

        // Getting userId movies
        val moviesForUser = ratings.keyBy(_.user).lookup(userId)
        println("=> User " + userId + " rated " + moviesForUser.size + " movies\n")

        // Getting userId's best rated movies
        println("=> User's best rated")
        moviesForUser.sortBy(-_.rating).take(K).map(rating => (titles(rating.product), rating.rating)).foreach(println)

        // Getting userId's movie recommendations
        println("\n=> Recommended movies")
        topKRecs.map(rating => (titles(rating.product), rating.rating)).foreach(println)
    }
}
