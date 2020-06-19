package SocgenBaselineWorldVsUS.Question2

import java.io.File
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.spark.sql.SparkSession

object DisplayReportUsingSpark {
  def main(args: Array[String]): Unit = {
    val readObj = new ReadExcelUsingSpark
    val spark = SparkSession.builder().appName("").master("local[*]").getOrCreate()

    val sqlc = spark.sqlContext

    val baselineWorldFile = new File("C:\\Users\\Alka\\IdeaProjects\\SbtScalaSpark\\src\\main\\scala\\SocgenBaselineWorldVsUS\\InternationalBaseline2019.xls")
    //readExcel(baselineWorldFile.toString, spark, "Wheat").show(false)
    if (baselineWorldFile.exists()) {
      val workBook = new XSSFWorkbook(baselineWorldFile)
      val totalNumberOfSheets = workBook.getNumberOfSheets
      for (i <- 1 until totalNumberOfSheets) {
        println(s"READING SHEET $i")
        val sheetName = workBook.getSheetName(i)
        readObj.readExcel(baselineWorldFile.toString, spark, sheetName).show(false)
      }
    }
  }


}
