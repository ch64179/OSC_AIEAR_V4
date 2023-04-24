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
public class UserInfoVO extends ResponseVO {
	
	@ApiModelProperty(
			name = "UserInfoVO",
			example = "UserInfoVO"
	)
	
	@ApiParam(value = "유저명")
	private String user_nm;
	
	@ApiParam(value = "유저코드")
	private String user_code;
	
	@ApiParam(value = "변경 유저코드")
	private String mdfy_user_code;
	
	@ApiParam(value = "가족 유저코드")
	private String family_user_code;
	
	@ApiParam(value = "유저ID")
	private String user_id;
	
	@ApiParam(value = "정렬타입")
	private String order_type;

	@ApiParam(value = "정렬타입2")
	private String order_type2;
	
	@ApiParam(value = "페이지별 로우데이터 갯수")
	private Integer raw_cnt;
	
	@ApiParam(value = "페이지 선택")
	private Integer page_cnt;
	
	@ApiParam(value = "이미지 파일")
	private MultipartFile img_file;
	
	@ApiParam(value = "유저 주소")
	private String user_addr;
	
	@ApiParam(value = "유저 가족관계 Sequence")
	private String family_seq;
	
	@ApiParam(value = "유저 가족관계")
	private String family_relation;
	
	@ApiParam(value = "아이콘 타입")
	private String icon_type;
	
	
	//이미지 삽임 전용
	private byte[] img_file_byte;
	
	public HashMap<String, Object> beanToHmap(UserInfoVO vo) {
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
