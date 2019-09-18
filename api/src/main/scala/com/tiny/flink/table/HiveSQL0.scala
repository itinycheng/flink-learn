package com.tiny.flink.table

import org.apache.flink.api.scala.{ExecutionEnvironment, _}
import org.apache.flink.table.api.scala._
import org.apache.flink.table.api.scala.BatchTableEnvironment
import org.apache.flink.table.catalog.hive.HiveCatalog
import org.apache.flink.types.Row

/**
 * cmd: flink run -m yarn-cluster -yn 1 -ynm sql-test -c com.tiny.flink.table.HiveSQL1 -d -yq example.jar
 *
 * should be executed on server side
 */
object HiveSQL0 {

  def main(args: Array[String]): Unit = {

    val env = ExecutionEnvironment.getExecutionEnvironment
    val tableEnv = BatchTableEnvironment.create(env)
    tableEnv.registerCatalog("hive", new HiveCatalog("hive",
      "analytics", "/home/hadoop/apache/hive/latest/conf", "3.1.1"))
    val table = tableEnv.sqlQuery("select id, name from hive.analytics.tmp_sp_serde")
    table.toDataSet[Row].map(row => {
      val str = row.toString
      logger.info("print item: " + str)
      str
    }).print()
  }
}
