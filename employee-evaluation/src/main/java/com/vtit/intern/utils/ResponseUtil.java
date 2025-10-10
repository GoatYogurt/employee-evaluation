package com.vtit.intern.utils;

import com.vtit.intern.dtos.responses.ResponseDTO;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static <T> ResponseEntity<ResponseDTO<T>> success(T data) {
        return ResponseEntity.ok(ResponseDTO.<T>builder()
                .code("200")
                .message("Success")
                .data(data)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> success(String message) {
        return ResponseEntity.ok(ResponseDTO.<T>builder()
                .code("200")
                .message(message)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> created(T data) {
        return ResponseEntity.ok(ResponseDTO.<T>builder()
                .code("200")
                .message("Created")
                .data(data)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> deleted() {
        return ResponseEntity.ok(ResponseDTO.<T>builder()
                .code("200")
                .message("Deleted")
                .build());
    }
}
