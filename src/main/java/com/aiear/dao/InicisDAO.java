package com.aiear.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface InicisDAO {

	
	/**
	 * Description	: 
	 * @method		: getInicisIndList
	 * @author		: pcw
	 * @date		: 2023. 1. 30.
	 * @param map
	 * @return
	 */
	public Map<String, Object> getInicisIndList(Map<String, String> map);
	
	/**
	 * Description	: 
	 * @method		: insertInicisPayHst
	 * @author		: pcw
	 * @date		: 2023. 1. 1.
	 * @param map
	 * @return
	 */
	public int insertInicisPayHst(Map<String, String> map);
	
	
	/**
	 * Description	: 
	 * @method		: insertInicisUserPayMapp
	 * @author		: pcw
	 * @date		: 2023. 1. 2.
	 * @param map
	 * @return
	 */
	public int insertInicisUserPayMapp(Map<String, String> map);
	
	
	/**
	 * Description	: 
	 * @method		: insertInicisRefundHst
	 * @author		: pcw
	 * @date		: 2023. 1. 30.
	 * @param map
	 * @return
	 */
	public int insertInicisRefundHst(Map<String, Object> map);
	
	
	/**
	 * Description	: 
	 * @method		: updateInicisPayHst
	 * @author		: pcw
	 * @date		: 2023. 1. 30.
	 * @param map
	 * @return
	 */
	public int updateInicisPayHst(Map<String, Object> map);
	
	
	/**
	 * Description	: 
	 * @method		: updateInicisUserPayMapp
	 * @author		: pcw
	 * @date		: 2023. 1. 30.
	 * @param map
	 * @return
	 */
	public int updateInicisUserPayMapp(Map<String, Object> map);
}
