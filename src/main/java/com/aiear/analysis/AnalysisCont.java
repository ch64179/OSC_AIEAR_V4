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
import com.aiear.util.DateUtil;
import com.aiear.util.ExcelUtil;
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
			, notes = "통계 Grid 데이터 조회")
	@GetMapping(value = "getAnalysisYearList.do")
	public @ResponseBody Map<String, Object> getAnalysisYearList(
			HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("■■■■■■ getAnalysisYearList");
		
		Map<String, Object> list = new HashMap<String, Object>();
		
		List<Map<String, Object>> yearList = analDAO.getAnalysisYearList(null);
		
		list.put("data", yearList);
		list.put("size", yearList.size());
		
		return list;
	}
	
	
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
		
		//임시로 귀건강 검사랑 동일한 데이터 전달
		list.put("es_data", mdList);
		list.put("es_size", mdList.size());
		
		return list;
	}
	
	
	@ApiOperation(value = "통계 Grid 데이터 Excel Export"
			, notes = "통계 Grid 데이터 Excel Export"
			+ "\n 1. search_year"
			+ "<br> 	- (필수값) 검색 년도")
	@GetMapping(value = "exportAnalysisGridList.do")
	public void exportAccountList(
			HttpServletRequest req,
			HttpServletResponse res,
			AnalysisVO analVO
			) {
		try {
			
			logger.info("■■■■■■ exportAnalysisGridList / analVO : {}", analVO.beanToHmap(analVO).toString());
			
			Map<String, Object> list = new HashMap<String, Object>();
			Map<String, Object> mdList = new LinkedHashMap<String, Object>();
			
			
			if("".equals(analVO.getSearch_year()) || analVO.getSearch_year() == null){
				res.setStatus(400);
			}
			
			
			analVO.setUser_type("USER");
			List<Map<String, Object>> userList = analDAO.getGridList(analVO);
			
			analVO.setUser_type("HOSPITAL");
			List<Map<String, Object>> hsptList = analDAO.getGridList(analVO);
			
			List<Map<String, Object>> userListSize = userList;
//			List<Map<String, Object>> accListSize = hsptList;
			
			String excelName = DateUtil.getToday() + "_통계_" + analVO.getSearch_year(); 
			String titleList = "개인|병원|누적|";
			String columnList = "user|hospital|total|";
			
			
			//TODO: Excel Export 양식에 맞게
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
			
			ExcelUtil.ExcelfileCreateAnalysis(res, titleList, columnList, mdList, excelName);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res.setStatus(400);
		}
	}
	
	
}
