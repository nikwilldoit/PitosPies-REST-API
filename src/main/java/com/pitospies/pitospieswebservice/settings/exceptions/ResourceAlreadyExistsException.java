package com.pitospies.pitospieswebservice.settings.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException{
    public ResourceAlreadyExistsException(String message){
        super(message);
    }
}
