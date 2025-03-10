package org.makson.servlets.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ConvertCurrencyRequestDto;
import org.makson.exception.ExchangeRateNotFoundException;
import org.makson.exception.InvalidCurrencyCodeException;
import org.makson.exception.ParameterNotFoundException;
import org.makson.services.ExchangeRateService;
import org.makson.utils.CurrencyValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fromCurrency = req.getParameter("from");
        String toCurrency = req.getParameter("to");
        String amount = req.getParameter("amount");

        if (fromCurrency == null || fromCurrency.isBlank()) {
            throw new ServletException(new ParameterNotFoundException("The parameter «from» is missing"));
        } else if (toCurrency == null || toCurrency.isBlank()) {
            throw new ServletException(new ParameterNotFoundException("The parameter «to» is missing"));
        } else if (amount == null || amount.isBlank()) {
            throw new ServletException(new ParameterNotFoundException("The parameter amount is missing"));
        }

        if (!CurrencyValidator.isValidCurrencyCode(fromCurrency) || !CurrencyValidator.isValidCurrencyCode(toCurrency)) {
            throw new ServletException(new InvalidCurrencyCodeException());
        }

        ConvertCurrencyRequestDto convertCurrencyRequestDto = new ConvertCurrencyRequestDto(fromCurrency, toCurrency, new BigDecimal(amount));

        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.convertCurrency(convertCurrencyRequestDto));
        } catch (ExchangeRateNotFoundException | SQLException e) {
            throw new ServletException(e);
        }
    }
}
