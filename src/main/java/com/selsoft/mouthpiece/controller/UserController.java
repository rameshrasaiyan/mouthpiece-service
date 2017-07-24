package com.selsoft.mouthpiece.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.selsoft.mouthpiece.models.User;
import com.selsoft.mouthpiece.service.UserService;

@RestController("/user")
@Controller
public class UserController {
	
	@Autowired
	private UserService userService = null;
	
	@RequestMapping(value="/signUp", method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public User newUserSignUp(@RequestBody User user) {
		if(user == null) return null;
		userService.saveNewUser(user);
		return user;
	}

}
