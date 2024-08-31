package com.library.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.ApiException;
import com.library.NaverErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.library.ErrorType.EXTERNAL_API_ERROR;

@Slf4j
public class NaverErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    public NaverErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            NaverErrorResponse errorResponse = objectMapper.readValue(body, NaverErrorResponse.class);
            throw new ApiException(errorResponse.errorMessage(), EXTERNAL_API_ERROR, HttpStatus.valueOf(response.status()));
        } catch (IOException e) {
            log.error("[Naver] 에러 메시지 파싱 에러 code={}, request={}, methodKey={}, errorMessage={}", response.status(), response.request(), methodKey, e.getMessage());
            throw new ApiException("네이버 메시지 파싱에러", EXTERNAL_API_ERROR, HttpStatus.valueOf(response.status()));
        }
    }
}
