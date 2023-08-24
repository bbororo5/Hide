package com.example.backend.util.execption;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CustomResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return (statusCode.is4xxClientError() || statusCode.is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode.is5xxServerError()) {
            // 5xx 에러 처리
            throw new HttpServerErrorException(statusCode, "서버에서 오류 발생");
        } else if (statusCode.is4xxClientError()) {
            // 401 에러 처리
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                throw new HttpClientErrorException(statusCode, "인증 오류 발생");
            }
            // 기타 4xx 에러
            throw new HttpClientErrorException(statusCode, "클라이언트 오류 발생");
        }
    }
}