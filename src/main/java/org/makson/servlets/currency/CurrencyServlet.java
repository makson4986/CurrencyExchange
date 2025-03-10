package org.makson.servlets.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.makson.dto.ErrorResponseDto;
import org.makson.exception.CurrencyNotFoundException;
import org.makson.services.CurrencyService;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currencyCode = req.getPathInfo().substring(1);

        if (currencyCode.isBlank()) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Currency code is missing in the address"));
            return;
        } else if (!currencyCode.equals(currencyCode.toUpperCase()) || currencyCode.length() !=3) {
            resp.setStatus(400);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("The currency does not comply with ISO 4217 standard"));
            return;
        }

        try {
            resp.setStatus(200);
            objectMapper.writeValue(resp.getWriter(), currencyService.findByCode(currencyCode));
        } catch (CurrencyNotFoundException e) {
            resp.setStatus(404);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Currency not found"));
        } catch (SQLException e) {
            resp.setStatus(500);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponseDto("Something went wrong on the server. Please try again later."));
        }

    }
}
