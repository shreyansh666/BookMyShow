package com.BookMyShow.demo.util;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GenericResponse {

    private HttpStatus responseStatus;
    private String status;
    private String message;
    private Object data;

    public ResponseEntity<?> create() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status);
        map.put("message", message);

        if (!ObjectUtils.isEmpty(data)) {
            map.put("data", data);
        }
        return new ResponseEntity<>(map, responseStatus);
    }
}
