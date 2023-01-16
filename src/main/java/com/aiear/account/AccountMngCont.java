package com.aiear.account;

import io.swagger.annotations.ApiOperation;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aiear.dao.AccountMngDAO;
import com.aiear.dao.CommonDAO;
import com.aiear.dao.HospitalMngDAO;
import com.aiear.util.DateUtil;
import com.aiear.util.ExcelUtil;
import com.aiear.util.HttpUrlUtil;
import com.aiear.vo.AccountInfoVO;
import com.aiear.vo.HospitalInfoVO;
import com.aiear.vo.ResponseVO;


@RestController
@RequestMapping("/account/*")
@RequiredArgsConstructor
public class AccountMngCont {
	
	/** 로그 처리용 개체 선언 */
	protected Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private AccountMngDAO accDAO;

	@Autowired
	private CommonDAO commonDAO;
	
	@Autowired
	private HospitalMngDAO hsptDAO;
	
	@ApiOperation(value = "계정 관리 리스트 조회"
				, notes = "계정 관리 리스트 조회"
						+ "<br> ** > hospital_nm, hospital_id 없을 시 전체조회**"
						+ "<br> ** > order_type, oder_type2 2개다 있어야지 정렬**"
						+ "<br> ** > raw_cnt, page_cnt 2개다 있어야지 Paginatoin 가능**"
						+ "\n 1. hospital_nm"
						+ "<br> 	- 이름, ID LIKE 검색"
//						+ "\n 2. hospital_id"
//						+ "<br> 	- LIKE 검색"
						+ "\n 3. order_type"
						+ "<br> 	- Default : 가입일(GEN_DT)"
						+ " - 활동 상태(USE_YN), 병원 이름(HOSPITAL_NM), 병원 ID(HOSPITAL_ID), 생성일(GEN_DT)"
						+ "\n 4. order_type2"
						+ "<br> 	- Default : 내림차순(DESC)"
						+ "<br>  - 오름차순(ASC), 내림차순(DESC)"
						+ "\n 5. raw_cnt"
						+ "<br> 	- 페이지별 로우데이터 갯수"
						+ "\n 6. page_cnt"
						+ "<br>  - 페이지 선택"
				)
	@GetMapping(value = "getAccountList.do")
	public @ResponseBody Map<String, Object> getAccountList(
			HttpServletRequest req,
			HttpServletResponse res,
			AccountInfoVO accInfoVO) {
		
		logger.info("■■■■■■ getAccountList / accInfoVO : {}", accInfoVO.beanToHmap(accInfoVO).toString());
		List<Map<String, Object>> accList = accDAO.getAccountList(accInfoVO);
		
		Map<String, Object> list = new HashMap<String, Object>();
		
		list.put("data", accList);
		
		accInfoVO.setRaw_cnt(null);
		accInfoVO.setPage_cnt(null);
		List<Map<String, Object>> accListSize = accDAO.getAccountList(accInfoVO);
		
		list.put("size", accListSize.size());
		
		return list;
	}
	
	
	@GetMapping(value = "exportAccountList.do")
	public void exportAccountList(
			HttpServletRequest req,
			HttpServletResponse res,
			AccountInfoVO accInfoVO
			) {
		try {
			List<Map<String, Object>> accListSize = accDAO.getAccountList(accInfoVO);
			
			String excelName = DateUtil.getToday() + "_계정관리_리스트"; 
			String titleList = "new_yn|new_yn_nm|use_yn|use_yn_nm|hospital_nm|hospital_id|hospital_pwd|gen_by|gen_dt|mdfy_by|mdfy_dt|user_type";
			String columnList = "new_yn|new_yn_nm|use_yn|use_yn_nm|hospital_nm|hospital_id|hospital_pwd|gen_by|gen_dt|mdfy_by|mdfy_dt|user_type";
			
			ExcelUtil.ExcelfileCreate(res, titleList, columnList, accListSize, excelName);
//			CsvUtil.createCSVFile(res, accListSize, titleList, columnList, excelName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res.setStatus(400);
		}
	}
			
	
	@PostMapping(value = "inferenceTest.do")
	public @ResponseBody Map<String, Object> inferenceTest(
			HttpServletRequest req,
			HttpServletResponse res,
			@RequestParam(value = "img_file", required = true) MultipartFile img_file,
			@RequestParam Integer c) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
		
			byte[] b_img_file = img_file.getBytes();
			byte[] base64 = Base64.encodeBase64(b_img_file);
			
			BufferedImage bufferedImage = ImageIO.read(img_file.getInputStream());
		    int width = bufferedImage.getWidth();
		    int height = bufferedImage.getHeight();
			
		    
		    JSONObject jsonObj = new JSONObject();
		    
		    jsonObj.put("img", new String(base64, "UTF-8"));
		    jsonObj.put("w", width);
		    jsonObj.put("h", height);
		    jsonObj.put("c", c);
			
			String url = "http://103.22.220.93:8000/inference/";
			String method = "POST";
			
			result = HttpUrlUtil.getHttpBodyDataToMap(url, method, jsonObj);
			
			logger.info("■■■■■■ 통신결과 : {}", result.toString());
			
			//success : HTTP 통신 결과
			//msg : "[FAIL] ~" 에러, 성공할 경우 없음
			//result : 결과값
			//	- Ar
			//	- Myri
			//	- Normal
			//	- Ome
			//	- Tp
			//	- Tumor
			Boolean aiInferRslt = false;
			
			//통신 결과 여부 분기점
			if((boolean) result.get("success")){
				//AI 추론 결과 분기점
				if("".equals(result.get("msg"))){
					aiInferRslt = true;
				} else {
					aiInferRslt = false;
				}
			}
			
			if(aiInferRslt){
				// DB에 적재 테이블 설계 후 추가
			}
			
		logger.info(">>>>>> result : {}", result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@ApiOperation(value = "계정 관리 비밀번호 수정"
			, notes = "계정 관리 비밀번호 수정"
					+ "\n 1. hospital_id"
					+ "<br> 	- 필수값"
					+ "\n 2. hospital_pwd"
					+ "<br>		- 선택값")
	@PostMapping(value = "updateAccountInfo/{hospital_id}.do")
	public @ResponseBody ResponseVO updateHospitalInfo(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String hospital_id,
			@RequestBody AccountInfoVO accInfoVO) {
		
		logger.info("■■■■■■ updateAccountInfo / accInfoVO : {}", accInfoVO.beanToHmap(accInfoVO).toString());
		
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rslt = new HashMap<String, Object>();
		int cnt = -1;
		
		try {
			accInfoVO.setHospital_id(hospital_id);
			
			if(accInfoVO.getHospital_id() == null || "".equals(accInfoVO.getHospital_id())) {
				rslt.put("msg", "병원 ID값이 없습니다.");
				rslt.put("val", cnt);
				rsltVO.setData(rslt);
				res.setStatus(400);
				return rsltVO;
			}
			
			
			cnt = accDAO.updateAccountInfo(accInfoVO);
			
			if(cnt < 1){ throw new Exception();}
			
			HospitalInfoVO hsptInfoVO = new HospitalInfoVO();
			hsptInfoVO.setHospital_id(accInfoVO.getHospital_id());
			cnt = cnt > 0 ? hsptDAO.insertHospitalHst(hsptInfoVO) : cnt; 
			
			rslt.put("cnt", cnt);
			rslt.put("msg", "SUCCESS");
		} catch (Exception e) {
			// TODO: handle exception
			rslt.put("msg", e.getMessage());
			rslt.put("cnt", cnt);
			rsltVO.setStatus(400);
			rsltVO.setMessage("계정관리 비밀번호 수정 실패");
			res.setStatus(400);
		}
		
		rsltVO.setData(rslt);
		
		return rsltVO;
	}
	
	
}
