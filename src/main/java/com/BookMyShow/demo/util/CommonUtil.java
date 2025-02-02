package com.BookMyShow.demo.util;
import com.BookMyShow.demo.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;


public class CommonUtil {

    public static ResponseEntity<?> createBuildResponse(Object data, HttpStatus status) {

        GenericResponse response = GenericResponse.builder().responseStatus(status).message("success").status("OK")
                .data(data).build();
        return response.create();
    }

    public static ResponseEntity<?> createBuildResponseMessage(String message, HttpStatus status) {

        GenericResponse response = GenericResponse.builder().responseStatus(status).message(message).status(status.toString())
                .build();
        return response.create();
    }

    public static ResponseEntity<?> createErrorResponse(Object data, HttpStatus status) {
        GenericResponse response = GenericResponse.builder().responseStatus(status).status("failed")
                .data(data).build();
        return response.create();
    }

    public static ResponseEntity<?> createErrorResponseMessage(String message, HttpStatus status) {

        GenericResponse response = GenericResponse.builder().responseStatus(status).status("failed").message(message)
                .build();
        return response.create();
    }

    public static UserDetailsImpl getLoggedInUser() {
        try {
            return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
        } catch (Exception e) {
            throw e;
        }
    }

    public static String getUrl(HttpServletRequest request) {
        String apiUrl = request.getRequestURL().toString();
        apiUrl = apiUrl.replace(request.getServletPath(), "");
        return apiUrl;
    }



}

