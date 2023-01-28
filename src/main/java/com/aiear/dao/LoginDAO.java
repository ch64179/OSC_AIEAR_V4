package com.aiear.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.aiear.vo.LoginVO;

@Mapper
@Repository
public interface LoginDAO {

	/**
	 * Description	: 
	 * @method		: normalLoginIdProcess
	 * @author		: pcw
	 * @date		: 2022. 11. 25.
	 * @param vo
	 * @return
	 */
	public Map<String, Object> normalLoginIdProcess(LoginVO vo);
	
	
	/**
	 * Description	: 
	 * @method		: normalLoginPwdProcess
	 * @author		: pcw
	 * @date		: 2022. 11. 27.
	 * @param vo
	 * @return
	 */
	public Map<String, Object> normalLoginPwdProcess(LoginVO vo);
	
	
	/**
	 * Description	: 
	 * @method		: searchIdPwdInfo
	 * @author		: pcw
	 * @date		: 2022. 11. 27.
	 * @param vo
	 * @return
	 */
	public Map<String, Object> searchIdPwdInfo(LoginVO vo);

	
	/**
	 * Description	: 
	 * @method		: updateTempPwd
	 * @author		: pcw
	 * @date		: 2022. 11. 27.
	 * @param vo
	 * @return
	 */
	public int updateHsptTempPwd(LoginVO vo);
	
	
	/**
	 * Description	: 
	 * @method		: updateLoginInfo
	 * @author		: pcw
	 * @date		: 2022. 11. 30.
	 * @param vo
	 * @return
	 */
	public int updateLoginInfo(LoginVO vo);
	
	
	/**
	 * Description	: 
	 * @method		: getUserSalt
	 * @author		: pcw
	 * @date		: 2023. 1. 28.
	 * @param vo
	 * @return
	 */
	public LoginVO getUserSalt(LoginVO vo);
}
