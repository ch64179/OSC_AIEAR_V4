package com.aiear.analysis;

import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aiear.dao.AnalysisDAO;
import com.aiear.dao.CommonDAO;
import com.aiear.vo.AnalysisVO;


@RestController
@RequestMapping("/analysis/*")
@RequiredArgsConstructor
public class AnalysisCont {
	
	/** 로그 처리용 개체 선언 */
	protected Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private CommonDAO commonDAO;
	
	@Autowired
	private AnalysisDAO analDAO;
	
	@ApiOperation(value = "통계 Grid 데이터 조회"
				, notes = "통계 Grid 데이터 조회"
						+ "\n 1. search_year"
						+ "<br> 	- (필수값) 검색 년도")
	@GetMapping(value = "getAnalysisGrid.do")
	public @ResponseBody Map<String, Object> getAnalysisGrid(
			HttpServletRequest req,
			HttpServletResponse res,
			AnalysisVO analVO) {
		
		logger.info("■■■■■■ getAnalysisGrid / analVO : {}", analVO.beanToHmap(analVO).toString());
		
		Map<String, Object> list = new HashMap<String, Object>();
		Map<String, Object> mdList = new LinkedHashMap<String, Object>();
		
		if("".equals(analVO.getSearch_year()) || analVO.getSearch_year() == null){
			res.setStatus(400);
			return list;
		}
		
		analVO.setUser_type("USER");
		List<Map<String, Object>> userList = analDAO.getGridList(analVO);
		
		analVO.setUser_type("HOSPITAL");
		List<Map<String, Object>> hsptList = analDAO.getGridList(analVO);
		
		for(Map<String, Object> userMap : userList){
			List<Map<String, Object>> dList = new ArrayList<Map<String,Object>>();
			Map<String, Object> totalMap = new HashMap<String, Object>();			
			
			for(Map<String, Object> hsptMap : hsptList){
				if(userMap.get("xaxis_m").equals(hsptMap.get("xaxis_m"))){
					totalMap.put(userMap.get("user_type").toString().toLowerCase(), userMap.get("cnt"));
					totalMap.put(hsptMap.get("user_type").toString().toLowerCase(), hsptMap.get("cnt"));
					totalMap.put("total",
							Integer.parseInt(userMap.get("cnt").toString()) +
							Integer.parseInt(hsptMap.get("cnt").toString()));
				}
			}
			
			dList.add(totalMap);
			mdList.put(userMap.get("xaxis_m").toString(), dList);
		}
		
		list.put("ear_data", mdList);
		list.put("ear_size", mdList.size());
		
		list.put("es_data", "");
		list.put("es_size", 0);
		
		return list;
	}
	
	
	
//	@ApiOperation(value = "통계 Grid 데이터 Excel Export"
//			, notes = "통계 Grid 데이터 Excel Export"
//					+ "\n 1. xx"
//					+ "<br> 	- xx")
//	@GetMapping(value = "exportAccountList.do")
//	public void exportAccountList(
//			HttpServletRequest req,
//			HttpServletResponse res,
//			AccountInfoVO accInfoVO
//			) {
//		try {
//			List<Map<String, Object>> accListSize = accDAO.getAccountList(accInfoVO);
//			
//			String excelName = DateUtil.getToday() + "_계정관리_리스트"; 
//			String titleList = "new_yn|new_yn_nm|use_yn|use_yn_nm|hospital_nm|hospital_id|hospital_pwd|gen_by|gen_dt|mdfy_by|mdfy_dt|user_type";
//			String columnList = "new_yn|new_yn_nm|use_yn|use_yn_nm|hospital_nm|hospital_id|hospital_pwd|gen_by|gen_dt|mdfy_by|mdfy_dt|user_type";
//			
//			ExcelUtil.ExcelfileCreate(res, titleList, columnList, accListSize, excelName);
////			CsvUtil.createCSVFile(res, accListSize, titleList, columnList, excelName);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			res.setStatus(400);
//		}
//	}
	
	
//	@ApiOperation(value = "병원 정보 리스트 조회"
//				, notes = "병원 정보 리스트 조회"
//						+ "<br> ** > hospital_nm, hospital_id 없을 시 전체조회**"
//						+ "<br> ** > order_type, oder_type2 2개다 있어야지 정렬**"
//						+ "<br> ** > raw_cnt, page_cnt 2개다 있어야지 Paginatoin 가능**"
//						+ "\n 1. hospital_nm"
//						+ "<br> 	- 이름, ID LIKE 검색"
////						+ "\n 2. hospital_id"
////						+ "<br> 	- LIKE 검색"
//						+ "\n 3. order_type"
//						+ "<br> 	- Default : 가입일(GEN_DT)"
//						+ " - 활동 상태(USE_YN), 병원 이름(HOSPITAL_NM), 병원 ID(HOSPITAL_ID), 생성일(GEN_DT)"
//						+ "\n 4. order_type2"
//						+ "<br> 	- Default : 내림차순(DESC)"
//						+ "<br>  - 오름차순(ASC), 내림차순(DESC)"
//						+ "\n 5. raw_cnt"
//						+ "<br> 	- 페이지별 로우데이터 갯수"
//						+ "\n 6. page_cnt"
//						+ "<br>  - 페이지 선택"
//				)
//	@GetMapping(value = "getHospitalList.do")
//	public @ResponseBody Map<String, Object> getHospitalList(
//			HttpServletRequest req,
//			HttpServletResponse res,
//			HospitalInfoVO hsptInfoVO) {
//		
//		logger.info("■■■■■■ getHospitalList / hsptInfoVO : {}", hsptInfoVO.beanToHmap(hsptInfoVO).toString());
//		List<Map<String, Object>> hsptList = hsptDAO.getHospitalList(hsptInfoVO);
//		
//		Map<String, Object> list = new HashMap<String, Object>();
//		
//		list.put("data", hsptList);
//		
//		hsptInfoVO.setRaw_cnt(null);
//		hsptInfoVO.setPage_cnt(null);
//		List<Map<String, Object>> hsptListSize = hsptDAO.getHospitalList(hsptInfoVO);
//		
//		list.put("size", hsptListSize.size());
//		
//		return list;
//	}
	
	
}
