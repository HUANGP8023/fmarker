package com.test;

import java.util.HashMap;
import java.util.Map;

import com.fm.utils.HttpClientUtils;

public class Test {
	
	public static void main(String[] args) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("aaaaaaa","33333");
		params.put("bbbbbbb","11111");
		params.put("ccccccc","22222");
		System.out.println(HttpClientUtils.httpPost("http://127.0.0.1:8080/fmarker/v1.1/hello",params, "", ""));
	}

}
