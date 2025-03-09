package org.makson.servlets.exchangeRate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ExchangeRateRequestDto;
import org.makson.exception.ExchangeRateNotFoundException;
import org.makson.services.ExchangeRateService;
import org.makson.utils.JsonMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final JsonMapper jsonMapper = JsonMapper.getInstance();

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
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String baseCurrencyCode = req.getPathInfo().substring(1, 4);
        String targetCurrencyCode = req.getPathInfo().substring(4);

        try (var printWriter = resp.getWriter()) {
            printWriter.write(jsonMapper.dtoToJson(exchangeRateService.findByExchangeRate(baseCurrencyCode, targetCurrencyCode)));
        } catch (ExchangeRateNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String string = req.getReader().readLine().split("=")[1];
        BigDecimal rate = new BigDecimal(string);

        String baseCurrencyCode = req.getPathInfo().substring(1, 4);
        String targetCurrencyCode = req.getPathInfo().substring(4);

        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(
                baseCurrencyCode,
                targetCurrencyCode,
                rate
        );

//        try (var printWriter = resp.getWriter()) {
//            printWriter.write(jsonMapper.dtoToJson(exchangeRateService.update(exchangeRateRequestDto)));
//        }
    }
}
