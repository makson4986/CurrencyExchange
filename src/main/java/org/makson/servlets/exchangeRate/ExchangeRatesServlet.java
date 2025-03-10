package org.makson.servlets.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ErrorResponseDto;
import org.makson.dto.ExchangeRateRequestDto;
import org.makson.exception.CurrencyNotFoundException;
import org.makson.exception.ExchangeRateAlreadyExistsException;
import org.makson.services.ExchangeRateService;

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
            resp.setStatus(200);
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.findAll());
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Something went wrong on the server. Please try again later"));
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if (baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The parameter baseCurrencyCode is missing"));
            return;
        } else if (targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The parameter targetCurrencyCode is missing"));
            return;
        } else if (rate == null || rate.isBlank()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The parameter rate is missing"));
            return;
        }

        ExchangeRateRequestDto newExchangeRate = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, new BigDecimal(rate));

        try {
            resp.setStatus(201);
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.save(newExchangeRate));
        } catch (CurrencyNotFoundException e) {
            resp.setStatus(404);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("One (or both) currencies from the currency pair do not exist in the database"));
        } catch (ExchangeRateAlreadyExistsException e) {
            resp.setStatus(409);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("A currency pair with this code already exists"));
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Something went wrong on the server. Please try again later"));
        }

    }
}
