package org.makson.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ErrorResponseDto;
import org.makson.exception.*;

import java.io.IOException;

@WebFilter("/*")
public class ErrorFilter implements Filter {
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
            case CurrencyAlreadyExistException currencyAlreadyExistException ->
                    handleException(servletResponse, 409, "A currency with this code already exists");
            case CurrencyNotFoundException currencyNotFoundException ->
                    handleException(servletResponse, 404, "Currency not found");
            case ExchangeRateAlreadyExistException exchangeRateAlreadyExistException ->
                    handleException(servletResponse, 409, "A currency pair with this code already exists");
            case ExchangeRateNotFoundException exchangeRateNotFoundException ->
                    handleException(servletResponse, 404, "Exchange rate for the pair not found");
            case InvalidCurrencyCodeException invalidCurrencyCodeException ->
                    handleException(servletResponse, 400, "This currency does not exist");
            case ParameterNotFoundException parameterNotFoundException ->
                    handleException(servletResponse, 400, exception.getMessage());
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
