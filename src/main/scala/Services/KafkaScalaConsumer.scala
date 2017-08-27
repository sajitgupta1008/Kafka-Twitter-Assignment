package Services

import java.util.{Collections, Properties}
import Model.TweetData
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import scala.collection.JavaConverters._

class KafkaScalaConsumer extends CassandraProvider {

  val props: Properties = configureConsumer()
  val topic = "kafka-topic-kip"
  val consumer: KafkaConsumer[Nothing, String] = new KafkaConsumer[Nothing, String](props)
  consumer.subscribe(Collections.singletonList(topic))

  private def configureConsumer(): Properties = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("group.id", "consumer-group-1")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    props.put("auto.offset.reset", "earliest")
    props.put("session.timeout.ms", "30000")
    props
  }

  def writeHashTags(): Unit = {
    while (true) {

      val records: ConsumerRecords[Nothing, String] = consumer.poll(100)
      cassandraConn.execute(s"CREATE TABLE IF NOT EXISTS tweets (id timestamp PRIMARY KEY, hashtag text, username text) ")
      for (record <- records.asScala) {

        lazy val tweetData: TweetData = JsonHandler.deserializeJson(record.value())
        cassandraConn.execute(
          s"INSERT INTO tweets (id, hashtag, username) VALUES (dateOf(now()),'${tweetData.hastTag}', '${tweetData.userName}')")
        println(record.value())
      }
      // cassandraConn.close()
    }
  }
}

object KafkaScalaConsumer {

  def main(args: Array[String]): Unit = {
    val consumer = new KafkaScalaConsumer
    consumer.writeHashTags()
  }
}