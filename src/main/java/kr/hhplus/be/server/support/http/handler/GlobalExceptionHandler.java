package kr.hhplus.be.server.support.http.handler;

import kr.hhplus.be.server.domain.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.domain.coupon.exception.CouponNotUsableException;
import kr.hhplus.be.server.domain.point.exception.PointLimitExceededException;
import kr.hhplus.be.server.domain.point.exception.PointNotEnoughException;
import kr.hhplus.be.server.support.http.response.ApiResponse;
import kr.hhplus.be.server.support.http.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> notFoundException(ResourceNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(e.getResponseCode()));
    }

    @ExceptionHandler(CouponNotUsableException.class)
    public ResponseEntity<ApiResponse<Void>> couponNotUsableException(CouponNotUsableException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.failure(ResponseCode.COUPON_INVALID));
    }

    @ExceptionHandler(PointLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> pointLimitExceededException(PointLimitExceededException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(ResponseCode.MAXIMUM_POINT_LIMIT_EXCEEDED));
    }

    @ExceptionHandler(PointNotEnoughException.class)
    public ResponseEntity<ApiResponse<Void>> pointNotEnoughException(PointNotEnoughException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(ResponseCode.POINT_NOT_ENOUGH));
    }

}
