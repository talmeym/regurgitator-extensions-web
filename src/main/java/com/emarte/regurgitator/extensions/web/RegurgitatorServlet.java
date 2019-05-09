/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.emarte.regurgitator.core.ConfigurationFile.loadFile;
import static com.emarte.regurgitator.core.Log.getLog;
import static com.emarte.regurgitator.core.StringType.stringify;
import static com.emarte.regurgitator.extensions.web.HttpGlobalUtil.applyGlobalData;
import static com.emarte.regurgitator.extensions.web.HttpRequestUtil.applyRequestData;
import static com.emarte.regurgitator.extensions.web.HttpResponseUtil.applyResponseData;

public class RegurgitatorServlet extends HttpServlet implements HasId {
    private final Log log = getLog(this);
    private Regurgitator regurgitator;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();

        try {
            String id = config.getInitParameter("regurgitator-id");
            regurgitator = new Regurgitator(id == null ? "regurgitator" : id, loadFile(config.getInitParameter("config-location")));
        } catch (RegurgitatorException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest request, final HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();

        try {
            log.debug("Accepting new http request");

            ResponseCallBack responseCallBack = new ResponseCallBack() {
                @Override
                public void respond(Message message, Object value) {
                    log.debug("Processing callback");
                    log.debug("Applying message data to http response");
                    applyResponseData(message, response);

                    log.debug("Writing back response payload");
                    try {
                        ServletOutputStream outputStream = response.getOutputStream();
                        outputStream.write(stringify(value).getBytes());
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        log.error("Error writing response text back from servlet", e);
                        try {
                            response.sendError(500, e + (e.getCause() != null ? ": " + e.getCause() : ""));
                        } catch (IOException ioe) {
                            // I tried
                        }
                    }

                    log.debug("Callback processing complete");
                }
            };

            log.debug("Creating new message");
            Message message = new Message(responseCallBack);

            log.debug("Applying http request data to message");
            applyRequestData(request, message);

            log.debug("Applying global data to message");
            applyGlobalData(message);

            log.debug("Sending message to regurgitator");
            regurgitator.processMessage(message);
        } catch (Exception e) {
            String errorMsg = e + (e.getCause() != null ? ": " + e.getCause() : "");
            log.error("Error handling http request", e);
            response.sendError(500, errorMsg);
        }

        long end = System.currentTimeMillis();
        log.debug("*** All done: {} milliseconds ***", end - start);
    }

    @Override
    public Object getId() {
        return hashCode();
    }
}
