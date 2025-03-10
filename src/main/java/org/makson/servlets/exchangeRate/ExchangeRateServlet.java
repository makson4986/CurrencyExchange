package org.makson.servlets.exchangeRate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ErrorResponseDto;
import org.makson.dto.ExchangeRateRequestDto;
import org.makson.exception.ExchangeRateNotFoundException;
import org.makson.services.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
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
        if (req.getPathInfo().length() != 7) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Currency codes of the pair are missing in the address"));
            return;
        }

        String baseCurrencyCode = req.getPathInfo().substring(1, 4);
        String targetCurrencyCode = req.getPathInfo().substring(4);

        try {
            resp.setStatus(200);
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.findByExchangeRate(baseCurrencyCode, targetCurrencyCode));
        } catch (ExchangeRateNotFoundException e) {
            resp.setStatus(404);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Exchange rate for the pair not found"));
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Something went wrong on the server. Please try again later"));
        }

    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String parameter = req.getReader().readLine();
        if (parameter == null || !parameter.contains("rate")) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The parameter rate is missing"));
            return;
        }

        String baseCurrencyCode = req.getPathInfo().substring(1, 4);
        String targetCurrencyCode = req.getPathInfo().substring(4);
        BigDecimal rate = new BigDecimal(parameter.replace("rate=", ""));
        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);

        try {
            resp.setStatus(200);
            objectMapper.writeValue(resp.getWriter(), exchangeRateService.update(exchangeRateRequestDto));
        } catch (ExchangeRateNotFoundException e) {
            resp.setStatus(404);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The currency pair is missing from the database"));
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Something went wrong on the server. Please try again later"));
        }
    }
}
