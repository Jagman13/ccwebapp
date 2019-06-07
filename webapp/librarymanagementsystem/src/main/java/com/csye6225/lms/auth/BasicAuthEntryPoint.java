package com.csye6225.lms.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.csye6225.lms.dao.UserRepository;
import com.csye6225.lms.pojo.User;
import org.apache.http.entity.ContentType;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

@Component
public class BasicAuthEntryPoint extends BasicAuthenticationEntryPoint {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BcryptPasswordEncoderBean bCryptPasswordEncoder;

    @Override
    public void commence
            (HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException, ServletException {
        PrintWriter writer = response.getWriter();
        JsonObject jsonObject = new JsonObject();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        String header = request.getHeader("Authorization") ;
        if(null != header){
            final String[] userDetails = decode(header);
            User user = userRepository.findByEmail(userDetails[0]);
            if (user == null) {
                jsonObject.addProperty("message", "You are not registered");
            }else{
                if(!bCryptPasswordEncoder.passwordEncoder().encode(userDetails[1]).equals(user.getPassword())){
                    jsonObject.addProperty("message", "Password is incorrect");
                }
            }
        }else{
            jsonObject.addProperty("message", "You are not logged in");

        }
        writer.println(jsonObject.toString());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("assignment1");
        super.afterPropertiesSet();
    }

    private static String[] decode(final String encoded) {

        String[] authParts = encoded.split("\\s+");
        String authInfo = authParts[1];
        final byte[] decodedBytes
                = Base64.decodeBase64(authInfo.getBytes());
        final String pair = new String(decodedBytes);
        final String[] userDetails = pair.split(":", 2);
        return userDetails;
    }

}
