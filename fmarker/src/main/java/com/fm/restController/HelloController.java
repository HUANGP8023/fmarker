package com.fm.restController;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;  

import com.fm.common.ApiVersion;
import com.fm.controller.BaseController;
   
@RequestMapping("{version}")  
@RestController  
public class HelloController extends BaseController {  
  
    @ApiVersion(1.1)  
    @RequestMapping("hello")  
    public @ResponseBody Map<String,Object> hello1(){  
    	Map<String,Object> resMap=new HashMap<String,Object>();
    	resMap.put("errorCode", "0");
    	resMap.put("errorMsg","hello1");
        return resMap;
    }  
      
    @ApiVersion(1.2)  
    @RequestMapping("hello")  
    public @ResponseBody Map<String,Object> hello2(){  
    	Map<String,Object> resMap=new HashMap<String,Object>();
    	resMap.put("errorCode", "0");
    	resMap.put("errorMsg","hello2");
        return resMap;
    }  
}  