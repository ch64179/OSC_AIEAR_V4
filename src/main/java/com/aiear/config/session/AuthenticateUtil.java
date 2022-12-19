/**
 * 
 */
package com.aiear.config.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @packageName : com.aiear.config.session
 * @fileName	: AuthenticateUtil.java
 * @author		: pcw
 * @date		: 2022. 11. 29.
 * @description	:
 * ===============================================
 * DATE				AUTHOR			NOTE
 * 2022. 11. 29.		pcw				최초 생성
 */
public class AuthenticateUtil {

	@Autowired
    private static AuthenticationManager authenticationManager;
	
	@Autowired
    private static JwtUtil jwtUtil;
	
	public static String generateToken(String hospitalId, String hospitalPwd) throws Exception {
		
		AuthRequest authRequest = new AuthRequest();
		authRequest.setHospitalId(hospitalId);
		authRequest.setHospitalPwd(hospitalPwd);
		
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getHospitalId(), authRequest.getHospitalPwd())
            );
        } catch (Exception ex) {
            throw new Exception("inavalid username/password");
        }
        return jwtUtil.generateToken(authRequest.getHospitalId(), authRequest.getHospitalPwd(), authRequest.getUserType());
    }
	
}
