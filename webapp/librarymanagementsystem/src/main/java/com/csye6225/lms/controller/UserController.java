package com.csye6225.lms.controller;

import java.security.Principal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
public class UserController {

	@Autowired
	private Gson gson;

	@GetMapping(value = "/")
	public ResponseEntity<String> authenticate() {
		Date date = new Date();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("message", "You are logged in. The current time is " + date.toString());
		return ResponseEntity.ok(gson.toJson(jsonObject));
	}
}