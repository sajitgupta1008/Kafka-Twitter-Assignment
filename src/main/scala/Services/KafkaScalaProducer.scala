package Services

import java.util.Properties
import Model.TweetData
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import scala.concurrent.ExecutionContext.Implicits.global

class KafkaScalaProducer {

  val props: Properties = configureProducer()
  val topic = "kafka-topic-kip"
  val producer: KafkaProducer[Nothing, String] = new KafkaProducer[Nothing, String](props)

  private def configureProducer(): Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("client.id", "ScalaProducerExample")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("acks", "all")
    props.put("retries", "0")
    props.put("batch.size", "16384")
    props.put("linger.ms", "1")
    props.put("buffer.memory", "33554432")
    props
  }

  def sendHashTags(userName: String): Unit = {

    val statuses = TwitterService.getTweets(userName)
    statuses.map { list =>
      list.foreach { hastTag =>

        val tweetData: String = JsonHandler.serializeJson(TweetData(hastTag, userName))
        val record: ProducerRecord[Nothing, String] = new ProducerRecord(topic, tweetData)
        println(tweetData)
        producer.send(record)
      }
    }

    Thread.sleep(10000)
    producer.close()
  }
}

object KafkaScalaProducer {

  def main(args: Array[String]): Unit = {
    val producer = new KafkaScalaProducer
    println("enter username")
    producer.sendHashTags(scala.io.StdIn.readLine())

  }
}