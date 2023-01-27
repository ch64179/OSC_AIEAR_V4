package com.aiear.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.aiear.vo.AnalysisVO;


@Mapper
@Repository
public interface AnalysisDAO {

	/**
	 * Description	: 
	 * @method		: getAnalysisYearList
	 * @author		: pcw
	 * @date		: 2023. 1. 18.
	 * @param vo
	 * @return
	 */
	public List<Map<String, Object>> getAnalysisYearList(AnalysisVO vo);
	
	/**
	 * Description	: 
	 * @method		: getGridList
	 * @author		: pcw
	 * @date		: 2023. 1. 18.
	 * @param vo
	 * @return
	 */
	public List<Map<String, Object>> getGridList(AnalysisVO vo);
	
	
}
