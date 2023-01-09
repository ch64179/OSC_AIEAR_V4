/**
 * 
 */
package com.aiear.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * @packageName : com.aiear.util
 * @fileName	: CsvUtil.java
 * @author		: pcw
 * @date		: 2023. 1. 9.
 * @description	:
 * ===============================================
 * DATE				AUTHOR			NOTE
 * 2023. 1. 9.		pcw				최초 생성
 */
public class CsvUtil {

	@SuppressWarnings("unchecked")
	public static void createCSVFile(HttpServletResponse response, List<?> dataList, String titleList, String columnList, String csvFileName) throws IOException {
		
		String[] titleArr=StringUtil.split(titleList, "|");
		
//		columnList = columnList.substring(0, columnList.length()-1);
		String[] columnArr=StringUtil.split(columnList, "|");
		
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", URLEncoder.encode((csvFileName+".csv"),"UTF-8"));

		response.setHeader("Set-Cookie", "fileDownload=true; path=/");
		
		response.setHeader("Content-Description", "JSP Generated Data"); 
		response.setHeader("Content-Transfer-Encoding", "binary;"); 
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");
		
		response.setContentType("text/csv; charset=UTF8");		
		response.setCharacterEncoding("UTF8");
		response.setHeader(headerKey, headerValue);
		
		
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(0xEF);   
		outputStream.write(0xBB);
		outputStream.write(0xBF);   
		
		
		Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
		writer.write('\uFEFF');
		final ICsvMapWriter csvWriter = new CsvMapWriter(writer,
				CsvPreference.STANDARD_PREFERENCE);
		
		try {
			// output header
			if ( true ) { // TODO make configurable property
				csvWriter.writeHeader(titleArr);
			}
			
			for(int i=0; i<dataList.size(); i++){
				HashMap<String, String> hmap = new HashMap<String, String>();
				for(int j=0; j<columnArr.length; j++){
					Object val = ((Map<String, Object>) dataList.get(i)).get(columnArr[j]);
					if(val != null){
						String val2 = val.toString();
						hmap.put(columnArr[j], val2);
					}
				}
				csvWriter.write(hmap, columnArr);
			}
			
		} finally {
			csvWriter.flush();
			csvWriter.close();
		}
		
		return ;
	}
	
}
