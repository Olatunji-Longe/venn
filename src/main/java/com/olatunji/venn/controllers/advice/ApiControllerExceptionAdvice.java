package com.olatunji.venn.controllers.advice;

import com.olatunji.venn.controllers.common.ApiErrorResponse;
import com.olatunji.venn.exceptions.BadRequestException;
import com.olatunji.venn.exceptions.InternalServerException;
import com.olatunji.venn.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class ApiControllerExceptionAdvice {

  @ExceptionHandler({ResourceNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiErrorResponse notFoundExceptionHandler(
      Exception ex, HttpServletRequest request, HttpServletResponse response) {
    return this.errorResponseOf(request, response, HttpStatus.NOT_FOUND, ex);
  }

  @ExceptionHandler({BadRequestException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrorResponse badRequestExceptionHandler(
      Exception ex, HttpServletRequest request, HttpServletResponse response) {
    return this.errorResponseOf(request, response, HttpStatus.BAD_REQUEST, ex);
  }

  // Handles Internal Server Exceptions & all other non-explicitly handled exceptions
  @ExceptionHandler({InternalServerException.class, Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiErrorResponse internalServerExceptionHandler(
      Exception ex, HttpServletRequest request, HttpServletResponse response) {
    return this.errorResponseOf(request, response, HttpStatus.INTERNAL_SERVER_ERROR, ex);
  }

  private ApiErrorResponse errorResponseOf(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final HttpStatus httpStatus,
      final Exception ex) {
    logErrorMessage(ex, httpStatus);
    response.setStatus(httpStatus.value());
    var errorMessage =
        HttpStatus.INTERNAL_SERVER_ERROR.equals(httpStatus)
            ? HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
            : ExceptionUtils.getRootCauseMessage(ex);
    return ApiErrorResponse.from(request, httpStatus, errorMessage);
  }

  private static void logErrorMessage(final Exception ex, final HttpStatus httpStatus) {
    if (httpStatus.is5xxServerError()) {
      log.error(ex.getMessage(), ex);
    } else {
      log.warn(ex.getMessage());
    }
  }
}
