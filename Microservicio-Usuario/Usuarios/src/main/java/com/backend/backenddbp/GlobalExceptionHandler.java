package com.backend.backenddbp;


import com.backend.backenddbp.Chat.Exceptions.ChatNotFound;
import com.backend.backenddbp.Chat.Exceptions.NotAllowedPermissionChat;
import com.backend.backenddbp.Friendship.Exceptions.NotFriendException;

import com.backend.backenddbp.Mensaje.Exceptions.MessageException;
import com.backend.backenddbp.Security.Auth.Exceptions.UnauthorizedException;
import com.backend.backenddbp.Security.Utils.ErrorDetail;
import com.backend.backenddbp.User.Exceptions.BadCredentialException;
import com.backend.backenddbp.User.Exceptions.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetail> UserExceptionHandler(UserException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorDetail> MessageExceptionHandler(MessageException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<ErrorDetail> BadCredentialExceptionHandler(BadCredentialException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(NotAllowedPermissionChat.class)
    public ResponseEntity<ErrorDetail> ChatNotFoundHandler(NotAllowedPermissionChat e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ChatNotFound.class)
    public ResponseEntity<ErrorDetail> ChatNotFoundHandler(ChatNotFound e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFriendException.class)
    public ResponseEntity<ErrorDetail> NotFriendExceptionHandler(NotFriendException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDetail> UnauthorizedExceptionHandler(UnauthorizedException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }

}
