package com.suvra.trainbooking.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suvra.trainbooking.exception.AppException;
import com.suvra.trainbooking.util.Json;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public abstract class BaseServlet extends HttpServlet {

    protected final ObjectMapper mapper = Json.mapper();

    protected void json(HttpServletResponse response,
                        int status,
                        Object body) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), body);
    }

    protected void error(HttpServletResponse response, Exception ex)
            throws IOException {

        int status = ex instanceof AppException
                ? ((AppException) ex).getStatusCode()
                : 500;

        json(response, status, Map.of(
                "error", ex.getMessage() == null
                        ? "Unexpected server error"
                        : ex.getMessage()
        ));
    }
}
