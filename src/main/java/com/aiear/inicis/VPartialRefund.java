package com.aiear.inicis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.aiear.util.AES128;
import com.aiear.util.SHA512;

public class VPartialRefund {

	public static void main(String[] args) throws Exception {

		SHA512 sha512 = new SHA512();
		AES128 aes128 = new AES128();
		Date date_now = new Date(System.currentTimeMillis());
		SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyyMMddHHmmss");

		//step1. 요청을 위한 파라미터 설정
		String key = "ItEQKi3rY7uvDS8l"; 
		String iv = "HYb3yQ4f65QL89==";
		String type = "PartialRefund";
		String paymethod = "Vacct";
		String timestamp = fourteen_format.format(date_now);
		String clientIp = "111.222.333.889";
		String mid = "INIpayTest";
		String tid = ""; 			
		String msg = "가상계좌 부분환불요청";
		String price = "";												
		String confirmPrice = "";									
		String refundBankCode = "";
		String refundAcctName = "";
		String refundAcctNum = "";
		
		// AES Encryption
		String enc_refundAcctNum = aes128.encAES(refundAcctNum, key, iv);
		
		// Hash Encryption
		String data_hash = key + type + paymethod + timestamp + clientIp + mid + tid + price + confirmPrice + enc_refundAcctNum ;
		String hashData = sha512.hash(data_hash);
		
		// Request URL
		String apiUrl = "https://iniapi.inicis.com/api/v1/refund";

		Map<String, Object> resultMap = new HashMap<String, Object>();

		resultMap.put("type", type);
		resultMap.put("paymethod", paymethod);
		resultMap.put("timestamp", timestamp);
		resultMap.put("clientIp", clientIp);
		resultMap.put("mid", mid);
		resultMap.put("tid", tid);
		resultMap.put("msg", msg);
		resultMap.put("price", price);
		resultMap.put("confirmPrice", confirmPrice);
		resultMap.put("refundAcctNum", enc_refundAcctNum);
		resultMap.put("refundBankCode", refundBankCode);
		resultMap.put("refundAcctName", refundAcctName);
		resultMap.put("hashData", hashData);
		
		StringBuilder postData = new StringBuilder();
		for(Map.Entry<String, Object> params: resultMap.entrySet()) {
			
			if(postData.length() != 0) postData.append("&");
			try {
				postData.append(URLEncoder.encode(params.getKey(), "UTF-8"));
				postData.append("=");
				postData.append(URLEncoder.encode(String.valueOf(params.getValue()), "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//step2. key=value 로 post 요청
		try {
			URL url = new URL(apiUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			if (conn != null) {
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
				conn.setRequestMethod("POST");
				conn.setDefaultUseCaches(false);
				conn.setDoOutput(true);
				
				if (conn.getDoOutput()) {
					conn.getOutputStream().write(postData.toString().getBytes("UTF-8"));
					conn.getOutputStream().flush();
					conn.getOutputStream().close();
				}

				conn.connect();
				
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
					
					//step3. 요청 결과
					System.out.println(br.readLine());
					br.close();
				}

		}catch(Exception e ) {
			e.printStackTrace();
		} 
	}
}

