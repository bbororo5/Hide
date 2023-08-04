package com.example.backend;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatusResponseDto {
    private String msg;
    public StatusResponseDto(String msg){
        this.msg=msg;
    }
}
