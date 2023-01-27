package com.aiear.hospital;

import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
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

import com.aiear.dao.CommonDAO;
import com.aiear.dao.HospitalMngDAO;
import com.aiear.vo.CommonCdVO;
import com.aiear.vo.HospitalInfoVO;
import com.aiear.vo.ResponseVO;


@RestController
@RequestMapping("/hospital/*")
@RequiredArgsConstructor
public class HospitalMngCont {
	
	/** 로그 처리용 개체 선언 */
	protected Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private HospitalMngDAO hsptDAO;

	@Autowired
	private CommonDAO commonDAO;
	
	
	@ApiOperation(value = "병원 정보 리스트 조회"
				, notes = "병원 정보 리스트 조회"
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
	@GetMapping(value = "getHospitalList.do")
	public @ResponseBody Map<String, Object> getHospitalList(
			HttpServletRequest req,
			HttpServletResponse res,
			HospitalInfoVO hsptInfoVO) {
		
		logger.info("■■■■■■ getHospitalList / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
		List<Map<String, Object>> hsptList = hsptDAO.getHospitalList(hsptInfoVO);
		
		Map<String, Object> list = new HashMap<String, Object>();
		
		list.put("data", hsptList);
		
		hsptInfoVO.setRaw_cnt(null);
		hsptInfoVO.setPage_cnt(null);
		List<Map<String, Object>> hsptListSize = hsptDAO.getHospitalList(hsptInfoVO);
		
		list.put("size", hsptListSize.size());
		
		return list;
	}
	
	
	@ApiOperation(value = "병원 등록"
			, notes = "병원 등록"
					+ "\n 1. hospital_id"
					+ "<br> 	- 필수값"
					+ "\n 2. hospital_nm"
					+ "<br>		- 필수값"
					+ "\n 3. hospital_pwd"
					+ "<br>		- 필수값")
	@PostMapping(value = "insertHospitalInfo/{hospital_id}.do")
	public @ResponseBody ResponseVO insertHospitalInfo(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String hospital_id,
			@RequestBody HospitalInfoVO hsptInfoVO) {
		
		logger.info("■■■■■■ insertHospitalInfo / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
		
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rslt = new HashMap<String, Object>();
		int cnt = -1;
		
		try {
			hsptInfoVO.setHospital_id(hospital_id);
			
			if(hsptInfoVO.getHospital_id() == null || "".equals(hsptInfoVO.getHospital_id())) {
				rslt.put("msg", "병원 ID값이 없습니다.");
				rslt.put("val", cnt);
				rsltVO.setStatus(400);
				rsltVO.setData(rslt);
				res.setStatus(400);
				return rsltVO;
			}
			
			int dupCnt = hsptDAO.getHospitalDupChk(hsptInfoVO);
			if(dupCnt > 0) {
				rslt.put("cnt", cnt);
				rslt.put("msg", "FAIL / Duplication ID");
				rsltVO.setStatus(400);
				res.setStatus(400);
			} else {
				cnt = hsptDAO.insertHospitalInfo(hsptInfoVO);
				cnt = cnt > 0 ? hsptDAO.insertHospitalHst(hsptInfoVO) : cnt; 
				
				CommonCdVO cdVO = new CommonCdVO();
				cdVO.setCat_cd("DOW");
				List<CommonCdVO> dowList = commonDAO.getCommonCodeList(cdVO);
				
				for(CommonCdVO vo : dowList){
					HospitalInfoVO hsptVO = new HospitalInfoVO(); 
					hsptVO.setDay_of_week(vo.getCd().toString());
					hsptVO.setHospital_id(hsptInfoVO.getHospital_id());
					hsptDAO.insertHospitalClinic(hsptVO);
				}
					
				rslt.put("cnt", cnt);
				rslt.put("msg", "SUCCESS");
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			rslt.put("msg", e.getMessage());
			rslt.put("cnt", cnt);
			rsltVO.setStatus(400);
			rsltVO.setMessage("병원 등록 실패");
			res.setStatus(400);
		}
		
		rsltVO.setData(rslt);
		
		return rsltVO;
	}
	
	
	@ApiOperation(value = "병원 상세 정보 조회"
			, notes = "병원 상세 정보 조회"
					+ "\n 1. hospital_id"
					+ "<br> 	- 필수값")
	@GetMapping(value = "getHospitalDetail/{hospital_id}.do")
	public @ResponseBody Map<String, Object> getHospitalDetail(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String hospital_id,
			HospitalInfoVO hsptInfoVO) {

		logger.info("■■■■■■ getHospitalDetail / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
		Map<String, Object> hsptInfo = new HashMap<String, Object>();
		
		try {
			hsptInfoVO.setHospital_id(hospital_id);
			
			if(hsptInfoVO.getHospital_id() == null || "".equals(hsptInfoVO.getHospital_id())) {
				hsptInfo.put("msg", "병원 ID값이 없습니다.");
				res.setStatus(400);
				return hsptInfo;
			}
			
			hsptInfo = hsptDAO.getHospitalDetail(hsptInfoVO);
			
			if(hsptInfo.get("hospital_img") != null) {
				byte[] bArr = (byte[]) hsptInfo.get("hospital_img");
				byte[] base64 = Base64.encodeBase64(bArr);
				
				if(base64 != null){
					hsptInfo.put("hospital_img_base64", base64);
					hsptInfo.put("hospital_img_str", ("data:image/jpeg;base64," + new String(base64, "UTF-8")));
				} 
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hsptInfoVO.setStatus(400);
			hsptInfoVO.setMessage("병원 상세 정보 조회 실패");
			res.setStatus(400);
		}
		
		return hsptInfo;
	}
	
	
	@ApiOperation(value = "병원 진료시간 조회"
			, notes = "병원 진료시간 조회"
					+ "\n 1. hospital_id"
					+ "<br> 	- 필수값")
	@GetMapping(value = "getHospitalClinicList/{hospital_id}.do")
	public @ResponseBody Map<String, Object> getHospitalClinicList(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String hospital_id,
			HospitalInfoVO hsptInfoVO) {
		
		Map<String, Object> list = new HashMap<String, Object>();
		
		hsptInfoVO.setHospital_id(hospital_id);
		
		if(hsptInfoVO.getHospital_id() == null || "".equals(hsptInfoVO.getHospital_id())) {
			res.setStatus(400);
			return list;
		}
		
		logger.info("■■■■■■ getHospitalClinicList / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
		List<Map<String, Object>> hsptList = hsptDAO.getHospitalClinicList(hsptInfoVO);
		
		
		list.put("data", hsptList);
		list.put("size", hsptList.size());
		
		return list;
	}
	
	
	@ApiOperation(value = "병원 진료시간 신규등록"
			, notes = "병원 진료시간 신규등록"
					+ "\n 1. hospital_id"
					+ "<br> 	- 필수값"
					+ "\n 2. day_of_week"
					+ "<br>		- 필수값 : MON, TUE, WED, THR, FRI, SAT, SUN"
					+ "\n 3. clinic_yn"
					+ "<br>		- 선택값 : Y, N"
					+ "\n 4. lunch_yn"
					+ "<br>		- 선택값 : Y, N"
					+ "\n 5. c_strt_tm"
					+ "<br>		- 진료 시작 시간"
					+ "\n 6. c_end_tm"
					+ "<br>		- 진료 종료 시간"
					+ "\n 7. l_strt_tm"
					+ "<br>		- 점심 시작 시간"
					+ "\n 8. l_end_tm"
					+ "<br>		- 점심 종료 시간")
	@PostMapping(value = "insertHospitalClinic/{hospital_id}.do")
	public @ResponseBody ResponseVO insertHospitalClinic(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String hospital_id,
			@RequestBody HospitalInfoVO hsptInfoVO) {
		
		logger.info("■■■■■■ insertHospitalClinic / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
		
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rslt = new HashMap<String, Object>();
		int cnt = -1;
		
		try {
			hsptInfoVO.setHospital_id(hospital_id);
			
			if(hsptInfoVO.getHospital_id() == null || "".equals(hsptInfoVO.getHospital_id())) {
				rslt.put("msg", "병원 ID값이 없습니다.");
				rslt.put("val", cnt);
				rsltVO.setData(rslt);
				res.setStatus(400);
				return rsltVO;
			}
			
			int dupCnt = hsptDAO.insertHospitalDupChk(hsptInfoVO);
			if(dupCnt > 0) {
				rslt.put("cnt", cnt);
				rslt.put("msg", "FAIL / Duplication Hospital Clinic Mapp");
				res.setStatus(400);
			} else {
				cnt = hsptDAO.insertHospitalClinic(hsptInfoVO);
				
				if(cnt < 1){ throw new Exception();}
				
				rslt.put("cnt", cnt);
				rslt.put("msg", "SUCCESS");
			}
		} catch (Exception e) {
			// TODO: handle exception
			rslt.put("msg", e.getMessage());
			rslt.put("cnt", cnt);
			rsltVO.setStatus(400);
			rsltVO.setMessage("병원 진료시간 신규등록 실패");
			res.setStatus(400);
		}
		
		rsltVO.setData(rslt);
		
		return rsltVO;
	}
	
	
	@ApiOperation(value = "병원 진료시간 수정"
			, notes = "병원 진료시간 수정"
					+ "\n 1. hospital_id"
					+ "<br> 	- 필수값"
					+ "\n 2. day_of_week"
					+ "<br>		- 필수값 : MON, TUE, WED, THR, FRI, SAT, SUN"
					+ "\n 3. clinic_yn"
					+ "<br>		- 선택값 : Y, N"
					+ "\n 4. lunch_yn"
					+ "<br>		- 선택값 : Y, N"
					+ "\n 5. c_strt_tm"
					+ "<br>		- 진료 시작 시간"
					+ "\n 6. c_end_tm"
					+ "<br>		- 진료 종료 시간"
					+ "\n 7. l_strt_tm"
					+ "<br>		- 점심 시작 시간"
					+ "\n 8. l_end_tm"
					+ "<br>		- 점심 종료 시간")
	@PostMapping(value = "updateHospitalClinic/{hospital_id}.do")
	public @ResponseBody ResponseVO updateHospitalClinic(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String hospital_id,
			@RequestBody HospitalInfoVO hsptInfoVO) {
		
		logger.info("■■■■■■ updateHospitalClinic / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
		
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rslt = new HashMap<String, Object>();
		int cnt = -1;
		
		try {
			hsptInfoVO.setHospital_id(hospital_id);
			
			if(hsptInfoVO.getHospital_id() == null || "".equals(hsptInfoVO.getHospital_id())) {
				rslt.put("msg", "병원 ID값이 없습니다.");
				rslt.put("val", cnt);
				rsltVO.setData(rslt);
				res.setStatus(400);
				return rsltVO;
			}
			
			int dupCnt = hsptDAO.insertHospitalDupChk(hsptInfoVO);
			
			if(dupCnt > 0) {
				cnt = hsptDAO.updateHospitalClinic(hsptInfoVO);
				
				if(cnt < 1){ throw new Exception();}
				
				rslt.put("cnt", cnt);
				rslt.put("msg", "SUCCESS");
			} else {
				rslt.put("cnt", cnt);
				rslt.put("msg", "FAIL / No Data Hospital Clinic Mapp");
				res.setStatus(400);
			}
		} catch (Exception e) {
			// TODO: handle exception
			rslt.put("msg", e.getMessage());
			rslt.put("cnt", cnt);
			rsltVO.setStatus(400);
			rsltVO.setMessage("병원 진료시간 수정 실패");
			res.setStatus(400);
		}
		
		rsltVO.setData(rslt);
		
		return rsltVO;
	}
	
	

	@ApiOperation(value = "병원 상세 정보 수정"
			, notes = "병원 상세 정보 수정"
					+ "\n 1. hospital_id"
					+ "<br> 	- 필수값"
					+ "\n 2. hospital_pwd"
					+ "<br>		- 선택값")
	@PostMapping(value = "updateHospitalInfo/{hospital_id}.do")
	public @ResponseBody ResponseVO updateHospitalInfo(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String hospital_id,
			@RequestParam(value = "img_file", required = false) MultipartFile img_file, 
			HospitalInfoVO hsptInfoVO) {
		
		logger.info("■■■■■■ updateHospitalInfo / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
		
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rslt = new HashMap<String, Object>();
		int cnt = -1;
		
		try {
			hsptInfoVO.setHospital_id(hospital_id);
			
			if(hsptInfoVO.getHospital_id() == null || "".equals(hsptInfoVO.getHospital_id())) {
				rslt.put("msg", "병원 ID값이 없습니다.");
				rslt.put("val", cnt);
				rsltVO.setData(rslt);
				res.setStatus(400);
				return rsltVO;
			}
			
			byte[] b_img_file;
			if(img_file != null) {
				b_img_file = img_file.getBytes();
				hsptInfoVO.setImg_file_byte(b_img_file);
			}
			
			cnt = hsptDAO.updateHospitalInfo(hsptInfoVO);
			
			if(cnt < 1){ throw new Exception();}
			
			cnt = cnt > 0 ? hsptDAO.insertHospitalHst(hsptInfoVO) : cnt; 
			
			rslt.put("cnt", cnt);
			rslt.put("msg", "SUCCESS");
		} catch (Exception e) {
			// TODO: handle exception
			rslt.put("msg", e.getMessage());
			rslt.put("cnt", cnt);
			rsltVO.setStatus(400);
			rsltVO.setMessage("병원 상세정보 수정 실패");
			res.setStatus(400);
		}
		
		rsltVO.setData(rslt);
		
		return rsltVO;
	}
	
	
	@ApiOperation(value = "병원 탈퇴 처리"
			, notes = "병원 탈퇴 처리")
	@PostMapping(value = "deleteHospitalInfo/{hospital_id}.do")
	public @ResponseBody ResponseVO deleteHospitalInfo(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String hospital_id,
			@RequestBody HospitalInfoVO hsptInfoVO) {
		
		logger.info("■■■■■■ deleteHospitalInfo / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
		
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rslt = new HashMap<String, Object>();
		int cnt = -1;
		
		try {
			hsptInfoVO.setHospital_id(hospital_id);
			hsptInfoVO.setUse_yn("N");
			
			if(hsptInfoVO.getHospital_id() == null || "".equals(hsptInfoVO.getHospital_id())) {
				rslt.put("msg", "병원 ID값이 없습니다.");
				rslt.put("val", cnt);
				rsltVO.setData(rslt);
				res.setStatus(400);
				return rsltVO;
			}
			
			cnt = hsptDAO.updateHospitalInfo(hsptInfoVO);
			
			if(cnt < 1){ throw new Exception();}
			
			cnt = cnt > 0 ? hsptDAO.insertHospitalHst(hsptInfoVO) : cnt; 
			
			rslt.put("cnt", cnt);
			rslt.put("msg", "SUCCESS");
		} catch (Exception e) {
			// TODO: handle exception
			rslt.put("msg", e.getMessage());
			rslt.put("cnt", cnt);
			rsltVO.setStatus(400);
			rsltVO.setMessage("병원 탈퇴 처리 실패");
			res.setStatus(400);
		}
		
		rsltVO.setData(rslt);
		
		return rsltVO;
	}
	
	
	@ApiOperation(value = "병원 결제 내역 리스트 조회"
			, notes = "병원 결제 내역 리스트 조회")
	@GetMapping(value = "getHospitalPayInfoList/{hospital_id}.do")
	public @ResponseBody Map<String, Object> getHospitalPayInfoList(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String hospital_id,
			HospitalInfoVO hsptInfoVO) {
		
		hsptInfoVO.setHospital_id(hospital_id);
		
		if(hsptInfoVO.getHospital_id() == null || "".equals(hsptInfoVO.getHospital_id())) {
			res.setStatus(400);
			return null;
		}
		
		logger.info("■■■■■■ getHospitalPayInfoList / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
		List<Map<String, Object>> hsptList = hsptDAO.getHospitalPayInfoList(hsptInfoVO);
		
		Map<String, Object> list = new HashMap<String, Object>();
		
		list.put("data", hsptList);
		list.put("size", hsptList.size());
		
		return list;
	}
	
	
	@ApiOperation(value = "병원 서비스 이용 리스트 조회"
			, notes = "병원 서비스 이용 리스트 조회"
					+ "<br> ** > 기획서 미완성으로 전체리스트만 간략하게 전달")
	@GetMapping(value = "getHospitalInferList.do")
	public @ResponseBody Map<String, Object> getHospitalInferList(
			HttpServletRequest req,
			HttpServletResponse res,
//			@PathVariable String hospital_id,
			HospitalInfoVO hsptInfoVO) {
		
//		hsptInfoVO.setHospital_id(hospital_id);
//		
//		if(hsptInfoVO.getHospital_id() == null || "".equals(hsptInfoVO.getHospital_id())) {
//			res.setStatus(400);
//			return null;
//		}
		
		logger.info("■■■■■■ getHospitalInferList / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
		List<Map<String, Object>> hsptList = hsptDAO.getHospitalInferList(hsptInfoVO);
		
		Map<String, Object> list = new HashMap<String, Object>();
		
		list.put("data", hsptList);
		list.put("size", hsptList.size());
		
		if(hsptList.size() == 0){
			return null;
		}
		
		return list;
	}
	
	
	
}
