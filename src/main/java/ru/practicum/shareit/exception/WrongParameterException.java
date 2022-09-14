package ru.practicum.shareit.exception;

public class WrongParameterException extends RuntimeException{
    public WrongParameterException(String s) {
        super(s);
    }
}