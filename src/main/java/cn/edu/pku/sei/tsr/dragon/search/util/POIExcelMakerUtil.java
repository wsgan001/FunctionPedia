package cn.edu.pku.sei.tsr.dragon.search.util;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class POIExcelMakerUtil {

	public File excelFile;


	public HSSFWorkbook workBook;

	public POIExcelMakerUtil(File file) throws Exception {
		this.excelFile = file;
		this.workBook = new HSSFWorkbook();
	}

	/**
	 * 写入Excel文件并关闭
	 */
	public void writeAndClose() {
		FileOutputStream fos  = null;
        try
        {
            fos = new FileOutputStream(this.excelFile);
            this.workBook.write(fos);
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }          
	}

	private static String convertString(Object value) {
		if (value == null) {
			return "";
		} else {
			return value.toString();
		}
	}
	
}