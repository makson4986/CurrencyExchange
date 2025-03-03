package org.makson.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.CurrencyDto;
import org.makson.service.CurrencyService;
import org.makson.utils.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final JsonMapper jsonMapper = JsonMapper.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (var printWriter = resp.getWriter()) {
            printWriter.write(jsonMapper.dtoToJson(currencyService.findAll()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        //TODO resp.setContentType("application/json"); и ниже, вынести в отд метод

        CurrencyDto newCurrency = new CurrencyDto(
                req.getParameter("name"),
                req.getParameter("code"),
                req.getParameter("sign"));

        try (var printWriter = resp.getWriter()) {
            printWriter.write(jsonMapper.dtoToJson(currencyService.save(newCurrency)));
        }
    }
}
