package com.vtit.intern.utils;

import com.vtit.intern.dtos.responses.ResponseDTO;

public class ResponseUtil {
    public static <T> ResponseDTO<T> success(T data) {
        return ResponseDTO.<T>builder()
                .code("200")
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ResponseDTO<T> success(String message) {
        return ResponseDTO.<T>builder()
                .code("200")
                .message(message)
                .build();
    }

    public static <T> ResponseDTO<T> error(String code, String message) {
        return ResponseDTO.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    public static <T> ResponseDTO<T> created(T data) {
        return ResponseDTO.<T>builder()
                .code("201")
                .message("Created")
                .data(data)
                .build();
    }

    public static <T> ResponseDTO<T> deleted() {
        return ResponseDTO.<T>builder()
                .code("200")
                .message("Deleted")
                .build();
    }
}
