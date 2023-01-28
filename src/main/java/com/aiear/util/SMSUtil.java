package com.aiear.util;

import java.util.HashMap;

import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;

import org.json.simple.JSONObject;

import com.aiear.vo.SMSVO;

public class SMSUtil {
	
	public static SMSVO sendSMS(SMSVO smsVO) {
		JSONObject obj = new JSONObject();
		
	    Message coolsms = new Message(smsVO.getApi_key(), smsVO.getApi_secret());

	    // 4 params(to, from, type, text) are mandatory. must be filled
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("to", smsVO.getTo_mobile_no());	// 수신전화번호
	    params.put("from", smsVO.getFrom_mobile_no());	// 발신전화번호. 해당 계정 전화번호
	    params.put("type", smsVO.getSend_type());
	    params.put("text", smsVO.getSend_msg());
	    params.put("app_version", smsVO.getApp_version()); // application name and version

	    try {
	      obj = (JSONObject) coolsms.send(params);
	      System.out.println(obj.toString());
	      smsVO.setSms_rslt(obj.toJSONString());
	    } catch (CoolsmsException e) {
	      System.out.println(e.getMessage());
	      System.out.println(e.getCode());
	    }
	    
	    return smsVO;
	  }
    
}
