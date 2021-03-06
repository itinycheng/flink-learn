package com.tiny.flink.table

import java.util.Properties

import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.{StreamTableEnvironment, _}
import org.apache.flink.table.api.{DataTypes, TableSchema}
import org.apache.flink.table.descriptors.{Csv, Kafka, Schema}
import org.apache.flink.types.Row

/**
 * read CSV data from kafka
 * use TableEnv.connect(...) API
 *
 **/
object KafkaSQL1 {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)
    tableEnv.connect(new Kafka()
      .version("0.8")
      .topic(activityTopic)
      .properties(kafkaProps)
      .startFromEarliest()
      .sinkPartitionerFixed()
    ).withFormat(new Csv()
      .schema(new RowTypeInfo(schema.getFieldTypes, schema.getFieldNames))
      .fieldDelimiter(','))
      .withSchema(new Schema().schema(schema))
      .inAppendMode()
      .createTemporaryTable("activity_log")
    val table = tableEnv.sqlQuery("select deviceid, productdeviceoffset from activity_log where productid = 3281358")
    table.toAppendStream[Row].print()

    println(env.getExecutionPlan)
    env.execute()
  }

  def schema: TableSchema = new TableSchema.Builder()
    .field("trackid", DataTypes.STRING())
    .field("deviceid", DataTypes.STRING())
    .field("productid", DataTypes.BIGINT())
    .field("sessionid", DataTypes.STRING())
    .field("starttime", DataTypes.BIGINT())
    .field("pageduration", DataTypes.INT())
    .field("versioncode", DataTypes.STRING())
    .field("refpagename", DataTypes.STRING())
    .field("pagename", DataTypes.STRING())
    .field("platformid", DataTypes.INT())
    .field("partnerid", DataTypes.BIGINT())
    .field("developerid", DataTypes.BIGINT())
    .field("deviceoffset", DataTypes.BIGINT())
    .field("productdeviceoffset", DataTypes.BIGINT())
    .build()

  def kafkaProps: Properties = {
    val props = new Properties()
    props.put("fetch.message.max.bytes", "33554432")
    props.put("socket.receive.buffer.bytes", "1048576")
    props.put("auto.commit.interval.ms", "10000")
    props.put("bootstrap.servers", kafkaServers)
    props.put("zookeeper.connect", kafkaZookeepers)
    props.put("group.id", consumerKafkaSQL_83)
    props
  }

}
