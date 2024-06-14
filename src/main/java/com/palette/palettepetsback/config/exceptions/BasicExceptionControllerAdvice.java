package com.palette.palettepetsback.config.exceptions;

import com.palette.palettepetsback.config.exceptions.exception.BasicLoginIOException;
import com.palette.palettepetsback.config.exceptions.exception.NotAuthenticatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(1)
public class BasicExceptionControllerAdvice {

    // 회원 / 펫을 찾지 못하는 에러가 발생하였을 때 반환할 에러 응답
    @ExceptionHandler({ NoMemberExistException.class, NoSuchPetException.class })
    public ResponseEntity<?> handleNoMemberExistException(NoMemberExistException e) {
        log.error("NoMemberExistException = {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 로그인 IO 에러 응답
    @ExceptionHandler({ BasicLoginIOException.class })
    public ResponseEntity<?> basicLoginIOException(BasicLoginIOException e) {
        log.error("BasicLoginIOException = {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE); // 406 error
    }

    // 레디스 연결 에러 응답
    @ExceptionHandler({ RedisConnectionFailureException.class })
    public ResponseEntity<?> redisConnectionFailureException(RedisConnectionFailureException e) {
        log.error("RedisConnectionFailureException = {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NETWORK_AUTHENTICATION_REQUIRED); // 511 error
    }

    // 존재하지 않는 시큐리티 인증 정보 조회 에러
    @ExceptionHandler({ NotAuthenticatedException.class })
    public ResponseEntity<?> notAuthenticatedException(NotAuthenticatedException e) {
        log.error("NotAuthenticatedException = {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 error
    }
}
