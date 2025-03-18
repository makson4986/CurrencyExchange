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

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.findAll());
        } catch (SQLException e) {
            throw new ServletException(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if (!DataValidator.isValidParameter(baseCurrencyCode)) {
            throw new ServletException(new InvalidParameterException("Parameter baseCurrencyCode is missing or has invalid value"));
        } else if (!DataValidator.isValidParameter(targetCurrencyCode)) {
            throw new ServletException(new InvalidParameterException("Parameter targetCurrencyCode is missing or has invalid value"));
        } else if (!DataValidator.isValidParameter(rate)) {
            throw new ServletException(new InvalidParameterException("Parameter rate is missing or has invalid value"));
        }

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new ServletException(new InvalidParameterException("It is impossible to add a course with the same currencies"));
        }

        if (!CurrencyValidator.isValidCurrencyCode(baseCurrencyCode) || !CurrencyValidator.isValidCurrencyCode(targetCurrencyCode)) {
            throw new ServletException(new InvalidCurrencyCodeException());
        }

        ExchangeRateRequestDto newExchangeRate;
        try {
            DataValidator.isValidNumericalValueParameter(rate);
            newExchangeRate = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, new BigDecimal(rate).setScale(6, RoundingMode.CEILING));
        } catch (NumberFormatException e) {
            throw new ServletException(new NumberFormatException("Invalid value rate"));
        }

        try {
            resp.setStatus(201);
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.save(newExchangeRate));
        } catch (DataNotFoundException | DataAlreadyExistException | SQLException e) {
            throw new ServletException(e);
        }

    }
}
