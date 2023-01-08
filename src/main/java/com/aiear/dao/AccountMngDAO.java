package com.aiear.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.aiear.vo.AccountInfoVO;

@Mapper
@Repository
public interface AccountMngDAO {

	/**
	 * Description	: 
	 * @method		: getAccountList
	 * @author		: pcw
	 * @date		: 2023. 1. 7.
	 * @param vo
	 * @return
	 */
	public List<Map<String, Object>> getAccountList(AccountInfoVO vo);
	
	
	/**
	 * Description	: 
	 * @method		: updateAccountInfo
	 * @author		: pcw
	 * @date		: 2023. 1. 8.
	 * @param vo
	 * @return
	 */
	public int updateAccountInfo(AccountInfoVO vo);
}
