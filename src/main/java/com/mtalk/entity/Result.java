package com.mtalk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private String errorMessage = "";
    private String errorCode = "";
    private Object data;

    public Result(Object data){
        this.data = data;
    }

    public Result(String errorMessage,String errorCode){
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

}
