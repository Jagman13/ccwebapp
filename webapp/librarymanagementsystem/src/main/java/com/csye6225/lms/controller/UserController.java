package com.csye6225.lms.controller;

import java.io.PrintWriter;
import java.security.Principal;
import java.util.Date;

import com.csye6225.lms.auth.BcryptPasswordEncoderBean;
import com.csye6225.lms.dao.UserRepository;
import com.csye6225.lms.pojo.User;
import com.csye6225.lms.service.CustomUserDetailsService;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class UserController {

	@Autowired
	private Gson gson;
	@Autowired
	private BcryptPasswordEncoderBean bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomUserDetailsService userService ;



	@GetMapping(value = "/")
	public ResponseEntity<String> authenticate() {
		Date date = new Date();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("message", "You are logged in. The current time is " + date.toString());
		return ResponseEntity.ok(gson.toJson(jsonObject));
	}

	@PostMapping(value = "/user/register")
	public ResponseEntity<String> register(@Valid @RequestBody User user) {
		JsonObject jsonObject = new JsonObject();

		if(!userService.validatePassword(user.getPassword())){
			jsonObject.addProperty("message", "Password must be greater than 8 characters with atleast one uppercase, one lowercase, one digit and one special character ");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson(jsonObject));
		}
		String pass= bCryptPasswordEncoder.passwordEncoder().encode(user.getPassword());
		user.setPassword(pass);


		User newuser=userRepository.findByEmail(user.getEmail());

		if (newuser==null){
			User usersaved=userRepository.saveAndFlush(user);
			jsonObject.addProperty("message","You have been registered in system ");

		}else if(newuser != null) {

			jsonObject.addProperty("message", "User already exists");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(gson.toJson(jsonObject));
		}
		return ResponseEntity.ok(gson.toJson(jsonObject));
	}
}