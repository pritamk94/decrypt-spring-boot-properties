package com.springboot.prj;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;
/**
 * @author Pritam
 *
 */
@Slf4j
public class AESUtil {
 
    private static SecretKeySpec secretKey;
    
    public static void setMyKey(String mySecretKey) 
    {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			secretKey =  new SecretKeySpec(md.digest(mySecretKey.getBytes("UTF-8")), "AES");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			log.error("Error occured while setting the key  : " + e.getMessage());
			e.printStackTrace();
		}
    }
 
    public static String encrypt(String strToEncrypt, String secret) 
    {
        try
        {
        	setMyKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } 
        catch (Exception e) 
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
 
    public static String decrypt(String strToDecrypt, String secret) 
    {
        try
        {
        	setMyKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } 
        catch (Exception e) 
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
    public static void main(String[] args) {
    	System.out.println(encrypt("World War 2","keySecretPassword"));
	}
}