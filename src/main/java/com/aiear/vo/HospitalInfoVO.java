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
public class HospitalInfoVO extends ResponseVO  {
	
	@ApiModelProperty(
			name = "HospitalInfoVO",
			example = "HospitalInfoVO"
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
	
	@ApiParam(value = "병원 이미지 파일")
	private MultipartFile img_file;

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
	

	
	@ApiParam(value = "활동여부")
	private String use_yn;
	
	@ApiParam(value = "신규여부")
	private String new_yn;
	
	@ApiParam(value = "진료여부")
	private String clinic_yn;
	
	@ApiParam(value = "점심여부")
	private String lunch_yn;
	
	
	
	@ApiParam(value = "요일")
	private String day_of_week;
	
	@ApiParam(value = "진료 시작 시간")
	private String c_strt_tm;
	
	@ApiParam(value = "진료 종료 시간")
	private String c_end_tm;
	
	@ApiParam(value = "점심 시작 시간")
	private String l_strt_tm;
	
	@ApiParam(value = "점심 종료 시간")
	private String l_end_tm;
	
	
	private String user_salt;			// 랜덤 20자리 SALT
	
	//이미지 삽임 전용
	private byte[] img_file_byte;
	
	public HashMap<String, Object> beanToHmap(HospitalInfoVO vo) {
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
