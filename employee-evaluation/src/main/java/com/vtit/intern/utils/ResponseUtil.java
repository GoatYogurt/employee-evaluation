package com.vtit.intern.utils;

import com.vtit.intern.dtos.responses.ResponseDTO;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static <T> ResponseEntity<ResponseDTO<T>> success(T data) {
        return ResponseEntity.ok(ResponseDTO.<T>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(data)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> success(String message) {
        return ResponseEntity.ok(ResponseDTO.<T>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> created(T data) {
        return ResponseEntity.ok(ResponseDTO.<T>builder()
                .code(HttpStatus.OK.value())
                .message("Created")
                .data(data)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> created(String message) {
        return ResponseEntity.ok(ResponseDTO.<T>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> deleted(String message) {
        return ResponseEntity.ok(ResponseDTO.<T>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> error(HttpStatus status, String message) {
        return ResponseEntity.badRequest().body(ResponseDTO.<T>builder()
                .code(status.value())
                .message(message)
                .build());
    }
}
