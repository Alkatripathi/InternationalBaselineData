package SocgenBaselineWorldVsUS.Question2

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, lit, row_number}
import scala.collection.mutable.HashMap

class ReadExcelUsingSpark {
  def readExcel(file: String, spark: SparkSession, sheetName: String): DataFrame = {
    val cropDF = spark.sqlContext.read
      .format("com.crealytics.spark.excel")
      .option("sheetName", sheetName)
      .option("useHeader", "false")
      .option("treatEmptyValuesAsNulls", "false")
      .option("inferSchema", "false")
      .option("location", file)
      .option("addColorColumns", "False")
      .load(file)

    import spark.implicits._
    val requiredDF = cropDF.select("_c0", "_c1")
      .withColumnRenamed("_c0", "col1")
      .withColumnRenamed("_c1", "col2")

    val combinedDF = requiredDF.withColumn("test", lit("ABC"))

    val resultDF = combinedDF.select(
      col("col1"), col("col2"), row_number().over(
        Window.partitionBy(col("test")).orderBy(col("test"))
      ).alias("id")
    )

    resultDF.createOrReplaceTempView("crop")

    println("===============iterate===============")
    var dateRow = ""
    var cropDataRow = ""
    var country = ""
    var id = ""
    var count = 0
    var hashMapTotal: HashMap[String, String] = HashMap()
    var countryWiseWithTotalDataDF = Seq(("", "")).toDF("col1", "col2")
    var usaResultDF = Seq(("", "")).toDF("col1", "col2")

    for (row <- resultDF.rdd.collect) {
      val completeRowAsString = row.mkString(",").trim
      dateRow = completeRowAsString.split(",")(0).trim
      cropDataRow = completeRowAsString.split(",")(1).trim

      if (dateRow.toLowerCase == "year") {
        println("*************inside if*****************")
        val tempVariable1 = id.toInt + 1
        val CountryWiseDf = resultDF
          .select("col1", "col2")
          .where($"id" > tempVariable1)
          .where($"id" < tempVariable1 + 14)

        println("========CountryWiseDf==========")
        countryWiseWithTotalDataDF = countryWiseWithTotalDataDF.union(CountryWiseDf)
      }

      id = completeRowAsString.split(",")(2)
      if (dateRow.toUpperCase.startsWith("USA")) {
        val i = id.toInt + 2
        val j = i + 14
        usaResultDF = resultDF
          .select("col1", "col2")
          .where($"id" > i)
          .where($"id" < j)
          .orderBy("col1")
        //usaResultDF.show(false)
      }
    }

    countryWiseWithTotalDataDF.createOrReplaceTempView("xyza")
    val countryWiseWithTotalDataDF1 = spark.sqlContext
      .sql("select col1,sum(col2) as col2 from xyza where col2 is not null group by col1 order by col1")
    //xyza1.show(false)
    val countryWiseWithTotalDataDFFinal = countryWiseWithTotalDataDF1.filter($"col2".isNotNull)
    //xyz2.show(false)
    usaResultDF.createOrReplaceTempView("usa")
    countryWiseWithTotalDataDFFinal.createOrReplaceTempView("world")

    val joinedDf = spark.sqlContext
      .sql(s"select substr(w.col1,1,5) as col1, w.col2, ((u.col2/w.col2)*100) as col3 from world w inner join usa u on w.col1 = u.col1 order by w.col1")

    joinedDf
  }
}
