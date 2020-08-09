package com.springboot.prj;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PrintDecryptedValue {
	
	@Value("${pass1}")
	private String pass1;
	
	@Autowired private Environment env;
	
	@PostConstruct
	public void showValues() {
		log.info("@Value decypted value : " + pass1);
		log.info("@Env decypted value : " + env.getProperty("pass2"));
	}
}
