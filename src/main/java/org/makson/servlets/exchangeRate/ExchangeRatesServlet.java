package org.makson.servlets.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ExchangeRateRequestDto;
import org.makson.exception.CurrencyNotFoundException;
import org.makson.exception.ExchangeRateAlreadyExistException;
import org.makson.exception.InvalidCurrencyCodeException;
import org.makson.exception.ParameterNotFoundException;
import org.makson.services.ExchangeRateService;
import org.makson.utils.CurrencyValidator;

import java.io.IOException;
import java.math.BigDecimal;
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

        if (baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
            throw new ServletException(new ParameterNotFoundException("The parameter baseCurrencyCode is missing"));
        } else if (targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
            throw new ServletException(new ParameterNotFoundException("The parameter targetCurrencyCode is missing"));
        } else if (rate == null || rate.isBlank()) {
            throw new ServletException(new ParameterNotFoundException("The parameter rate is missing"));
        }

        if (!CurrencyValidator.isValidCurrencyCode(baseCurrencyCode) || !CurrencyValidator.isValidCurrencyCode(targetCurrencyCode)) {
            throw new ServletException(new InvalidCurrencyCodeException());
        }

        ExchangeRateRequestDto newExchangeRate = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, new BigDecimal(rate));

        try {
            resp.setStatus(201);
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.save(newExchangeRate));
        } catch (CurrencyNotFoundException | ExchangeRateAlreadyExistException | SQLException e) {
            throw new ServletException(e);
        }

    }
}
