package com.aiear.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface InicisDAO {

	
	/**
	 * Description	: 
	 * @method		: insertInicisPayHst
	 * @author		: pcw
	 * @date		: 2023. 1. 1.
	 * @param map
	 * @return
	 */
	public int insertInicisPayHst(Map<String, String> map);
	
	
}
