package org.makson.servlets.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ConvertCurrencyRequestDto;
import org.makson.dto.ErrorResponseDto;
import org.makson.exception.ExchangeRateNotFoundException;
import org.makson.services.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String fromCurrency = req.getParameter("from");
        String toCurrency = req.getParameter("to");
        String amount = req.getParameter("amount");

        if (fromCurrency == null || fromCurrency.isBlank()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The parameter from is missing"));
            return;
        } else if (toCurrency == null || toCurrency.isBlank()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The parameter to is missing"));
            return;
        } else if (amount == null || amount.isBlank()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The parameter amount is missing"));
            return;
        } else if ((!fromCurrency.equals(fromCurrency.toUpperCase()) || fromCurrency.length() != 3) ||
                (!toCurrency.equals(toCurrency.toUpperCase()) || toCurrency.length() != 3)) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The currency does not comply with ISO 4217 standard"));
            return;
        }

        ConvertCurrencyRequestDto convertCurrencyRequestDto = new ConvertCurrencyRequestDto(fromCurrency, toCurrency, new BigDecimal(amount));

        try {
            resp.setStatus(200);
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.convertCurrency(convertCurrencyRequestDto));
        } catch (ExchangeRateNotFoundException e) {
            resp.setStatus(404);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Exchange rate for the pair not found"));
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Something went wrong on the server. Please try again later"));
        }
    }
}
