package com.vtit.intern.dtos.responses;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ResponseDTO<T> implements Serializable {
    private Integer code;
    private String message;
    private transient T data;
}
