package com.aiear.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;

import lombok.Data;

@Data
public class AnalysisVO extends ResponseVO  {
	
	@ApiModelProperty(
			name = "AnalysisVO",
			example = "AnalysisVO"
	)
	
	@ApiParam(value = "검색년도")
	private String search_year;
	
	@ApiParam(value = "유저타입")
	private String user_type;
	
	@ApiParam(value = "Month값")
	private Integer xaxis_m;
	
	@ApiParam(value = "Count값")
	private String cnt;
	
	@ApiParam(value = "정렬타입")
	private String order_type;

	@ApiParam(value = "정렬타입2")
	private String order_type2;
	
	@ApiParam(value = "페이지별 로우데이터 갯수")
	private Integer raw_cnt;
	
	@ApiParam(value = "페이지 선택")
	private Integer page_cnt;
	

	public HashMap<String, Object> beanToHmap(AnalysisVO vo) {
		HashMap<String, Object> beanAsMap = new HashMap<String, Object>();
		
		try {
			BeanInfo info = Introspector.getBeanInfo(vo.getClass());
			
			for(PropertyDescriptor fb : info.getPropertyDescriptors()){
				Method reader = fb.getReadMethod();
				//TODO: class는 제외
				if(reader != null && !"class".equals(fb.getName())){
					beanAsMap.put(fb.getName(), reader.invoke(vo));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return beanAsMap;
	}
	
}
