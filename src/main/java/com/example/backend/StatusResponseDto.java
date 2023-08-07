package com.example.backend;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatusResponseDto {
    private String msg;
    private boolean isSuccess;
    public StatusResponseDto(String msg){
        this.msg=msg;
    }
    public StatusResponseDto(String msg, Boolean isSuccess){
        this.msg=msg;
        this.isSuccess=isSuccess;
    }
}
