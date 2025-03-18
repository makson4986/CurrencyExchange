package org.makson.servlets.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ExchangeRateRequestDto;
import org.makson.exception.*;
import org.makson.services.ExchangeRateService;
import org.makson.utils.CurrencyValidator;
import org.makson.utils.DataValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo().isEmpty() || req.getPathInfo().isBlank() || req.getPathInfo().length() < 7) {
            throw new ServletException(new CurrencyCodeMissingException());
        }

        String baseCurrencyCode = req.getPathInfo().substring(1, 4);
        String targetCurrencyCode = req.getPathInfo().substring(4);

        if (!CurrencyValidator.isValidCurrencyCode(baseCurrencyCode) || !CurrencyValidator.isValidCurrencyCode(targetCurrencyCode)) {
            throw new ServletException(new InvalidCurrencyCodeException());
        }

        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.findByExchangeRate(baseCurrencyCode, targetCurrencyCode));
        } catch (DataNotFoundException | SQLException e) {
            throw new ServletException(e);
        }

    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String parameter = req.getReader().readLine();
        if (parameter == null || !parameter.contains("rate")) {
            throw new ServletException(new InvalidParameterException("The parameter rate is missing"));
        }

        ExchangeRateRequestDto exchangeRateRequestDto = getExchangeRateRequestDto(req, parameter);

        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.update(exchangeRateRequestDto));
        } catch (DataNotFoundException | SQLException e) {
            throw new ServletException(e);
        }
    }

    private ExchangeRateRequestDto getExchangeRateRequestDto(HttpServletRequest req, String parameter) throws ServletException {
        String baseCurrencyCode;
        String targetCurrencyCode;

        try {
            baseCurrencyCode = req.getPathInfo().substring(1, 4);
            targetCurrencyCode = req.getPathInfo().substring(4);
        } catch (IndexOutOfBoundsException e) {
            throw new ServletException(new CurrencyCodeMissingException());
        }

        if (!CurrencyValidator.isValidCurrencyCode(baseCurrencyCode) || !CurrencyValidator.isValidCurrencyCode(targetCurrencyCode)) {
            throw new ServletException(new InvalidCurrencyCodeException());
        }

        try {
            String rate = parameter.replace("rate=", "");
            DataValidator.isValidNumericalValueParameter(rate);
            return new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, new BigDecimal(rate).setScale(6, RoundingMode.CEILING));
        } catch (NumberFormatException e) {
            throw new ServletException(new NumberFormatException("Invalid value rate"));
        }

    }
}
