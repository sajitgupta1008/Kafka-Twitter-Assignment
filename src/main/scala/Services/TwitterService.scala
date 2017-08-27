package Services

import twitter4j.TwitterFactory
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object TwitterService {

  def getTweets(userName: String): Future[List[String]] = Future {

    val twitter = TwitterFactory.getSingleton
    twitter.timelines().getUserTimeline(userName).asScala.toList.flatMap {
      status => status.getHashtagEntities.toList.map(_.getText)
    }
  }
}
