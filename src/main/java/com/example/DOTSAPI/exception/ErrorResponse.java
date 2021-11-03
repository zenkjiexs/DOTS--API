package com.example.DOTSAPI.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
@Getter @Setter
public class ErrorResponse
{
    private HttpStatus status;
    private String path;
    //General error message about nature of error
    private String message;

    //Specific errors in API request processing
    private String detail;

    //Getter and setters
    public ErrorResponse(HttpStatus status) {
        super();
        this.status = status;
    }

}