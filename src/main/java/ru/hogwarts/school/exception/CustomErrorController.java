package ru.hogwarts.school.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

    @Autowired
    private MessageSource messageSource;

    @RequestMapping("/error")
    public Map<String, Object> handleError(HttpServletRequest request, Locale locale) {
        Object statusAttr = request.getAttribute("jakarta.servlet.error.status_code");
        int status = statusAttr != null ? Integer.parseInt(statusAttr.toString()) : 500;

        if (status == HttpStatus.NOT_FOUND.value()) {
            String msg = messageSource.getMessage("error.404.static", null, messageSource.getMessage("error.404", null, "Not found", locale), locale);
            return Map.of(
                    "status", status,
                    "message", msg
            );
        }

        String msg = messageSource.getMessage("error.internal", null, "Internal server error", locale);
        return Map.of(
                "status", status,
                "message", msg
        );
    }
}
