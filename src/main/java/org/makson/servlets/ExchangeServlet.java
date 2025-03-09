package org.makson.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ConvertCurrencyRequestDto;
import org.makson.services.ExchangeRateService;
import org.makson.utils.JsonMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final JsonMapper jsonMapper = JsonMapper.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String fromCurrency = req.getParameter("from");
        String toCurrency = req.getParameter("to");
        String amount = req.getParameter("amount");

        ConvertCurrencyRequestDto convertCurrencyRequestDto = new ConvertCurrencyRequestDto(
                fromCurrency,
                toCurrency,
                new BigDecimal(amount)
        );

        try (var printWriter = resp.getWriter()) {
            printWriter.write(jsonMapper.dtoToJson(exchangeRateService.convertCurrency(convertCurrencyRequestDto)));
        }
    }
}
