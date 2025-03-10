package org.makson.servlets.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.CurrencyRequestDto;
import org.makson.exception.CurrencyAlreadyExistException;
import org.makson.exception.InvalidCurrencyCodeException;
import org.makson.exception.ParameterNotFoundException;
import org.makson.services.CurrencyService;
import org.makson.utils.CurrencyValidator;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            objectMapper.writeValue(resp.getWriter(), currencyService.findAll());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || name.isBlank()) {
            throw new ServletException(new ParameterNotFoundException("The parameter name is missing"));
        } else if (code == null || code.isBlank()) {
            throw new ServletException(new ParameterNotFoundException("The parameter code is missing"));
        } else if (sign == null || sign.isBlank()) {
            throw new ServletException(new ParameterNotFoundException("The parameter sign is missing"));
        }

        if (!CurrencyValidator.isValidCurrencyCode(code)) {
            throw new ServletException(new InvalidCurrencyCodeException());
        }

        CurrencyRequestDto newCurrency = new CurrencyRequestDto(name, code, sign);

        try {
            resp.setStatus(201);
            objectMapper.writeValue(resp.getWriter(), currencyService.save(newCurrency));
        } catch (CurrencyAlreadyExistException | SQLException e) {
            throw new ServletException(e);
        }
    }
}
