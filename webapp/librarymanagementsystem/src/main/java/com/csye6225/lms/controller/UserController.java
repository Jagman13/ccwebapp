package com.csye6225.lms.controller;

import java.io.PrintWriter;
import java.security.Principal;
import java.util.Date;

import com.csye6225.lms.dao.UserRepository;
import com.csye6225.lms.pojo.User;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;

@RestController
public class UserController {

	@Autowired
	private Gson gson;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;


	@GetMapping(value = "/")
	public ResponseEntity<String> authenticate() {
		Date date = new Date();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("message", "You are logged in. The current time is " + date.toString());
		return ResponseEntity.ok(gson.toJson(jsonObject));
	}

	@PostMapping(value = "/user/register")
	public ResponseEntity<String> register(@ModelAttribute User user) {
		String email=user.getEmail();
		String password=user.getPassword();
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		User newuser=userRepository.findByEmail(email);
		JsonObject jsonObject = new JsonObject();
		if (newuser==null){
			User usersaved=userRepository.saveAndFlush(user);
			jsonObject.addProperty("message","You have been registered in system ");
			jsonObject.addProperty("username",email);

		}else if(newuser != null) {
			jsonObject.addProperty("message", "User already exists");
		}
		return ResponseEntity.ok(gson.toJson(jsonObject));
	}

}