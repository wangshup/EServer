package com.dd.server.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dd.server.Server;
import com.dd.server.session.AbstractSession;

public class StatusServlet extends HttpServlet {
    private static final long serialVersionUID = -2869552041871303213L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Server server = Server.getInstance();
        StringBuilder sb = new StringBuilder(256);
        sb.append("STAT uptime ").append(server.getServerUpTime() / 1000L).append(" sec\n");
        sb.append("STAT time ").append(System.currentTimeMillis() / 1000L).append(" sec\n");
        sb.append("STAT version ").append("1.0.1").append("\n");
        sb.append("STAT total_connections ").append(server.getSessionService().getTotalSessionsCount()).append("\n");
        sb.append("STAT current_connections ").append(server.getSessionService().getCurrentSessionsCount())
                .append("\n");
        sb.append("STAT total_read_bytes ").append(AbstractSession.totalRead.longValue()).append("\n");
        sb.append("STAT total_send_bytes ").append(AbstractSession.totalSend.longValue()).append("\n");

        resp.getWriter().print(sb);
        resp.getWriter().flush();
    }
}