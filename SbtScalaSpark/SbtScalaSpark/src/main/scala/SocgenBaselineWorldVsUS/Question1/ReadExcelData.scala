package SocgenBaselineWorldVsUS.Question1

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFSheet
import scala.collection.mutable.HashMap

class ReadExcelData {
  def readSheet(sheet: XSSFSheet): HashMap[String, String] = {
    val header = sheet.getHeader
    val rowsCount = sheet.getLastRowNum()
    var x = 0
    var c = 0
    var hashMap: HashMap[String, String] = HashMap()
    var hashMapForUSA: HashMap[String, String] = HashMap()
    while (x <= rowsCount) {
      val row = sheet.getRow(x)
      if (row != null) {
        //println(s"row.getCell(x):  x: $x    ${row.getCell(0)}")
        val tempCell = row.getCell(0)
        if (tempCell != null) {
          tempCell.setCellType(CellType.STRING)
          var y = x + 1
          if (tempCell.getStringCellValue.toLowerCase == "year") {
            while (y <= x + 13) {
              val row1 = sheet.getRow(y)
              val year = row1.getCell(0)
              val harvest = row1.getCell(1)
              year.setCellType(CellType.STRING)
              harvest.setCellType(CellType.STRING)
              if (hashMap.contains(year.getStringCellValue)) {
                val harvestValue = hashMap(year.getStringCellValue)
                //println(s"x: $x y: $y year: $year harvest: $harvest")
                if (harvest.getStringCellValue.trim != "--") {
                  val totalHarvestValue = harvestValue.toDouble + harvest.getStringCellValue.toDouble
                  hashMap += (year.getStringCellValue.trim -> totalHarvestValue.toString.trim)
                }
              } else {
                hashMap += (year.getStringCellValue.trim -> harvest.getStringCellValue.trim)
              }
              y = y + 1
            }
            x = x + 13
          }

          var z = x + 3
          if (tempCell.getStringCellValue.toLowerCase.contains("usa")) {
            //println("---------------------yes usa der--------------")
            c = c + 1
            while (z <= x + 15) {
              val rowUS = sheet.getRow(z)
              val yearUS = rowUS.getCell(0)
              val harvestUS = rowUS.getCell(1)
              yearUS.setCellType(CellType.STRING)
              harvestUS.setCellType(CellType.STRING)
              hashMapForUSA += (yearUS.getStringCellValue.trim -> harvestUS.getStringCellValue.trim)
              z = z + 1
            }
          }
        }
      }
      x = x + 1
    }
    var hashMapNew: HashMap[String, String] = HashMap()
    hashMapForUSA ++ hashMap.map {
      case (k, v) =>
        if (hashMapForUSA(k).trim != "--" && v.trim != "--") {
          val percentValue = (hashMapForUSA(k).toDouble / v.toDouble) * 100
          if (k.contains("/")) {
            hashMapNew += (k.substring(0, k.indexOf("/")).trim -> s"$v|${percentValue.toString.trim}")
          } else {
            hashMapNew += (k.trim -> s"$v|${percentValue.toString.trim}")
          }
        }
    }
    hashMapNew.map {
      case (key, value) => println(s"keyNew $key  -> valueNew $value")
    }
    hashMapNew
  }
}
