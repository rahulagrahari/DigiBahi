package com.wonkmonk.digikhata.userauth.securityFilters;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    @Override
    public void commence
            (HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
//        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        class ResponseTextWrapper{
            String status;
            String response;
        }
        ResponseTextWrapper failedResponse = new ResponseTextWrapper();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        failedResponse.status = "401";
        failedResponse.response = "Authentication Required.";

        String json = mapper.writeValueAsString(failedResponse);
        response.getWriter().write(json);
        response.flushBuffer();
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("DigiBahi");
        super.afterPropertiesSet();
    }
}
