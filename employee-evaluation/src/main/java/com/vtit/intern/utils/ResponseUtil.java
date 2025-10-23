package com.vtit.intern.utils;

import com.vtit.intern.dtos.responses.ResponseDTO;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    public static <T> ResponseEntity<ResponseDTO<T>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.<T>builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(message)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> alreadyExists(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO.<T>builder()
                .code(HttpStatus.CONFLICT.value())
                .message(message)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> badRequest(String message) {
        return ResponseEntity.badRequest().body(ResponseDTO.<T>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build());
    }

    public static  <T> ResponseEntity<ResponseDTO<T>> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.<T>builder()
                .code(HttpStatus.FORBIDDEN.value())
                .message(message)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO.<T>builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .build());
    }

    public static <T> ResponseEntity<ResponseDTO<T>> error(HttpStatus status, String message) {
        return ResponseEntity.badRequest().body(ResponseDTO.<T>builder()
                .code(status.value())
                .message(message)
                .build());
    }

    public static ResponseEntity<InputStreamResource> downloadFile(String fileName, InputStreamResource resource) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
