package com.fm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fm.pojo.User;

@Controller
@RequestMapping(value="/fmControl")
public class FmController extends BaseController {
	
	private final static Log log = LogFactory.getLog(FmController.class);

	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String fmIndex(HttpServletRequest request,HttpServletResponse response) throws Exception{
		User u=new User(0,"admin","admin123");
		request.setAttribute("u",u);
		log.info("1111111111111111111111");
		return "manIndex";
	}
	
	
	
	
	
}
