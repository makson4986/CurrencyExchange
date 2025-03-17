package org.makson.servlets.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ConvertCurrencyRequestDto;
import org.makson.exception.DataNotFoundException;
import org.makson.exception.InvalidCurrencyCodeException;
import org.makson.exception.InvalidParameterException;
import org.makson.services.ExchangeRateService;
import org.makson.utils.CurrencyValidator;
import org.makson.utils.DataValidator;

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

        if (!DataValidator.isValidParameter(fromCurrency)) {
            throw new ServletException(new InvalidParameterException("Parameter «from» is missing or has invalid value"));
        } else if (!DataValidator.isValidParameter(toCurrency)) {
            throw new ServletException(new InvalidParameterException("Parameter «to» is missing or has invalid value"));
        } else if (!DataValidator.isValidParameter(amount)) {
            throw new ServletException(new InvalidParameterException("Parameter «amount» is missing or has invalid value"));
        }

        if (!CurrencyValidator.isValidCurrencyCode(fromCurrency) || !CurrencyValidator.isValidCurrencyCode(toCurrency)) {
            throw new ServletException(new InvalidCurrencyCodeException());
        }

        ConvertCurrencyRequestDto convertCurrencyRequestDto;
        try {
            DataValidator.isValidNumericalValueParameter(amount);
            convertCurrencyRequestDto = new ConvertCurrencyRequestDto(fromCurrency, toCurrency, new BigDecimal(amount));
        } catch (NumberFormatException e) {
            throw new ServletException(new NumberFormatException("Invalid value amount"));
        }

        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.convertCurrency(convertCurrencyRequestDto));
        } catch (DataNotFoundException | SQLException e) {
            throw new ServletException(e);
        }
    }
}
