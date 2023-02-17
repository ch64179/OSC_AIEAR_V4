package com.aiear.inicis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import org.supercsv.cellprocessor.ParseInt;

import com.aiear.dao.HospitalMngDAO;
import com.aiear.dao.InicisDAO;
import com.aiear.util.AES128;
import com.aiear.util.SHA512;
import com.aiear.vo.HospitalInfoVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inicis.std.util.HttpUtil;
import com.inicis.std.util.ParseUtil;
import com.inicis.std.util.SignatureUtil;


@Controller
@RequestMapping("/inicis/*")
@RequiredArgsConstructor
public class InicisPayCont {
	
	/** 로그 처리용 개체 선언 */
	protected Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private InicisDAO inicisDAO;
	
	@Autowired
	private HospitalMngDAO hsptDAO;
	
	
	
	@GetMapping("/index")
	public String hello() {
		return "index";
	}
	
	@GetMapping("/req")
	public String req() {
		
		return "INIstdpay_pc_req";
	}
	
	@PostMapping("/ret")
	public String ret(HttpServletRequest request, HttpServletResponse response) {
		
		return "INIstdpay_pc_return";
	}
	
	@GetMapping("/close")
	public String close() {
		return "close";
	}
	
	@GetMapping("/success")
	public String success(ModelMap model
			, HttpServletRequest request
			, HttpServletResponse response) throws Exception {
		
		Map<String, String> resultMap = new HashMap<String, String>();

		request.setCharacterEncoding("UTF-8");

		Map<String,String> paramMap = new Hashtable<String,String>();

		Enumeration elems = request.getParameterNames();

		String temp = "";

		while(elems.hasMoreElements())
		{
			temp = (String) elems.nextElement();
			paramMap.put(temp, request.getParameter(temp));
		}
		
		System.out.println("SUCCESS ParamMap : "+ paramMap.toString());
		
		return "payInfoReturn";
	}
	
	@GetMapping("/fail")
	public String fail(ModelMap model
			, HttpServletRequest request
			, HttpServletResponse response) throws Exception {
		
		Map<String, String> resultMap = new HashMap<String, String>();

		request.setCharacterEncoding("UTF-8");

		Map<String,String> paramMap = new Hashtable<String,String>();

		Enumeration elems = request.getParameterNames();

		String temp = "";

		while(elems.hasMoreElements())
		{
			temp = (String) elems.nextElement();
			paramMap.put(temp, request.getParameter(temp));
		}
		
		System.out.println("FAIL ParamMap : "+ paramMap.toString());
		
		return "payFailReturn";
	}
	
	
	@GetMapping(value = "payInfo/{hospital_id}.do")
	public String payInfo(
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String hospital_id,
			@RequestParam(value = "goodsname", required = true) String goodsname,
			@RequestParam(value = "sug_cnt", required = true) String sug_cnt,
			@RequestParam(value = "ins_cnt", required = true) String ins_cnt)
		throws Exception {
		
		//Total 결제 가격 정보(2023.01.29)
		List<Map<String, Object>> goodsList = hsptDAO.getGoodsList();
		Integer total = 0;
		Integer sug_price = 0;
		Integer ins_price = 0;
		
		for(Map<String, Object> good : goodsList){
			String cd = good.get("cd").toString();
			Integer gPrice = Integer.parseInt(good.get("cd_attr_1").toString());
			switch (cd) {
			case "SUG":
				sug_price = gPrice;
				total += gPrice * Integer.parseInt(sug_cnt);
				break;
			case "INS":
				ins_price = gPrice;
				total += gPrice * Integer.parseInt(ins_cnt);
				break;
			default:
				break;
			}
		}
		
		String mid					= "INIpayTest";		                    // 상점아이디					
		String signKey			    = "SU5JTElURV9UUklQTEVERVNfS0VZU1RS";	// 웹 결제 signkey
		
		String mKey = SignatureUtil.hash(signKey, "SHA-256");

		String timestamp			= SignatureUtil.getTimestamp();			// util에 의해서 자동생성
		String orderNumber			= mid+"_"+SignatureUtil.getTimestamp();	// 가맹점 주문번호(가맹점에서 직접 설정)
		String price				= String.valueOf(total);				// 상품가격(특수기호 제외, 가맹점에서 직접 설정)

		String cardNoInterestQuota = "11-2:3:,34-5:12,14-6:12:24,12-12:36,06-9:12,01-3:4";
		String cardQuotaBase = "2:3:4:5:6:11:12:24:36";

		Map<String, String> signParam = new HashMap<String, String>();

		signParam.put("oid", orderNumber);
		signParam.put("price", price);
		signParam.put("timestamp", timestamp);

		String signature = SignatureUtil.makeSignature(signParam);
		String siteDomain = request.getRequestURL().toString();
		
		//병원 정보 조회
		HospitalInfoVO hsptInfoVO = new HospitalInfoVO();
		hsptInfoVO.setHospital_id(hospital_id);
		Map<String, Object> hsptInfo = hsptDAO.getHospitalDetail(hsptInfoVO);
		
		model.addAttribute("buyername", hospital_id);
		model.addAttribute("buyertel", hsptInfo.get("mgr_tel_no"));
		model.addAttribute("buyeremail", hospital_id + "@test.com");
		
		model.addAttribute("mid", mid);
		model.addAttribute("oid", orderNumber);
		model.addAttribute("price", price);
		model.addAttribute("timestamp", timestamp);
		model.addAttribute("signature", signature);
		model.addAttribute("mKey", mKey);
		model.addAttribute("cardQuotaBase", cardQuotaBase);
		model.addAttribute("cardNoInterestQuota", cardNoInterestQuota);
		model.addAttribute("siteDomain", siteDomain);
		model.addAttribute("goodname", goodsname);
		
		//상품 개수 추가 (2023.01.29)
		model.addAttribute("sugCnt", sug_cnt);
		model.addAttribute("insCnt", ins_cnt);
		model.addAttribute("sugIndPrice", sug_price);
		model.addAttribute("insIndPrice", ins_price);
		
		model.addAttribute("returnUrl", "http://103.22.220.93:8080/inicis/payAfter.do?sugCnt=" + sug_cnt + "&insCnt=" + ins_cnt + "&sugIndPrice=" + sug_price + "&insIndPrice=" + ins_price + "&price=" + price);
		model.addAttribute("closeUrl", "http://103.22.220.93:8080/inicis/close");
			
	    return "payInfo";
	}
	
	
	@GetMapping(value = "payInfoMap/{hospital_id}.do")
	public @ResponseBody Map<String, Object> payInfoMap(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String hospital_id)
		throws Exception {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		String mid					= "INIpayTest";		                    // 상점아이디					
		String signKey			    = "SU5JTElURV9UUklQTEVERVNfS0VZU1RS";	// 웹 결제 signkey
		
		String mKey = SignatureUtil.hash(signKey, "SHA-256");

		String timestamp			= SignatureUtil.getTimestamp();			// util에 의해서 자동생성
		String orderNumber			= mid+"_"+SignatureUtil.getTimestamp();	// 가맹점 주문번호(가맹점에서 직접 설정)
		String price				= "1000";								// 상품가격(특수기호 제외, 가맹점에서 직접 설정)

		String cardNoInterestQuota = "11-2:3:,34-5:12,14-6:12:24,12-12:36,06-9:12,01-3:4";
		String cardQuotaBase = "2:3:4:5:6:11:12:24:36";

		Map<String, String> signParam = new HashMap<String, String>();

		signParam.put("oid", orderNumber);
		signParam.put("price", price);
		signParam.put("timestamp", timestamp);

		String signature = SignatureUtil.makeSignature(signParam);
		String siteDomain = request.getRequestURL().toString();
		
		//병원 정보 조회
		HospitalInfoVO hsptInfoVO = new HospitalInfoVO();
		hsptInfoVO.setHospital_id(hospital_id);
		Map<String, Object> hsptInfo = hsptDAO.getHospitalDetail(hsptInfoVO);
		
		data.put("buyername", hospital_id);
		data.put("buyertel", hsptInfo.get("mgr_tel_no"));
		data.put("buyeremail", hospital_id + "@test.com");
		
		data.put("mid", mid);
		data.put("oid", orderNumber);
		data.put("price", price);
		data.put("timestamp", timestamp);
		data.put("signature", signature);
		data.put("mKey", mKey);
		data.put("cardQuotaBase", cardQuotaBase);
		data.put("cardNoInterestQuota", cardNoInterestQuota);
		data.put("siteDomain", siteDomain);
		
		data.put("goodname", "추천 병원");
		data.put("version", "1.0");
		data.put("currency", "WON");
		data.put("returnUrl", "http://103.22.220.93:8080/inicis/payAfter.do");
		data.put("closeUrl", "");
		data.put("acceptmethod", "ARDPOINT:HPP(1):no_receipt:va_receipt:vbanknoreg(0):below1000");
			
	    return data;
	}
	
	
	@PostMapping(value = "payAfter.do")
	public RedirectView payAfter(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String successUrl = "payInfoReturn";
//		String errorUrl = "payFailReturn";
		String successUrl = "/inicis/success";
		String errorUrl = "/inicis/fail";
		String returnUrl = successUrl;
		
		Map<String, String> resultMap = new HashMap<String, String>();

		//#############################
		// 인증결과 파라미터 일괄 수신
		//#############################
		request.setCharacterEncoding("UTF-8");

		Map<String,String> paramMap = new Hashtable<String,String>();

		Enumeration elems = request.getParameterNames();

		String temp = "";

		while(elems.hasMoreElements())
		{
			temp = (String) elems.nextElement();
			paramMap.put(temp, request.getParameter(temp));
		}
		
		System.out.println("paramMap : "+ paramMap.toString());
		
		
		if("0000".equals(paramMap.get("resultCode"))){
			
			System.out.println("####인증성공/승인요청####");
			
			//############################################
			// 1.전문 필드 값 설정(***가맹점 개발수정***)
			//############################################
			
			String mid 		= paramMap.get("mid");
			String signKey	= "SU5JTElURV9UUklQTEVERVNfS0VZU1RS";	// 웹 결제 signkey
			String timestamp= SignatureUtil.getTimestamp();
			String charset 	= "UTF-8";
			String format 	= "JSON";
			String authToken= paramMap.get("authToken");
			String authUrl	= paramMap.get("authUrl");
			String netCancel= paramMap.get("netCancelUrl");	
			String merchantData = paramMap.get("merchantData");
			
			//#####################
			// 2.signature 생성
			//#####################
			Map<String, String> signParam = new HashMap<String, String>();

			signParam.put("authToken",	authToken);		// 필수
			signParam.put("timestamp",	timestamp);		// 필수

			// signature 데이터 생성 (모듈에서 자동으로 signParam을 알파벳 순으로 정렬후 NVP 방식으로 나열해 hash)
			String signature = SignatureUtil.makeSignature(signParam);
			String price = paramMap.get("price");

			//#####################
			// 3.API 요청 전문 생성
			//#####################
			Map<String, String> authMap = new Hashtable<String, String>();

			authMap.put("mid"			,mid);			// 필수
			authMap.put("authToken"		,authToken);	// 필수
			authMap.put("signature"		,signature);	// 필수
			authMap.put("timestamp"		,timestamp);	// 필수
			authMap.put("charset"		,charset);		// default=UTF-8
			authMap.put("format"		,format);	    // default=XML
			authMap.put("price"			,price);

			HttpUtil httpUtil = new HttpUtil();

			try{
				//#####################
				// 4.API 통신 시작
				//#####################

				String authResultString = "";

				authResultString = httpUtil.processHTTP(authMap, authUrl);
				
				//############################################################
				//5.API 통신결과 처리(***가맹점 개발수정***)
				//############################################################
				
				String test = authResultString.replace(",", "&").replace(":", "=").replace("\"", "").replace(" ","").replace("\n", "").replace("}", "").replace("{", "");
				
							
				resultMap = ParseUtil.parseStringToMap(test); //문자열을 MAP형식으로 파싱
				
				
			  // 수신결과를 파싱후 resultCode가 "0000"이면 승인성공 이외 실패

			  //throw new Exception("강제 Exception");
			} catch (Exception ex) {

				//####################################
				// 실패시 처리(***가맹점 개발수정***)
				//####################################

				//---- db 저장 실패시 등 예외처리----//
				System.out.println(ex);

				//#####################
				// 망취소 API
				//#####################
				String netcancelResultString = httpUtil.processHTTP(authMap, netCancel);	// 망취소 요청 API url(고정, 임의 세팅 금지)

				System.out.println("## 망취소 API 결과 ##");

				// 취소 결과 확인
				System.out.println("<p>"+netcancelResultString.replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"</p>");
				
				returnUrl = errorUrl;
			}

		}else{
			resultMap.put("resultCode", paramMap.get("resultCode"));
			resultMap.put("resultMsg", paramMap.get("resultMsg"));
			returnUrl = errorUrl;
		}
		
		// 결제요청 결과이력 적재
		int paySeq = inicisDAO.insertInicisPayHst(resultMap);
		resultMap.put("paySeq", String.valueOf(paySeq));
		
		// 결제 성공시 계정별 결제 매핑테이블 적재
		if("0000".equals(resultMap.get("resultCode"))){
			int sugCnt = Integer.parseInt(paramMap.get("sugCnt"));
			int insCnt = Integer.parseInt(paramMap.get("insCnt"));
			
			// 01) 추천병원 등록
			for(int i=0; i<sugCnt; i++){
				String price = paramMap.get("sugIndPrice");
				resultMap.put("goodstype", "SUG");
				resultMap.put("price", price);
				inicisDAO.insertInicisUserPayMapp(resultMap);
			}
			
			
			// 02) 검사이용권 등록
			for(int j=0; j<insCnt; j++){
				String price = paramMap.get("insIndPrice");
				resultMap.put("goodstype", "INS");
				resultMap.put("price", price);
				inicisDAO.insertInicisUserPayMapp(resultMap);
			}
			
		} else {
			returnUrl = errorUrl;
		}
		
		model.put("resultMap", resultMap);
		
		return new RedirectView(returnUrl);
	}
	
	
	@PostMapping(value = "refund.do")
	public @ResponseBody Map<String, Object> refund(
			@RequestBody ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		SHA512 sha512 = new SHA512();
		AES128 aes128 = new AES128();
		Date date_now = new Date(System.currentTimeMillis());
		SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyyMMddHHmmss");

		String result = "";
		Map<String, Object> rsltMap = new HashMap<String, Object>();
		
		Map<String,String> paramMap = new Hashtable<String,String>();
		
		paramMap.put("seq", model.get("seq").toString());
		paramMap.put("pay_seq", model.get("pay_seq").toString());
		
		Map<String, Object> indInfo = inicisDAO.getInicisIndList(paramMap);
		
		System.out.println("SUCCESS indInfo : "+ indInfo.toString());
		
		int intConfimrPrice = Integer.parseInt(indInfo.get("totprice").toString()) - Integer.parseInt(indInfo.get("price").toString());
		
		//step1. 요청을 위한 파라미터 설정
		String key = "ItEQKi3rY7uvDS8l"; 
		String iv = "HYb3yQ4f65QL89==";
		String type = "PartialRefund";
		String paymethod = indInfo.get("paymethod").toString();
		String timestamp = fourteen_format.format(date_now);
		String clientIp = "111.222.333.889";
		String mid = indInfo.get("mid").toString();
		String tid = indInfo.get("tid").toString(); 			
		String msg = indInfo.get("goodname").toString();
		String price = indInfo.get("price").toString();												
		String confirmPrice = String.valueOf(intConfimrPrice);									
		String refundBankCode = "";
		String refundAcctName = "";
		String refundAcctNum = "";
		
		// AES Encryption
		String enc_refundAcctNum = aes128.encAES(refundAcctNum, key, iv);
		
		// Hash Encryption
		String data_hash = key + type + paymethod + timestamp + clientIp + mid + tid + price + confirmPrice + enc_refundAcctNum;
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
					
					StringBuilder sb = new StringBuilder();
					String line = null;
					while((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					//step3. 요청 결과
					System.out.println(br.readLine());
					br.close();
					
					result = sb.toString();
					
					ObjectMapper mapper = new ObjectMapper();
					rsltMap = mapper.readValue(result, Map.class);
					
					int rsltCode = conn.getResponseCode();
					rsltMap.put("rsltCode", rsltCode);
					
					
					//환불 SUCCESS CASE
					if("00".equals(rsltMap.get("resultCode"))){
						rsltMap.put("seq", model.get("seq").toString());
						rsltMap.put("pay_seq", model.get("pay_seq").toString());
						rsltMap.put("gen_by", "admin");
						
						int ref_seq = inicisDAO.insertInicisRefundHst(rsltMap);
						
						//업데이트
						String remainPrice = rsltMap.get("prtcRemains").toString();
						
						rsltMap.put("remainPrice", remainPrice);
						
						int upCnt = inicisDAO.updateInicisPayHst(rsltMap);
						int upCnt2 = inicisDAO.updateInicisUserPayMapp(rsltMap);
					}
					
				}

		}catch(Exception e ) {
			e.printStackTrace();
		} 
		
		return rsltMap;
	}
	
}
