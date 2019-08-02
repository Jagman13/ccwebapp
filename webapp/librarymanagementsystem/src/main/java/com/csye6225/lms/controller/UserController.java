package com.csye6225.lms.controller;

import java.util.Date;
import java.util.logging.Handler;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.csye6225.lms.dao.UserRepository;
import com.csye6225.lms.pojo.RestApiError;
import com.csye6225.lms.pojo.User;
import com.csye6225.lms.service.CustomUserDetailsService;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final static Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private StatsDClient statsDClient;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomUserDetailsService userService ;

	@Autowired
	private Environment environment;

	@Value("${aws.sns.topic.ARN}")
	private String topicArn;

	@GetMapping(value = "/")
	public ResponseEntity<String> authenticate() {
		statsDClient.incrementCounter("endpoint.login.http.get");
		logger.info("Getting User authenticated");
		Date date = new Date();
		Gson gson= new Gson();
		JsonObject jsonObject = new JsonObject();
		String[] profile= environment.getActiveProfiles();
		jsonObject.addProperty("message", "You are logged in. The current time is " + date.toString() + " .Environment is: " + profile[0]);
		logger.info("User successfully authenticated");
		return ResponseEntity.ok(gson.toJson(jsonObject));
	}

	@PostMapping(value = "/user/register")
	public ResponseEntity<String> register(@Valid @RequestBody User user) {
		statsDClient.incrementCounter("endpoint.user.http.post");
		logger.info("Getting User registered");
		Gson gson= new Gson();
		JsonObject jsonObject = new JsonObject();

		if(!userService.validatePassword(user.getPassword())){
			logger.error("Password not accepted.Password must be greater than 8 characters with atleast one uppercase, one lowercase, one digit and one special character" );
			logger.info("User Registered Response code: " + HttpStatus.BAD_REQUEST);
			jsonObject.addProperty("message", "Password must be greater than 8 characters with atleast one uppercase, one lowercase, one digit and one special character ");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(gson.toJson(jsonObject));
		}
		String pass= bCryptPasswordEncoder.encode(user.getPassword());
		user.setPassword(pass);


		User newuser=userRepository.findByEmail(user.getEmail());

		if (newuser==null){
			userRepository.saveAndFlush(user);
			logger.info("User successfully registered");

			jsonObject.addProperty("message","You have been registered in system ");

		}else if(newuser != null) {
			logger.error("User already exist");
			logger.info("User Registered Response code: " + HttpStatus.CONFLICT);
			jsonObject.addProperty("message", "User already exists");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(gson.toJson(jsonObject));
		}
		logger.info("User Registered Response code: " + HttpStatus.CREATED);
		return ResponseEntity.status(HttpStatus.CREATED).body(gson.toJson(jsonObject));
	}

	@PostMapping(value = "/reset")
	public ResponseEntity<Object> resetPassword(@RequestParam(name = "email") String email){
		if (email.isEmpty()){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestApiError("Validation Failed", "Please enter a valid email address"));
		}
		User user=userRepository.findByEmail(email);
		if (user==null){
			logger.info("User does not exist in the system");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new RestApiError("Validation Failed", "User does not exist in the system"));
		}
		// Publish a message to an Amazon SNS topic.
		final PublishRequest publishRequest = new PublishRequest(topicArn, email);
		AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withCredentials(new InstanceProfileCredentialsProvider(false)).build();
		final PublishResult publishResponse = snsClient.publish(publishRequest);

		// Print the MessageId of the message.
		System.out.println("MessageId: " + publishResponse.getMessageId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}