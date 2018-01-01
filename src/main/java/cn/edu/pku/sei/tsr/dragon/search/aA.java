package cn.edu.pku.sei.tsr.dragon.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class aA {


    public static void main(String[] args) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        short rowNum = 0;
        short colNum = 0;

//        HSSFRow row = sheet.createRow(rowNum++);
//        HSSFCell cell = row.createCell(colNum);
//        cell.setCellValue("<data>test data</data>");

        try {
            FileOutputStream out = new FileOutputStream(new File("Excel.xls"));
            workbook.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}