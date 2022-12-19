package com.aiear.config.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @packageName : com.aiear.config.session
 * @fileName	: AuthRequest.java
 * @author		: pcw
 * @date		: 2022. 11. 28.
 * @description	:
 * ===============================================
 * DATE				AUTHOR			NOTE
 * 2022. 11. 28.		pcw				최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    private String hospitalId;
    private String hospitalPwd;
    private String userType;
}