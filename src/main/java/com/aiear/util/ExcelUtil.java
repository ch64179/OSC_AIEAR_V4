/**
 * 
 */
package com.aiear.util;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * @packageName : com.aiear.util
 * @fileName	: ExcelUtil.java
 * @author		: pcw
 * @date		: 2023. 1. 9.
 * @description	:
 * ===============================================
 * DATE				AUTHOR			NOTE
 * 2023. 1. 9.		pcw				최초 생성
 */
public class ExcelUtil {

public static final int BUFF_SIZE = 2048;
	
	public static void ExcelfileCreate(HttpServletResponse res, String titleList, String columnList, List<Map<String, Object>> dataList, String excelName) throws Exception{
		
		
		//workbook을 생성 
		XSSFWorkbook workbook=new XSSFWorkbook();
		//sheet생성 
		XSSFSheet sheet=workbook.createSheet(excelName);
		//엑셀의 행 
		XSSFRow row=null;
		//엑셀의 셀 
		XSSFCell cell=null;
		
		Font headerFont = workbook.createFont();
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		headerStyle.setFillForegroundColor(IndexedColors.OLIVE_GREEN.index);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setFont(headerFont);

		XSSFCellStyle dataStyle = workbook.createCellStyle();
		dataStyle.setBorderBottom(BorderStyle.THIN);
		dataStyle.setBorderTop(BorderStyle.THIN);
		dataStyle.setBorderLeft(BorderStyle.THIN);
		dataStyle.setBorderRight(BorderStyle.THIN);
		

		//파라미터로 넘어온 titleList를 첫번째 row에 set.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
		row = sheet.createRow(0);
//		sheet.autoSizeColumn(0);
		String[] titleArr=StringUtil.split(titleList, "|");
		for (int i = 0; i < titleArr.length-1; i++){
			cell = row.createCell(i);
			cell.setCellValue(titleArr[i]);
			cell.setCellStyle(headerStyle);
		}
		
		//파라미터로 넘어온 columnList를 배열로 만든다
		String[] columnArr=StringUtil.split(columnList, "|");

		//파라미터로 넘어온 data들을 cell에 set
		for(int i=0; i<dataList.size(); i++){
//			sheet.autoSizeColumn(i);
			row = sheet.createRow(i+1);
			for(int j=0; j<columnArr.length-1; j++){
				cell = row.createCell(j);
				Object val = dataList.get(i).get(columnArr[j]);
				if(val != null){
					cell.setCellValue(val.toString());					
				}
				cell.setCellStyle(dataStyle);
			}
		}

		for(int i=0; i<columnArr.length-1; i++){
			sheet.autoSizeColumn((short)i);
			sheet.setColumnWidth(i, (sheet.getColumnWidth(i))+1024 );  // 윗줄만으로는 컬럼의 width 가 부족하여 더 늘려야 함.
		}
		res.setHeader("Set-Cookie", "fileDownload=true; path=/");
		
		res.setHeader("Content-Disposition","attachment;filename=" + (URLEncoder.encode((excelName+".xlsx"),"UTF-8")));
		res.setHeader("Content-Type", "application/vnd.ms-excel; charset=MS949");
		res.setHeader("Content-Description", "JSP Generated Data"); 
		res.setHeader("Content-Transfer-Encoding", "binary;"); 
		res.setHeader("Pragma", "no-cache;"); 
		res.setHeader("Expires", "-1;");
		ServletOutputStream sw = res.getOutputStream();
		workbook.write(sw);
		sw.close();
		
		
		return ;
	}
	
}
