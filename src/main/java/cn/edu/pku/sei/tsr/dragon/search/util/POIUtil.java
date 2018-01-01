package cn.edu.pku.sei.tsr.dragon.search.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class POIUtil {
	
    public static void main(String[] args){

        try {
        	File file = new File("jfreechart.xls");
        	POIExcelMakerUtil xls = new POIExcelMakerUtil(file);
        	HSSFWorkbook wb = xls.workBook;
        	HSSFCellStyle style = wb.createCellStyle();
        	//style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        	style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        	style.setWrapText(true);
        	File queryFile = new File("query\\jfreechart_PQuery.txt");
            BufferedReader reader = null;        	
            reader = new BufferedReader(new FileReader(queryFile));
            String tempString = null;
            int id = 0;
            while ((tempString = reader.readLine()) != null) {
            	id++;
            	HSSFSheet sheet = wb.createSheet(""+id);
            	HSSFRow row = sheet.createRow((short)0);
            	row.createCell((short)0).setCellValue("Query:");
            	row.createCell((short)1).setCellValue(tempString);
            	Vector<String> v = LuceneUtil.searchs(tempString);
            	int postid = 0;
            	for (String s : v){
            		postid ++;
            		HSSFRow newrow = sheet.createRow((short)postid);
            		s = s.replace("<p>", "").replace("</p>", "").replace("<code>"," ").replace("</code>", " ");
            		String[] ss = s.split("\n");
            		//HSSFCellStyle st = wb.createCellStyle();
            		HSSFCellStyle st = style;
            		st.setWrapText(true); 
            		st.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            		HSSFCell cell;
            		cell = newrow.createCell((short)0);
            		cell.setCellStyle(st);
            		cell.setCellValue("title :");
            		cell = newrow.createCell((short)1);
            		cell.setCellStyle(st);
            		cell.setCellValue(ss[0].replace("&#xA;", "\n"));
            		cell = newrow.createCell((short)2);
            		cell.setCellStyle(st);
            		cell.setCellValue("question :");
            		cell = newrow.createCell((short)3);
            		cell.setCellStyle(st);
            		cell.setCellValue(ss[1].replace("&#xA;", "\n"));
            		newrow.setHeightInPoints(5*sheet.getDefaultRowHeightInPoints());  
            		postid ++;
            		newrow = sheet.createRow((short)postid);
            		newrow.setHeightInPoints(5*sheet.getDefaultRowHeightInPoints()); 
            		cell = newrow.createCell((short)0);
            		cell.setCellValue("Points : ");
            		for (int i = 2; i < ss.length; i++){
            			String str = ss[i];
//            			String sss[] = str.split(" ");
//            			str = "";
//            			String tmp = "";
//            			for (String s1 : sss){
//            				tmp = tmp + s1 + " ";
//            				if (tmp.length() > 60){
//            					str = str + tmp + "\n";
//            					tmp = "";
//            				}
//            			}
//            			str = str + tmp;
            			cell = newrow.createCell((short)(i*2-2));
            			cell.setCellValue("answer :");
            			cell.setCellStyle(st);
            			cell = newrow.createCell((short)(i*2-1));
            			cell.setCellValue(str.replace("&#xA;", "\n"));
            			cell.setCellStyle(st);
            		}

            	}

        		
            	sheet.autoSizeColumn((short)0);
            	sheet.setColumnWidth((short)1,256*30);
            	sheet.autoSizeColumn((short)2);
            	sheet.setColumnWidth((short)3,256*200);
            }
            xls.writeAndClose();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
