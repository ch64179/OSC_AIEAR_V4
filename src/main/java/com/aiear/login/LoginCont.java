package com.aiear.login;

import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

import com.aiear.config.session.AuthRequest;
import com.aiear.config.session.AuthenticateUtil;
import com.aiear.config.session.JwtUtil;
//import com.aiear.config.session.JwtManager;
//import com.aiear.config.session.Member;
import com.aiear.dao.CommonDAO;
import com.aiear.dao.HospitalMngDAO;
import com.aiear.dao.LoginDAO;
import com.aiear.dao.SMSDAO;
import com.aiear.util.LoginUtil;
import com.aiear.util.SHA512;
import com.aiear.util.SMSUtil;
import com.aiear.vo.HospitalInfoVO;
import com.aiear.vo.LoginVO;
import com.aiear.vo.ResponseVO;
import com.aiear.vo.SMSVO;


@RestController
@RequestMapping("/login/*")
@RequiredArgsConstructor
public class LoginCont {
	
	/** 로그 처리용 개체 선언 */
	protected Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private LoginDAO loginDAO;

	@Autowired
	private CommonDAO commonDAO;
	
	@Autowired
	private SMSDAO smsDAO;
	
	@Autowired
	private HospitalMngDAO hsptDAO;
	
	@Autowired
    private JwtUtil jwtUtil;
	 
    @Autowired
    private AuthenticationManager authenticationManager;
	
	
	@Value("${coolsms.from.mobile.no}")
	String COOL_SMS_MOBILE_NO;
	
	@Value("${coolsms.api.key}")
	String COOL_SMS_API_KEY;
	
	@Value("${coolsms.api.secret}")
	String COOL_SMS_API_SECRET;
	
	
	@ApiOperation(value = "로그인"
				, notes = "로그인"
						+ "\n 1. user_id"
						+ "<br> 	- 필수값"
						+ "\n 2. user_pwd"
						+ "<br> 	- 필수값"
						+ "<br>"
						+ "<br>▶ 관리자 구분 : use_type 값이 'ADMIN'일 경우"
						+ "<br>▶ 신규구분 : new_yn 값이 'Y'일 경우"
						+ "<br>▶ 탈퇴구분 : use_yn 값이 'N'일 경우"
				)
	@PostMapping(value = "normalLogin.do")
	public @ResponseBody ResponseVO normalLogin(
			HttpServletRequest req,
			HttpServletResponse res,
			@RequestBody LoginVO loginVO) {
		
		logger.info("■■■■■■ normalLogin / loginVO : {}", loginVO.beanToHmap(loginVO).toString());
		
		ResponseVO resVO = new ResponseVO();
		HttpSession session = req.getSession();
//		JwtManager jwtManager = new JwtManager();
		
		//Session에 있을 경우 자동 로그인
		if(session.getAttribute("login_id") != null && session.getAttribute("user_type") != null){
			loginVO.setUser_id(session.getAttribute("user_id").toString());
			loginVO.setUser_pwd(session.getAttribute("user_pwd").toString());
		}
		
		if(loginVO.getUser_id() == null || "".equals(loginVO.getUser_id())){
			resVO.setMessage("아이디를 입력해 주세요.");
			resVO.setResult(false);
			resVO.setStatus(400);
			res.setStatus(400);
			return resVO;
		}
		
		if(loginVO.getUser_pwd() == null || "".equals(loginVO.getUser_pwd())){
			resVO.setMessage("비밀번호를 입력해 주세요.");
			resVO.setResult(false);
			resVO.setStatus(400);
			res.setStatus(400);
			return resVO;
		}
		
		Map<String, Object> idChk = loginDAO.normalLoginIdProcess(loginVO);
		
		if(idChk == null) {
			resVO.setMessage(loginVO.getUser_id() + " / 일치하는 계정이 없습니다.");
			resVO.setResult(false);
			resVO.setStatus(400);
			res.setStatus(400);
			return resVO;
		}
		
		//TODO: 로그인 비밀번호 암호화
		String saltVO = idChk.get("user_salt").toString();
		String encPwd = SHA512.sha256(loginVO.getUser_pwd(), saltVO);
		loginVO.setUser_pwd(encPwd);
		
		Map<String, Object> pwdChk = loginDAO.normalLoginPwdProcess(loginVO);
		
		if(pwdChk == null){
			resVO.setMessage("비밀번호가 일치하지 않습니다.");
			resVO.setResult(false);
			resVO.setStatus(400);
			res.setStatus(400);
		} else {
			String authToken = null;
			String refreshToken = null;
			
			try {
				AuthRequest authRequest = new AuthRequest();
				authRequest.setHospitalId(loginVO.getUser_id());
				authRequest.setHospitalPwd(loginVO.getUser_pwd());
				authRequest.setUserType(pwdChk.get("user_type").toString());
				authToken = generateTokenStr(authRequest);
				refreshToken = generateRefreshTokenStr(authRequest);
				
				pwdChk.put("accessToken", authToken);
				pwdChk.put("refreshToken", refreshToken);
				
				resVO.setMessage(pwdChk.get("user_type") + " 로그인 성공했습니다.");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				resVO.setStatus(400);
				resVO.setMessage(pwdChk.get("user_type") + " 일치하는 계정이 없습니다, 토큰 생성에 실패했습니다.");
				res.setStatus(400);
			}
			
			loginVO.setRefresh_token(refreshToken);
			loginDAO.updateLoginInfo(loginVO);
			
			resVO.setData(pwdChk);
			resVO.setResult(true);
		}
		
		return resVO;
	}
	
	
//	@ApiOperation(value = "토큰 발급"
//			, notes = "토큰 발급"
//					+ "\n 1. hospitalId"
//					+ "<br> 	- 필수값"
//					+ "\n 1. hospitalPwd"
//					+ "<br> 	- 필수값"
//			)
//	@PostMapping("/authenticate")
//	public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getHospitalId(), authRequest.getHospitalPwd())
//            );
//        } catch (Exception ex) {
//            throw new Exception("inavalid username/password");
//        }
//        return jwtUtil.generateToken(authRequest.getHospitalId(), authRequest.getHospitalPwd(), authRequest.getUserType());
//    }
//	
//	
//	@ApiOperation(value = "Refresh 토큰 발급"
//			, notes = "Refresh 토큰 발급"
//					+ "\n 1. hospitalId"
//					+ "<br> 	- 필수값"
//					+ "\n 1. hospitalPwd"
//					+ "<br> 	- 필수값"
//			)
//	@PostMapping("/authenticateR")
//	public String generateRefreshToken(@RequestBody AuthRequest authRequest) throws Exception {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getHospitalId(), authRequest.getHospitalPwd())
//            );
//        } catch (Exception ex) {
//            throw new Exception("inavalid username/password");
//        }
//        return jwtUtil.generateRefreshToken(authRequest.getHospitalId(), authRequest.getHospitalPwd(), authRequest.getUserType());
//    }
	
	
	@ApiOperation(value = "로그아웃"
			, notes = "로그아웃"
					+ "\n 1. user_id"
					+ "<br> 	- 필수값"
			)
	@PostMapping(value = "normalLogout.do")
	public @ResponseBody ResponseVO normalLogout(
			HttpServletRequest req,
			HttpServletResponse res,
			@RequestBody LoginVO loginVO) {
		
		logger.info("■■■■■■ normalLogout / loginVO : {}", loginVO.beanToHmap(loginVO).toString());
		
		ResponseVO resVO = new ResponseVO();
		
		resVO.setResult(true);
		
		return resVO;
	}
	
	
	@ApiOperation(value = "토큰 재발급"
			, notes = "Refresh Token으로 토큰 재발급"
					+ "\n 1. user_id"
					+ "<br> 	- 유저 ID"
					+ "\n 2. refresh_token"
					+ "<br> 	- Refresh Token값"
			)
	@PostMapping(value = "reAuthenticate.do")
	public @ResponseBody ResponseVO reAuthenticate(
			HttpServletRequest req,
			HttpServletResponse res,
			@RequestBody LoginVO loginVO
//			@RequestParam(value = "user_id", required = true) String user_id,
//			@RequestParam(value = "refresh_token", required = true) String refresh_token
		) {
		
//		LoginVO loginVO = new LoginVO();
//		loginVO.setUser_id(user_id);
		
		logger.info("■■■■■■ reAuthenticate / loginVO : {}", loginVO.beanToHmap(loginVO).toString());
		
		ResponseVO resVO = new ResponseVO();
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		
		Map<String, Object> idChk = loginDAO.normalLoginIdProcess(loginVO);
		
		if(idChk == null){
			resVO.setResult(true);
			resVO.setMessage(loginVO.getUser_id() + " / 일치하는 계정 또는 토큰정보가 없습니다.");
			resVO.setResult(false);
			resVO.setStatus(400);
			res.setStatus(400);
			return resVO;
		}
		
		if(loginVO.getRefresh_token().equals(idChk.get("refresh_token"))) {
			String authToken = null;
			String refreshToken = null;
			
			try {
				AuthRequest authRequest = new AuthRequest();
				
				authRequest.setHospitalId(loginVO.getUser_id());
				authRequest.setHospitalPwd(idChk.get("user_pwd").toString());
				authRequest.setUserType(idChk.get("user_type").toString());
				
				authToken = generateTokenStr(authRequest);
				refreshToken = generateRefreshTokenStr(authRequest);
				
				tokenMap.put("accessToken", authToken);
				tokenMap.put("refreshToken", refreshToken);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				resVO.setStatus(400);
				resVO.setMessage(idChk.get("user_type") + " 일치하는 계정이 없습니다, 토큰 생성에 실패했습니다.");
				res.setStatus(400);
			}
			
			loginVO.setRefresh_token(refreshToken);
			loginDAO.updateLoginInfo(loginVO);
			
			resVO.setData(tokenMap);
			resVO.setResult(true);
		} else {
			resVO.setResult(true);
			resVO.setMessage(loginVO.getUser_id() + " / 일치하는 계정 또는 토큰정보가 없습니다.");
			resVO.setResult(false);
			resVO.setStatus(400);
			res.setStatus(400);
			return resVO;
		}
		
		//Refresh Token 403 Error 추후에 수정
		res.setStatus(200);
		return resVO;
	}
	
	
	@ApiOperation(value = "아이디 찾기"
			, notes = "아이디 찾기"
					+ "\n 1. mobile_tel_no"
					+ "<br> 	- 필수값"
			)
	@PostMapping(value = "searchIdInfo.do")
	public @ResponseBody ResponseVO serachIdInfo(
			HttpServletRequest req,
			HttpServletResponse res,
			@RequestBody LoginVO loginVO) {
		
		logger.info("■■■■■■ searchIdInfo / loginVO : {}", loginVO.beanToHmap(loginVO).toString());
		
		ResponseVO resVO = new ResponseVO();
		
		try {
			if(loginVO.getMobile_tel_no() == null || "".equals(loginVO.getMobile_tel_no().toString())){
				resVO.setMessage("전화번호를 입력해 주세요");
				resVO.setResult(false);
				resVO.setStatus(400);
				res.setStatus(400);
				return resVO;
			}
			
			Map<String, Object> srchIdInfo = loginDAO.searchIdPwdInfo(loginVO);
			
			if(srchIdInfo == null){
				resVO.setMessage(loginVO.getUser_id() + " / 일치하는 계정이 없습니다.");
				resVO.setResult(false);
				resVO.setStatus(400);
				res.setStatus(400);
			} else { 
				
				//TODO. COOLSMS API 전송 로직 추가
				//	1. SMS 전송 및 이력 적재 (임시로 COOLSMS 테스트 계정으로 진행)
				String msg = "[AIEAR 계정 찾기] 귀하의 번호로 가입되어 있는 계정 : " + srchIdInfo.get("user_id").toString();
	
				SMSVO smsVO = new SMSVO();
				smsVO.setSend_msg(msg);
				smsVO.setTo_mobile_no(srchIdInfo.get("mobile_tel_no").toString());
				smsVO.setFrom_mobile_no(COOL_SMS_MOBILE_NO);
				smsVO.setApi_key(COOL_SMS_API_KEY);
				smsVO.setApi_secret(COOL_SMS_API_SECRET);
				
				SMSVO smsRsltVO = SMSUtil.sendSMS(smsVO);
				logger.info("■■■■■■ smsRsltVO : {}", smsRsltVO.toString());
				
				smsDAO.insertSMSSendHst(smsRsltVO);
				
				resVO.setData(srchIdInfo);
				resVO.setMessage("아이디 찾기 성공");
				resVO.setResult(true);
			}
		} catch (Exception e) {
			// TODO: handle exception
			resVO.setStatus(400);
			resVO.setMessage("아이디 찾기 실패");
			res.setStatus(400);
		}
		return resVO;
	}
	

	@ApiOperation(value = "임시 비밀번호 발급"
			, notes = "임시 비밀번호 발급"
					+ "\n 1. user_id"
					+ "<br>		- 필수값"
					+ "\n 1. mobile_tel_no"
					+ "<br> 	- 필수값"
			)
	@PostMapping(value = "searchPwdInfo.do")
	public @ResponseBody ResponseVO searchPwdInfo(
			HttpServletRequest req,
			HttpServletResponse res,
			@RequestBody LoginVO loginVO) {
		
		logger.info("■■■■■■ searchPwdInfo / loginVO : {}", loginVO.beanToHmap(loginVO).toString());
		
		ResponseVO resVO = new ResponseVO();
		
		try {
			
			if(loginVO.getUser_id() == null || "".equals(loginVO.getUser_id())){
				resVO.setMessage("아이디를 입력해 주세요.");
				resVO.setResult(false);
				resVO.setStatus(400);
				res.setStatus(400);
				return resVO;
			}
			if(loginVO.getMobile_tel_no() == null || "".equals(loginVO.getMobile_tel_no().toString())){
				resVO.setMessage("전화번호를 입력해 주세요");
				resVO.setResult(false);
				resVO.setStatus(400);
				res.setStatus(400);
				return resVO;
			}
			
			Map<String, Object> srchIdInfo = loginDAO.searchIdPwdInfo(loginVO);
			
			if(srchIdInfo == null){
				resVO.setMessage(loginVO.getMobile_tel_no() + " / 일치하는 계정이 없습니다.");
				resVO.setResult(false);
				resVO.setStatus(400);
				res.setStatus(400);
			} else {
				//TODO. COOLSMS API 전송 로직 추가
				//	1. 임시 비밀번호 생성 테스트
				String rndPwd = LoginUtil.getRamdomPassword(10);
				srchIdInfo.put("rnd_pwd", rndPwd);
				
				//	2. SMS 전송 및 이력 적재 (임시로 COOLSMS 테스트 계정으로 진행)
				String msg = "[비밀번호 변경] 임시 비밀번호 생성 완료 : " + rndPwd;
				
				SMSVO smsVO = new SMSVO();
				smsVO.setSend_msg(msg);
				smsVO.setTo_mobile_no(srchIdInfo.get("mobile_tel_no").toString());
				smsVO.setFrom_mobile_no(COOL_SMS_MOBILE_NO);
				smsVO.setApi_key(COOL_SMS_API_KEY);
				smsVO.setApi_secret(COOL_SMS_API_SECRET);
				
				SMSVO smsRsltVO = SMSUtil.sendSMS(smsVO);
				logger.info("■■■■■■ smsRsltVO : {}", smsRsltVO.toString());
				
				smsDAO.insertSMSSendHst(smsRsltVO);
				
				//	3. 임시 비밀번호로 업데이트
				//		+ 비밀번호 암호화 처리
				String userSalt = SHA512.getSalt();
				String encPwd = SHA512.sha256(rndPwd, userSalt);
				loginVO.setTemp_pwd(encPwd);
				loginVO.setUser_salt(userSalt);
				Integer rslt = loginDAO.updateHsptTempPwd(loginVO);
				
				if(rslt > 0){
					HospitalInfoVO hVO = new HospitalInfoVO();
					hVO.setHospital_id(loginVO.getUser_id());
					hsptDAO.insertHospitalHst(hVO);
					
					resVO.setData(srchIdInfo);
					resVO.setMessage("임시 비밀번호 발급 성공");
					resVO.setResult(true);
				} else {
					resVO.setMessage("임시 비밀번호 저장 실패");
					resVO.setResult(false);
					resVO.setStatus(400);
					res.setStatus(400);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			resVO.setStatus(400);
			resVO.setMessage("임시 비밀번호 발급 프로세스 오류");
			res.setStatus(400);
		}
		
		return resVO;
	}

	
	public String generateTokenStr(AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getHospitalId(), authRequest.getHospitalPwd())
            );
        } catch (Exception ex) {
            throw new Exception("inavalid username/password");
        }
        return jwtUtil.generateToken(authRequest.getHospitalId(), authRequest.getHospitalPwd(), authRequest.getUserType());
    }
	
	
	public String generateRefreshTokenStr(AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getHospitalId(), authRequest.getHospitalPwd())
            );
        } catch (Exception ex) {
            throw new Exception("inavalid username/password");
        }
        return jwtUtil.generateRefreshToken(authRequest.getHospitalId(), authRequest.getHospitalPwd(), authRequest.getUserType());
    }
	
}
