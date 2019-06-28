package com.csye6225.lms.controller;

import java.util.Date;

import com.csye6225.lms.dao.UserRepository;
import com.csye6225.lms.pojo.User;
import com.csye6225.lms.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.validation.Valid;

@RestController
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomUserDetailsService userService ;

	@Autowired
	private Environment environment;



	@GetMapping(value = "/")
	public ResponseEntity<String> authenticate() {
		Date date = new Date();
		Gson gson= new Gson();
		JsonObject jsonObject = new JsonObject();
		String[] profile= environment.getActiveProfiles();
		jsonObject.addProperty("message", "You are logged in. The current time is " + date.toString() + "environment: " + profile[0]);
		return ResponseEntity.ok(gson.toJson(jsonObject));
	}

	@PostMapping(value = "/user/register")
	public ResponseEntity<String> register(@Valid @RequestBody User user) {
		Gson gson= new Gson();
		JsonObject jsonObject = new JsonObject();

		if(!userService.validatePassword(user.getPassword())){
			jsonObject.addProperty("message", "Password must be greater than 8 characters with atleast one uppercase, one lowercase, one digit and one special character ");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson(jsonObject));
		}
		String pass= bCryptPasswordEncoder.encode(user.getPassword());
		user.setPassword(pass);


		User newuser=userRepository.findByEmail(user.getEmail());

		if (newuser==null){
			userRepository.saveAndFlush(user);
			jsonObject.addProperty("message","You have been registered in system ");

		}else if(newuser != null) {

			jsonObject.addProperty("message", "User already exists");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(gson.toJson(jsonObject));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(gson.toJson(jsonObject));
	}
}