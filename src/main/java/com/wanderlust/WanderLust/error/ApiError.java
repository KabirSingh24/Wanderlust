package com.wanderlust.WanderLust.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiError{

    private LocalDateTime time;
    private String error;
    private HttpStatus httpStatus;

    public ApiError(){this.time=LocalDateTime.now();}

    public ApiError(String error,HttpStatus httpStatus){
        this();
        this.error=error;
        this.httpStatus=httpStatus;
    }

}
