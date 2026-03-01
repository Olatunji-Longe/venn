package com.olatunji.venn.controllers.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiErrorResponse {

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private final LocalDateTime timestamp;

  private final String path;
  private final HttpStatus status;
  private final String message;

  private static String buildStatusMessage(HttpStatus httpStatus) {
    if (null != httpStatus) {
      return String.format("%s [%s]", httpStatus.value(), httpStatus.getReasonPhrase());
    } else {
      return null;
    }
  }

  public static ApiErrorResponse from(
      HttpServletRequest servletRequest, HttpServletResponse servletResponse, Throwable throwable) {
    return new ApiErrorResponse(
        LocalDateTime.now(ZoneId.systemDefault()),
        servletRequest.getRequestURI(),
        Optional.ofNullable(servletResponse)
            .map(response -> HttpStatus.resolve(response.getStatus()))
            .orElse(HttpStatus.INTERNAL_SERVER_ERROR),
        ExceptionUtils.getRootCauseMessage(throwable));
  }

  public static ApiErrorResponse from(
      HttpServletRequest servletRequest, HttpStatus httpStatus, String message) {
    return new ApiErrorResponse(
        LocalDateTime.now(ZoneId.systemDefault()),
        servletRequest.getRequestURI(),
        httpStatus,
        StringUtils.defaultIfBlank(message, buildStatusMessage(httpStatus)));
  }
}
