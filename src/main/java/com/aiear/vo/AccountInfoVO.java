package com.aiear.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

@Data
public class AccountInfoVO extends ResponseVO  {
	
	@ApiModelProperty(
			name = "AccountInfoVO",
			example = "AccountInfoVO"
	)
	
	@ApiParam(value = "병원명")
	private String hospital_nm;
	
	@ApiParam(value = "병원 사용자 ID")
	private String hospital_id;
	
	@ApiParam(value = "병원 사용자 비밀번호")
	private String hospital_pwd;
	
	@ApiParam(value = "병원 진료과목")
	private String hospital_type;
	
	@ApiParam(value = "병원 전화번호")
	private String hospital_tel_no;
	
	@ApiParam(value = "관리자 전화번호")
	private String mgr_tel_no;
	
	@ApiParam(value = "병원주소")
	private String hospital_addr;
	
	@ApiParam(value = "병원 설명")
	private String hospital_desc;
	
	
	@ApiParam(value = "정렬타입")
	private String order_type;

	@ApiParam(value = "정렬타입2")
	private String order_type2;
	
	@ApiParam(value = "페이지별 로우데이터 갯수")
	private Integer raw_cnt;
	
	@ApiParam(value = "페이지 선택")
	private Integer page_cnt;
	

	public HashMap<String, Object> beanToHmap(AccountInfoVO vo) {
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
