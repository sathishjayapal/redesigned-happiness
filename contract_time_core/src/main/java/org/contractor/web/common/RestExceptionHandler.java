package org.contractor.web.common;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.contractor.web.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected final ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
                                                                        final HttpHeaders headers,
                                                                        final HttpStatus status, final WebRequest request) {
        logger.info("Bad Request: " + ex.getMessage());
        logger.debug("Bad Request: ", ex);

        final ApiError apiError = message(HttpStatus.BAD_REQUEST, ex);
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected final ResponseEntity<Object>
    handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
                                 final HttpHeaders headers, final HttpStatus status,
                                 final WebRequest request) {
        logger.info("Bad Request: " + ex.getMessage());
        logger.debug("Bad Request: ", ex);
        return handleExceptionInternal(ex, message(HttpStatus.BAD_REQUEST, ex), headers, HttpStatus.BAD_REQUEST, request);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Exception> handleException(Exception e) {
        logger.error("> handleException");
        logger.error("- Exception: ", e);
        logger.error("< handleException");
        return new ResponseEntity<Exception>(e,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @ExceptionHandler(NotFoundException.class)
//    @ResponseStatus(value = HttpStatus.NOT_FOUND)
//    @ResponseBody
//    public ErrorResponse handleNotFoundError(HttpServletRequest req, NotFoundException exception) {
//        final ApiError apiError = message(HttpStatus.CONFLICT, ex);
//        return new ErrorResponse(errors);
//    }

//    @ExceptionHandler(.class)
    protected final ResponseEntity<Object> handleBadRequest(final RuntimeException ex, final WebRequest request) {
        if (ExceptionUtils.getRootCauseMessage(ex).contains("uplicate")) {
            final ApiError apiError = message(HttpStatus.CONFLICT, ex);
            return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.CONFLICT, request);
        }

        final ApiError apiError = message(HttpStatus.BAD_REQUEST, ex);
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private ApiError message(final HttpStatus httpStatus, final Exception ex) {
        final String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        final String devMessage = ex.getClass().getSimpleName();
        // devMessage = ExceptionUtils.getStackTrace(ex);

        return new ApiError(httpStatus.value(), message, devMessage);
    }

    protected ResponseEntity<Object> handleNoHandlerFoundException(final RuntimeException ex, final WebRequest request) {
        logger.warn("Not Found: " + ex.getMessage());

        final ApiError apiError = message(HttpStatus.NOT_FOUND, ex);
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ApiError apiError = new ApiError(status.value(), "Wrong data", ExceptionUtils.getStackTrace(ex));
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity(apiError, HttpStatus.BAD_REQUEST);
    }


}
