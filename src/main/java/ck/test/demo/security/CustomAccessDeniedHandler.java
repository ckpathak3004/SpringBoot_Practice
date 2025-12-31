package ck.test.demo.security;
import ck.test.demo.service.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // Set the HTTP Status code to 403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Set the response content type to JSON
        response.setContentType("application/json");

        // Create your custom error response body
        // Assuming you defined the ErrorResponse class previously
        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_FORBIDDEN,
                "You do not have sufficient permissions to access this resource."
        );

        // Use ObjectMapper to write the ErrorResponse object as JSON to the response output stream
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}