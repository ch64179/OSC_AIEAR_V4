package com.aiear.user;

import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.aiear.dao.UserMngDAO;
import com.aiear.vo.ResponseVO;
import com.aiear.vo.UserInfoVO;

@RestController
@RequestMapping("/user/*")
@RequiredArgsConstructor
public class UserMngCont {
	
	/** 로그 처리용 개체 선언 */
	protected Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private UserMngDAO userDAO;

	@Autowired
	private CommonDAO commonDAO;
	
	
	@ApiOperation(value = "유저 정보 리스트 조회"
				, notes = "유저 정보 리스트 조회"
						+ "<br> ** > user_nm, user_code 없을 시 전체조회**"
						+ "<br> ** > order_type, oder_type2 2개다 있어야지 정렬**"
						+ "<br> ** > raw_cnt, page_cnt 2개다 있어야지 Paginatoin 가능**"
						+ "\n 1. user_nm"
						+ "<br> 	- 이름, 코드 LIKE 검색"
//						+ "\n 2. user_code"
//						+ "<br> 	- LIKE 검색"
						+ "\n 3. order_type"
						+ "<br> 	- Default : 가입일(GEN_DT)"
						+ " - 활동 상태(USE_YN), 회원 닉네임(USER_NM), 회원 코드(USER_CODE), 가입 <br> 정보(USER_EMAIL), 가입일(GEN_DT)"
						+ "\n 4. order_type2"
						+ "<br> 	- Default : 내림차순(DESC)"
						+ "<br>  - 오름차순(ASC), 내림차순(DESC)"
						+ "\n 5. raw_cnt"
						+ "<br> 	- 페이지별 로우데이터 갯수"
						+ "\n 6. page_cnt"
						+ "<br>  - 페이지 선택"
				)
	@GetMapping(value = "getUserList.do")
	public @ResponseBody Map<String, Object> getUserList(
			HttpServletRequest req,
			HttpServletResponse res,
			UserInfoVO userInfoVO) {
		
		logger.info("■■■■■■ getUserList / userInfoVO : {}", userInfoVO.beanToHmap(userInfoVO).toString());
		List<Map<String, Object>> userList = userDAO.getUserListInfo(userInfoVO);
		
		Map<String, Object> list = new HashMap<String, Object>();
		
		list.put("data", userList);
		
		userInfoVO.setRaw_cnt(null);
		userInfoVO.setPage_cnt(null);
		List<Map<String, Object>> userListSize = userDAO.getUserListInfo(userInfoVO);
		
		list.put("size", userListSize.size());
		
		return list;
	}
	
	
	@ApiOperation(value = "유저 상세 정보 조회"
				, notes = "유저 상세 정보 조회")
	@GetMapping(value = "getUserDetail/{user_code}.do")
	public @ResponseBody Map<String, Object> getUserDetail(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String user_code,
			UserInfoVO userInfoVO) {
	
		logger.info("■■■■■■ getUserDetail / userInfoVO : {}", userInfoVO.beanToHmap(userInfoVO).toString());
		Map<String, Object> userInfo = new HashMap<String, Object>();
		
		try {
			userInfoVO.setUser_code(user_code);
			
			if(userInfoVO.getUser_code() == null || "".equals(userInfoVO.getUser_code())) {
				userInfo.put("msg", "유저 코드값이 없습니다.");
				res.setStatus(400);
				return userInfo;
			}
			
			userInfo = userDAO.getUserDetailInfo(userInfoVO);
			
			if(userInfo.get("user_img") != null){
				byte[] bArr = (byte[]) userInfo.get("user_img");
				byte[] base64 = Base64.encodeBase64(bArr);
				
				if(base64 != null){
					userInfo.put("user_img_str", ("data:image/jpeg;base64," + new String(base64, "UTF-8")));
				} 
			}
			
			userInfoVO.setResult(true);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			userInfoVO.setStatus(400);
			userInfoVO.setResult(false);
			userInfoVO.setMessage("유저 상세 정보 조회 실패");
			res.setStatus(400);
		}
		
		return userInfo;
	}
	
	
	@ApiOperation(value = "유저 가족관계 정보 조회"
				, notes = "유저 가족관계 정보 조회")
	@GetMapping(value = "getUserRelationList/{user_code}.do")
	public @ResponseBody Map<String, Object> getUserRelationList(
			HttpServletRequest req,
			HttpServletResponse res,
			UserInfoVO userInfoVO) {
		
		logger.info("■■■■■■ getUserRelationList / userInfoVO : {}", userInfoVO.beanToHmap(userInfoVO).toString());
		
		List<Map<String, Object>> userList = userDAO.getUserRelationList(userInfoVO);
		
		Map<String, Object> list = new HashMap<String, Object>();
		
		list.put("data", userList);
		list.put("size", userList.size());
		
		return list;
	}
	
	
	@ApiOperation(value = "유저 코드 중복체크"
				, notes = "유저 코드 중복체크"
						+ "\n 1. user_code"
						+ "<br> 	- 필수값")
	@GetMapping(value = "getUserCodeDupChk.do")
	public @ResponseBody ResponseVO getUserCodeDupChk(
			HttpServletRequest req,
			HttpServletResponse res,
			UserInfoVO userInfoVO) {
		
		logger.info("■■■■■■ getUserCodeDupChk / userInfoVO : {}", userInfoVO.beanToHmap(userInfoVO).toString());
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rslt = new HashMap<String, Object>();
		
		try {
			if(userInfoVO.getUser_code() == null || "".equals(userInfoVO.getUser_code())) {
				rslt.put("msg", "유저 코드값이 없습니다.");
				rsltVO.setData(rslt);
				res.setStatus(400);
				return rsltVO;
			}
			
			int cnt = userDAO.getUserCodeDupChk(userInfoVO);
			
			String msg = cnt > 0 ? "중복 코드가 존재합니다." : "사용가능합니다.";
			
			
			rslt.put("cnt", cnt);
			rslt.put("msg", msg);
			
			rsltVO.setData(rslt);
			
			if(cnt > 0){
				rsltVO.setStatus(400);
				rsltVO.setResult(false);
				res.setStatus(400);
			} else {
				rsltVO.setResult(true);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			rsltVO.setStatus(400);
			rsltVO.setResult(false);
			rsltVO.setMessage("유저 코드 중복체크 실패");
			res.setStatus(400);
		}
		
		return rsltVO;
	}
	
	
	@ApiOperation(value = "유저 정보 업데이트"
				, notes = "유저 정보 업데이트"
						+ "\n 1. mdfy_user_code"
						+ "<br> 	- (선택)"
						+ "\n 2. user_nm"
						+ "<br>		- (선택)"
						+ "\n 3. img_file"
						+ "<br>		- 이미지 파일(multipart)"
						+ "\n 4. user_addr"
						+ "<br>		- (선택)유저 주소")
	@PostMapping(value = "updateUserDetail/{user_code}.do")
	public @ResponseBody ResponseVO updateUserDetail(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String user_code,
			@RequestParam(value = "img_file", required = false) MultipartFile img_file,
			UserInfoVO userInfoVO) {
		
		logger.info("■■■■■■ updateUserDetail / userInfoVO : {}", userInfoVO.beanToHmap(userInfoVO).toString());
	
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rslt = new HashMap<String, Object>();
		int cnt = -1;
		
		try {
			userInfoVO.setUser_code(user_code);
			
			if(userInfoVO.getUser_code() == null || "".equals(userInfoVO.getUser_code())) {
				rslt.put("msg", "유저 코드값이 없습니다.");
				rslt.put("val", cnt);
				rsltVO.setData(rslt);
				res.setStatus(400);
				return rsltVO;
			}
			
			byte[] b_img_file;
			if(img_file != null) {
				b_img_file = img_file.getBytes();
				userInfoVO.setImg_file_byte(b_img_file);
			}
			
			cnt = userDAO.updateUserDetail(userInfoVO);
			
			if(cnt < 1){ throw new Exception(); }
			
			cnt = cnt > 0 ? userDAO.insertUserHst(userInfoVO) : cnt;
			
			rslt.put("cnt", cnt);
			rslt.put("msg", "SUCCESS");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			rslt.put("msg", e.getMessage());
			rslt.put("cnt", cnt);
			rsltVO.setStatus(400);
			rsltVO.setMessage("유저 정보 업데이트 실패");
			res.setStatus(400);
		}
		
		rsltVO.setData(rslt);
		
		return rsltVO;
	}
	
	
	@ApiOperation(value = "유저 가족관계 신규등록"
			, notes = "유저 가족관계 신규등록"
					+ "\n 1. family_user_code"
					+ "<br>		- (필수)"
					+ "\n 2. family_relation"
					+ "<br>		- (선택)")
	@PostMapping(value = "insertUserFamilyMapp/{user_code}.do")
	public @ResponseBody ResponseVO insertUserFamilyMapp(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String user_code,
			@RequestBody UserInfoVO userInfoVO) {
		
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rslt = new HashMap<String, Object>();
		int cnt = -1;
		
		logger.info("■■■■■■ insertUserFamilyMapp / userInfoVO : {}", userInfoVO.beanToHmap(userInfoVO).toString());
		
		try {
			userInfoVO.setUser_code(user_code);
			
			if(userInfoVO.getUser_code() == null || "".equals(userInfoVO.getUser_code())) {
				rslt.put("msg", "유저 코드값이 없습니다.");
				rslt.put("val", cnt);
				rsltVO.setData(rslt);
				res.setStatus(400);
				return rsltVO;
			}
			
			cnt = userDAO.insertUserFamilyMapp(userInfoVO);
			
			if(cnt < 1){ throw new Exception();}
			
			rslt.put("cnt", cnt);
			rslt.put("msg", "SUCCESS");
		} catch (Exception e) {
			// TODO: handle exception
			rslt.put("msg", e.getMessage());
			rslt.put("cnt", cnt);
			rsltVO.setStatus(400);
			rsltVO.setMessage("유저 가족관계 신규등록 실패");
			res.setStatus(400);
		}
		
		rsltVO.setData(rslt);
		
		return rsltVO;
	}
	
	
	@ApiOperation(value = "유저 가족관계 수정"
				, notes = "유저 가족관계 수정"
						+ "\n 1. family_seq"
						+ "<br> 	- (필수)"
						+ "\n 2. family_user_code"
						+ "<br>		- (선택)"
						+ "\n 3. family_relation"
						+ "<br>		- (선택)")
	@PostMapping(value = "updateUserFamilyMapp/{user_code}.do")
	public @ResponseBody ResponseVO updateUserFamilyMapp(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String user_code,
			@RequestBody UserInfoVO userInfoVO) {

		userInfoVO.setUser_code(user_code);
		logger.info("■■■■■■ updateUserFamilyMapp / userInfoVO : {}", userInfoVO.beanToHmap(userInfoVO).toString());
		
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rslt = new HashMap<String, Object>();
		int cnt = -1;
		
		try {
			userInfoVO.setUser_code(user_code);
			
			if(userInfoVO.getUser_code() == null || "".equals(userInfoVO.getUser_code())) {
				rslt.put("msg", "유저 코드값이 없습니다.");
				rslt.put("val", cnt);
				rsltVO.setData(rslt);
				res.setStatus(400);
				return rsltVO;
			}
			
			cnt = userDAO.updateUserFamilyMapp(userInfoVO);
			
			if(cnt < 1){ throw new Exception();}
			
			rslt.put("cnt", cnt);
			rslt.put("msg", "SUCCESS");
		} catch (Exception e) {
			// TODO: handle exception
			rslt.put("msg", e.getMessage());
			rslt.put("cnt", cnt);
			rsltVO.setStatus(400);
			rsltVO.setMessage("유저 가족관계 수정 실패");
			res.setStatus(400);
		}

		rsltVO.setData(rslt);
		
		return rsltVO;
	}
	
	
	@ApiOperation(value = "유저 탈퇴 처리"
				, notes = "유저 탈퇴 처리")
	@PostMapping(value = "deleteUserAction/{user_code}.do")
	public @ResponseBody ResponseVO deleteUserAction(
			HttpServletRequest req,
			HttpServletResponse res,
			@PathVariable String user_code,
			UserInfoVO userInfoVO) {
		
		logger.info("■■■■■■ deleteUserAction / userInfoVO : {}", userInfoVO.beanToHmap(userInfoVO).toString());
		
		ResponseVO rsltVO = new ResponseVO();
		Map<String, Object> rsltMap = new HashMap<String, Object>();
		Integer cnt = -1;
		
		try {
			userInfoVO.setUser_code(user_code);
			
			if(userInfoVO.getUser_code() == null || "".equals(userInfoVO.getUser_code())) {
				rsltMap.put("msg", "유저 코드값이 없습니다.");
				rsltMap.put("val", cnt);
				rsltVO.setData(rsltMap);
				res.setStatus(400);
				return rsltVO;
			}
			
			cnt = userDAO.deleteUserAction(userInfoVO);
			
			if(cnt < 1){ throw new Exception();}
			
			cnt = cnt > 0 ? userDAO.insertUserHst(userInfoVO) : cnt;
			
			rsltMap.put("msg", "SUCCESS");
			rsltMap.put("val", cnt);
			
		} catch (Exception e) {
			// TODO: handle exception
			rsltMap.put("msg", e.getMessage());
			rsltMap.put("val", cnt);
			rsltVO.setStatus(400);
			rsltVO.setMessage("유저 탈퇴 처리 실패");
			res.setStatus(400);
		}
		
		rsltVO.setData(rsltMap);
		
		return rsltVO;
	}
	
	
}
