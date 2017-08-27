package Services

import Model.TweetData
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

object JsonHandler {

  def serializeJson(tweetData: TweetData): String = {
    val objectMapper = new ObjectMapper() with ScalaObjectMapper
    objectMapper.registerModule(new DefaultScalaModule)
    objectMapper.writeValueAsString(tweetData)
  }

  def deserializeJson(jsonString: String): TweetData = {
    import com.fasterxml.jackson.databind.ObjectMapper
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.registerModule(DefaultScalaModule)
    mapper.readValue[TweetData](jsonString)
  }

}


