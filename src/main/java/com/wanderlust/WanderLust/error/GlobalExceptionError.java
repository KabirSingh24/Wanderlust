package com.wanderlust.WanderLust.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionError {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex){
        ApiError apiError=new ApiError("User Not Found"+ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiError,apiError.getHttpStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String methodArgumentNotValid(MethodArgumentNotValidException ex, Model model){
        StringBuilder errorMessages=new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            errorMessages.append(err.getField())
                    .append(" → ")
                    .append(err.getDefaultMessage())
                    .append("<br>");
        });
         ApiError apiError=new ApiError("Attribute Error "+errorMessages.toString(),HttpStatus.BAD_REQUEST);
        model.addAttribute("error",apiError);
        return "error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String noHandlerFoundException(NoHandlerFoundException ex,Model model){
        ApiError apiError=new ApiError("The requested URL /listings/ was not found on this server"+ex.getMessage(),HttpStatus.NOT_FOUND);
        model.addAttribute("error",apiError);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String allException(Exception ex,Model model){
        ApiError apiError=new ApiError("Something Wrong try Again"+ex.getMessage(),HttpStatus.NOT_FOUND);
        model.addAttribute("error",apiError);
        return "error";
    }

}
