package com.suvra.trainbooking.web;

import com.suvra.trainbooking.model.User;
import com.suvra.trainbooking.model.requests.LoginRequest;
import com.suvra.trainbooking.model.requests.RegisterRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        try {
            String path = request.getPathInfo();

            if ("/register".equals(path)) {
                RegisterRequest body =
                        mapper.readValue(request.getReader(), RegisterRequest.class);

                User user = ApiServlet.authService()
                        .register(body.name(), body.email(), body.password());

                json(response, 201, Map.of(
                        "id", user.id(),
                        "name", user.name(),
                        "email", user.email()
                ));
                return;
            }

            if ("/login".equals(path)) {
                LoginRequest body =
                        mapper.readValue(request.getReader(), LoginRequest.class);

                User user = ApiServlet.authService()
                        .login(body.email(), body.password());

                request.getSession(true).setAttribute("userId", user.id());

                json(response, 200, Map.of(
                        "message", "Login successful",
                        "userId", user.id(),
                        "name", user.name()
                ));
                return;
            }

            response.sendError(404, "Unknown auth endpoint.");

        } catch (Exception ex) {
            error(response, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {

        request.getSession().invalidate();

        json(response, 200, Map.of("message", "Logged out."));
    }
}
