package com.aiear.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aiear.vo.CommonCdVO;
import com.aiear.vo.ResponseVO;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/aiear/*")
@RequiredArgsConstructor
public class AiearAPICont {
	
	/** 로그 처리용 개체 선언 */
	protected Logger logger = LogManager.getLogger(getClass());
	
	
	@RequestMapping(value = "oscPostTest.do")
	@ApiOperation(value = "Swagger 테스트 API", notes = "Swagger Test용 REST 생성")
	public @ResponseBody ResponseVO oscPostTest(
			HttpServletRequest req,
			HttpServletResponse res,
			@RequestBody CommonCdVO commonVO
		) {
		
		ResponseVO resVO = new ResponseVO();
		
		resVO.add("name", "OSC_USER");
		resVO.add("age", "31");
		resVO.add("email", "O1342@test.com");
		resVO.add("address", "12345 지번 23-2-3, 특수문자 #${}@{}%+=16=4$+%$)_#%)(!&<>? ${}");
		resVO.add("phone_no", "010-1234-5678s");
		
		resVO.setReqData(commonVO.beanToHmap(commonVO));
		
		return resVO;
	}
	
}
