package org.makson.servlets.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.CurrencyRequestDto;
import org.makson.exception.DataAlreadyExistException;
import org.makson.exception.InvalidCurrencyCodeException;
import org.makson.exception.InvalidParameterException;
import org.makson.services.CurrencyService;
import org.makson.utils.CurrencyValidator;
import org.makson.utils.DataValidator;

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

        if (!DataValidator.isValidParameter(name)) {
            throw new ServletException(new InvalidParameterException("Parameter name is missing or has an invalid value"));
        } else if (!DataValidator.isValidParameter(code)) {
            throw new ServletException(new InvalidParameterException("Parameter code is missing or has an invalid value"));
        } else if (!DataValidator.isValidParameter(sign)) {
            throw new ServletException(new InvalidParameterException("Parameter sign is missing or has invalid value"));
        }

        if (!CurrencyValidator.isValidCurrencyCode(code)) {
            throw new ServletException(new InvalidCurrencyCodeException());
        }

        CurrencyRequestDto newCurrency = new CurrencyRequestDto(name, code, sign);

        try {
            resp.setStatus(201);
            objectMapper.writeValue(resp.getWriter(), currencyService.save(newCurrency));
        } catch (DataAlreadyExistException | SQLException e) {
            throw new ServletException(e);
        }
    }
}
