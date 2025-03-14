package org.makson.servlets.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.exception.CurrencyCodeMissingException;
import org.makson.exception.DataNotFoundException;
import org.makson.exception.InvalidCurrencyCodeException;
import org.makson.services.CurrencyService;
import org.makson.utils.CurrencyValidator;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currencyCode = req.getPathInfo().substring(1);

        if (currencyCode.isBlank() || currencyCode.length() < 3) {
            throw new ServletException(new CurrencyCodeMissingException("Currency code is missing in the address"));
        } else if (!CurrencyValidator.isValidCurrencyCode(currencyCode)) {
            throw new ServletException(new InvalidCurrencyCodeException());
        }

        try {
            objectMapper.writeValue(resp.getWriter(), currencyService.findByCode(currencyCode));
        } catch (DataNotFoundException | SQLException e) {
            throw new ServletException(e);
        }

    }
}
