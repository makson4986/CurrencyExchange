package org.makson.servlets.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.CurrencyRequestDto;
import org.makson.dto.ErrorResponseDto;
import org.makson.exception.CurrencyAlreadyExistsException;
import org.makson.services.CurrencyService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            objectMapper.writeValue(resp.getWriter(), currencyService.findAll());
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Something went wrong on the server. Please try again later"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || name.isBlank()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The parameter name is missing"));
            return;
        } else if (code == null || code.isBlank()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The parameter code is missing"));
            return;
        } else if (sign == null || sign.isBlank()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The parameter sign is missing"));
            return;
        }

        CurrencyRequestDto newCurrency = new CurrencyRequestDto(name, code, sign);

        try {
            resp.setStatus(201);
            objectMapper.writeValue(resp.getWriter(), currencyService.save(newCurrency));
        } catch (CurrencyAlreadyExistsException e) {
            resp.setStatus(409);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("A currency with this code already exists."));
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Something went wrong on the server. Please try again later."));
        }
    }
}
