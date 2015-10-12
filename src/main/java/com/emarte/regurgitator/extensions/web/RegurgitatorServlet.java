package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

import static com.emarte.regurgitator.core.StringType.stringify;
import static com.emarte.regurgitator.extensions.web.HttpGlobalUtil.applyGlobalData;
import static com.emarte.regurgitator.extensions.web.HttpRequestUtil.applyRequestData;
import static com.emarte.regurgitator.extensions.web.HttpResponseUtil.applyResponseData;

public class RegurgitatorServlet extends HttpServlet {
    private static Log log = Log.getLog(RegurgitatorServlet.class);

    private Regurgitator regurgitator;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();
        try {
            regurgitator = new Regurgitator("regurgitator", ConfigurationFile.loadFile(config.getInitParameter("config-location")));
        } catch (RegurgitatorException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();

		try {
            log.debug("Accepting new HTTP request");

            ResponseCallBack responseCallBack = new ResponseCallBack() {
                @Override
                public void respond(Message message, Object value) {
                    applyResponseData(message, response);

                    log.debug("Writing back response data");
					try {
            			ServletOutputStream outputStream = response.getOutputStream();
						outputStream.write(stringify(value).getBytes());
						outputStream.flush();
						outputStream.close();
					} catch (IOException e) {
						log.error("Error writing response text back from servlet: " + e.getMessage());
						try {
							response.sendError(500, e + (e.getCause() != null ? ": " + e.getCause() : ""));
						} catch (IOException e1) {
							// I tried
						}
					}
                }
            };

            log.debug("Creating new regurgitator message");
            Message message = new Message(responseCallBack);

            log.debug("Applying request details to message");
            applyRequestData(message, request);

            log.debug("Applying global details to message");
			applyGlobalData(message);

            log.debug("Sending message to regurgitator");
            regurgitator.processMessage(message);
        } catch (Exception e) {
			String errorMsg = e + (e.getCause() != null ? ": " + e.getCause() : "");
			log.error("Error handling http request: " + errorMsg);
            response.sendError(500, errorMsg);
        }

		long end = System.currentTimeMillis();
		log.debug("All done: " + (end - start) + " milliseconds");
    }
}
