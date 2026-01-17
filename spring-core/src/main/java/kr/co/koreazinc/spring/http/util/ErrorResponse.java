package kr.co.koreazinc.spring.http.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationResult;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import kr.co.koreazinc.spring.http.enhancer.HttpEnhancer;
import kr.co.koreazinc.spring.support.SuppressWarning;
import kr.co.koreazinc.spring.utility.MessageUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    @Schema(description = "타임스탬프")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "상태 코드")
    private HttpStatus status;

    @Schema(description = "Path")
    private String path;

    @Schema(description = "Error")
    private String error;

    @Schema(description = "Trace")
    private String trace;

    @Schema(description = "메시지")
    private String message;

    public static class ErrorResponseBuilder {

        public ErrorResponseBuilder exception(Map<String, Object> errorAttributes, Throwable exception) {
            if (exception != null) {
                while (exception instanceof ServletException && exception.getCause() != null) {
                    exception = exception.getCause();
                }
                this.error = exception.getClass().getName();
                this.trace = addStackTrace(exception);
            }
            this.message = addErrorMessage(errorAttributes, exception);
            return this;
        }

        private String addStackTrace(Throwable exception) {
            try (StringWriter stackTrace = new StringWriter()) {
                exception.printStackTrace(new PrintWriter(stackTrace));
                stackTrace.flush();
                return stackTrace.toString();
            } catch (Exception e) {
                return null;
            }
        }

        private String addErrorMessage(Map<String, Object> errorAttributes, Throwable error) {
            if (error instanceof BindingResult bindingResult) {
                return String.format("Validation failed for object='%s'. Error count: %d", bindingResult.getObjectName(), bindingResult.getAllErrors());
            }
            if (error instanceof MethodValidationResult methodValidationResult) {
                List<ObjectError> errors = methodValidationResult.getAllErrors()
                    .stream()
                    .filter(ObjectError.class::isInstance)
                    .map(ObjectError.class::cast)
                    .toList();
                return String.format("Validation failed for method='%s'. Error count: %d", methodValidationResult.getMethod(), errors.size());
            }
            if (errorAttributes.containsKey(RequestDispatcher.ERROR_MESSAGE)) {
                return getAttribute(errorAttributes, RequestDispatcher.ERROR_MESSAGE);
            }
            return Optional.ofNullable(error).map(Throwable::getMessage).orElseGet(()->MessageUtils.getMessage("exception.default", Locale.getDefault()));
        }
    }

    public static ErrorResponse of(HttpEnhancer.Servlet.Request request) {
        Map<String, Object> errorAttributes = request.getAttributes();
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(getStatus(errorAttributes))
            .path(getPath(errorAttributes))
            .exception(errorAttributes, getException(errorAttributes))
            .build();
    }

    public static ErrorResponse of(HttpEnhancer.Reactive.Request request) {
        Map<String, Object> errorAttributes = request.getAttributes();
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(getStatus(errorAttributes))
            .path(getPath(errorAttributes))
            .exception(errorAttributes, getException(errorAttributes))
            .build();
    }


    @SuppressWarnings(SuppressWarning.UNCHECKED)
    private static <T> T getAttribute(Map<String, Object> errorAttributes, String key) {
        return (T) errorAttributes.get(key);
    }

    @SuppressWarnings(SuppressWarning.UNCHECKED)
    private static <T> T getAttributeOrDefault(Map<String, Object> errorAttributes, String key, T defaultValue) {
        return (T) errorAttributes.getOrDefault(key, defaultValue);
    }

    private static HttpStatus getStatus(Map<String, Object> errorAttributes) {
        try {
            return HttpStatus.resolve(getAttribute(errorAttributes, RequestDispatcher.ERROR_STATUS_CODE));
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private static String getPath(Map<String, Object> errorAttributes) {
        return getAttribute(errorAttributes, RequestDispatcher.ERROR_REQUEST_URI);
    }

    private static Throwable getException(Map<String, Object> errorAttributes) {
        return getAttributeOrDefault(errorAttributes, RequestDispatcher.ERROR_EXCEPTION, new Exception(MessageUtils.getMessage("exception.default", Locale.getDefault())));
    }
}