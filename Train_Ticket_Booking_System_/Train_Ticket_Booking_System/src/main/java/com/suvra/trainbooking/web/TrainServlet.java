package com.suvra.trainbooking.web;

import com.suvra.trainbooking.model.Train;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/api/trains/*")
public class TrainServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        try {
            String path = request.getPathInfo();

            if ("/search".equals(path)) {
                String source = request.getParameter("source");
                String destination = request.getParameter("destination");
                LocalDate date = LocalDate.parse(request.getParameter("date"));

                List<Train> trains =
                        ApiServlet.trainService().search(source, destination, date);

                json(response, 200, trains);
                return;
            }

            if (path != null && path.matches("/\\d+")) {
                long trainId = Long.parseLong(path.substring(1));
                json(response, 200, ApiServlet.trainService().getTrain(trainId));
                return;
            }

            if (path != null && path.matches("/\\d+/seats")) {
                String[] parts = path.split("/");
                long trainId = Long.parseLong(parts[1]);

                json(response, 200, ApiServlet.trainService().getSeats(trainId));
                return;
            }

            response.sendError(404, "Unknown train endpoint.");

        } catch (Exception ex) {
            error(response, ex);
        }
    }
}
