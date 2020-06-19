package SocgenBaselineWorldVsUS.Question1

import java.io.{File, FileNotFoundException, IOException}
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import scala.collection.mutable
import scala.collection.mutable.HashMap

object DisplayReport {
  def main(args: Array[String]): Unit = {
    val excelObj = new ReadExcelData
    try {
      val file = new File("C:\\Users\\Alka\\IdeaProjects\\SbtScalaSpark\\src\\main\\scala\\SocgenBaselineWorldVsUS\\InternationalBaseline2019.xls")
      println(file.toString)
      println(file.exists())
      if (file.exists()) {
        val workBook = new XSSFWorkbook(file)
        val totalNumberOfSheets = workBook.getNumberOfSheets
        var testSMap: HashMap[String, String] = HashMap()
        var arrH: Array[mutable.HashMap[String, String]] = Array()
        for (i <- 0 until totalNumberOfSheets) {
          println(s"READING SHEET $i")
          val sheet = workBook.getSheetAt(i)
          val sheetName = workBook.getSheetName(i)
          testSMap = testSMap ++ excelObj.readSheet(workBook.getSheetAt(i)).map { case (k, v) => k -> (v ++ testSMap.getOrElse(k, "")) }
        }
        testSMap.map {
          case (key, value) => println(s"key $key  -> value $value")
        }
      }
    } catch {
      case e: FileNotFoundException => println("Couldn't find that file.")
      case e: IOException => println("Had an IOException trying to read that file")
    }
  }
}
