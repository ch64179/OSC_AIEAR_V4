package com.aiear.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.aiear.vo.HospitalInfoVO;

@Mapper
@Repository
public interface HospitalMngDAO {

	/**
	* @methodName    : getHospitalListInfo
	* @author        : pcw
	* @date        : 2022.11.19
	* @param vo
	* @return
	*/
	public List<Map<String, Object>> getHospitalList(HospitalInfoVO vo);
	
	
	/**
	* @methodName    : insertHospital
	* @author        : pcw
	* @date        : 2022.11.20
	* @param vo
	* @return
	*/
	public int insertHospitalInfo(HospitalInfoVO vo);
	
	
	/**
	* @methodName    : getHospitalDupChk
	* @author        : pcw
	* @date        : 2022.11.20
	* @param vo
	* @return
	*/
	public int getHospitalDupChk(HospitalInfoVO vo);
	
	
	/**
	* @methodName    : insertHospitalHst
	* @author        : pcw
	* @date        : 2022.11.20
	* @param vo
	* @return
	*/
	public int insertHospitalHst(HospitalInfoVO vo);
	
	
	/**
	* @methodName    : getHospitalDetail
	* @author        : pcw
	* @date        : 2022.11.20
	* @param vo
	* @return
	*/
	public Map<String, Object> getHospitalDetail(HospitalInfoVO vo);
	
	
	/**
	* @methodName    : getHospitalClinicList
	* @author        : pcw
	* @date        : 2022.11.20
	* @param vo
	* @return
	*/
	public List<Map<String, Object>> getHospitalClinicList(HospitalInfoVO vo);

	
	/**
	 * Description	: 
	 * @method		: insertHospitalDupChk
	 * @author		: pcw
	 * @date		: 2022. 11. 21.
	 * @param vo
	 * @return
	 */
	public int insertHospitalDupChk(HospitalInfoVO vo);
	
	
	/**
	* @methodName    : insertHospitalClinic
	* @author        : pcw
	* @date        : 2022.11.20
	* @param vo
	* @return
	*/
	public int insertHospitalClinic(HospitalInfoVO vo);
	
	
	/**
	* @methodName    : updateHospitalClinic
	* @author        : pcw
	* @date        : 2022.11.20
	* @param vo
	* @return
	*/
	public int updateHospitalClinic(HospitalInfoVO vo);
	
	
	/**
	 * Description	: 
	 * @method		: updateHospitalInfo
	 * @author		: pcw
	 * @date		: 2022. 11. 21.
	 * @param vo
	 * @return
	 */
	public int updateHospitalInfo(HospitalInfoVO vo);
	
	
	/**
	 * Description	: 
	 * @method		: getHospitalPayInfoList
	 * @author		: pcw
	 * @date		: 2023. 1. 21.
	 * @param vo
	 * @return
	 */
	public List<Map<String, Object>> getHospitalPayInfoList(HospitalInfoVO vo);
	
	
	/**
	 * Description	: 
	 * @method		: getHospitalInferList
	 * @author		: pcw
	 * @date		: 2023. 1. 21.
	 * @param vo
	 * @return
	 */
	public List<Map<String, Object>> getHospitalInferList(HospitalInfoVO vo);

	
	/**
	 * Description	: 
	 * @method		: getGoodsList
	 * @author		: pcw
	 * @date		: 2023. 1. 29.
	 * @return
	 */
	public List<Map<String, Object>> getGoodsList();
	
}
