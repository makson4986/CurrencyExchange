package org.makson.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ErrorResponseDto;
import org.makson.exception.*;

import java.io.IOException;

@WebFilter("/*")
public class ErrorFilter extends HttpFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            handleException((HttpServletResponse) servletResponse, e);
        }
    }

    private void handleException(HttpServletResponse servletResponse, Exception e) throws IOException {
        Throwable exception = e.getCause();

        switch (exception) {
            case DataNotFoundException dataNotFoundException ->
                    handleException(servletResponse, 404, exception.getMessage());
            case DataAlreadyExistException dataAlreadyExistException ->
                    handleException(servletResponse, 409, exception.getMessage());
            case InvalidParameterException parameterNotFoundException ->
                    handleException(servletResponse, 400, exception.getMessage());
            case NumberFormatException numberFormatException ->
                    handleException(servletResponse, 400, exception.getMessage());
            case InvalidCurrencyCodeException invalidCurrencyCodeException ->
                    handleException(servletResponse, 400, "This currency does not exist");
            case CurrencyCodeMissingException currencyCodeMissingException ->
                    handleException(servletResponse, 400, "Currency code(s) missing from address");
            case null, default ->
                    handleException(servletResponse, 500, "Something went wrong on the server. Please try again later");
        }
    }

    private void handleException(HttpServletResponse servletResponse, Integer statusCode, String message) throws IOException {
        servletResponse.setStatus(statusCode);
        objectMapper.writeValue(servletResponse.getWriter(), new ErrorResponseDto(message));
    }
}
