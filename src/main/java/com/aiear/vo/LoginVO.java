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
public class LoginVO {
	
	@ApiModelProperty(
			name = "LoginVO",
			example = "LoginVO"
	)
	
	@ApiParam(value = "유저명")
	private String user_nm;
	
	@ApiParam(value = "유저코드")
	private String user_code;
	
	@ApiParam(value = "유저ID")
	private String user_id;

	@ApiParam(value = "유저 모바일 번호")
	private String mobile_tel_no;
	
	@ApiParam(value = "비밀번호")
	private String user_pwd;
	
	@ApiParam(value = "임시 비밀번호")
	private String temp_pwd;
	
	@ApiParam(value = "Refresh Token")
	private String refresh_token;
	
	private String user_salt;		//비밀번호 암호화 SALT 20자리
	
	public HashMap<String, Object> beanToHmap(LoginVO vo) {
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
