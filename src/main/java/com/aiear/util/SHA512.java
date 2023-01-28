package com.aiear.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class SHA512 {

//  public static void main(String[] args) {
//		String pw = "password";
//      String salt = getSalt();
//      String pwSalt = sha256(pw, salt);
//      System.out.println(pwSalt);
//	}
	
	public String hash(String data_hash) {
		String salt = data_hash;
		String hex = null;

		try {
			MessageDigest msg = MessageDigest.getInstance("SHA-512");
			msg.update(salt.getBytes());
			hex = String.format("%0128x", new BigInteger(1, msg.digest()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return hex;
	}
	
	
	/**
	 * Description	: 
	 * @method		: getSalt
	 * @author		: pcw
	 * @date		: 2023. 1. 28.
	 * @return
	 */
	public static String getSalt(){
		
        // 보안 이슈로 Random이 아닌 SecureRandom을 사용해 주는것이 좋다.
        SecureRandom secureRandom = new SecureRandom();
        
		byte[] salt = new byte[20];
		secureRandom.nextBytes(salt); // 난수 생성
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < salt.length; i++) {
			sb.append(String.format("%2X", salt[i]));
		}
		return sb.toString();
	}
    
	
    /**
     * Description	: 
     * @method		: sha256
     * @author		: pcw
     * @date		: 2023. 1. 28.
     * @param pw
     * @param salt
     * @return
     */
    public static String sha256(String pw, String salt) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update((pw + salt).getBytes());
		} catch(Exception e) {
			e.printStackTrace();
		}
		byte[] pwSalt = md.digest();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < pwSalt.length; i++) {
			sb.append(String.format("%2X", pwSalt[i])); 
		}
		return sb.toString();
	}
	
}

