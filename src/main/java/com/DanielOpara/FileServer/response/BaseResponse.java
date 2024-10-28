package com.DanielOpara.FileServer.response;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class BaseResponse {
    private int statusCode;
    private String message;
    private Object data;
}
