package com.csye6225.lms.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.ContentType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

@Component
public class BasicAuthEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence
            (HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        PrintWriter writer = response.getWriter();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", "You are not logged in");
        writer.println(jsonObject.toString());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("assignment1");
        super.afterPropertiesSet();
    }
}
