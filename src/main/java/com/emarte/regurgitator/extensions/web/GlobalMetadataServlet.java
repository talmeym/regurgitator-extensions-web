/*
 * Copyright (C) 2017 Miles Talmey.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */
package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.CoreTypes;
import com.emarte.regurgitator.core.Parameter;
import com.emarte.regurgitator.core.ParameterType;
import com.emarte.regurgitator.core.RegurgitatorException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

import static com.emarte.regurgitator.core.CoreConfigConstants.*;
import static com.emarte.regurgitator.core.EntityLookup.parameterType;
import static com.emarte.regurgitator.core.FileUtil.getInputStreamForFile;
import static com.emarte.regurgitator.extensions.web.HttpGlobalUtil.*;

public class GlobalMetadataServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();
        String propertiesLocation = config.getInitParameter("global-location");

        if(propertiesLocation != null) {
            try {
                Properties properties = new Properties();
                properties.load(getInputStreamForFile(propertiesLocation));
                addGlobalParametersFromProperties(propertiesLocation, properties);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter(NAME);

        if(name != null && name.length() > 0) {
            returnParameter(name, resp);
            return;
        }

        returnAllParameters(resp);
    }

    private void returnAllParameters(HttpServletResponse resp) throws IOException {
        ServletOutputStream outputStream = resp.getOutputStream();

        for(Parameter parameter: getAllGlobalParameters()) {
            outputStream.write(parameterString(parameter).getBytes());
        }

        outputStream.flush();
        outputStream.close();
    }

    private void returnParameter(String name, HttpServletResponse resp) throws IOException {
        Parameter parameter = getGlobalParameter(name);

        if(parameter != null) {
            sendMessage(parameterString(parameter), resp);
        }

        sendMessage("parameter not found", 400, resp);
    }

    private void sendMessage(String message, HttpServletResponse resp) throws IOException {
        sendMessage(message, 200, resp);
    }

    private void sendMessage(String message, int statusCode, HttpServletResponse resp) throws IOException {
        resp.setStatus(statusCode);
        ServletOutputStream outputStream = resp.getOutputStream();
        outputStream.write(message.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private String parameterString(Parameter parameter) {
        return parameter.getName() + "=" + parameter.getValue() + " [" + parameter.getType().getClass().getName() + "]\n";
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter(NAME);
        String type = req.getParameter(TYPE);
        String value = req.getParameter(VALUE);

        try {
            ParameterType parameterType = type != null ? parameterType(type) : CoreTypes.STRING;
            setGlobalParameter(name, parameterType, value);
            sendMessage("parameter set", resp);
        } catch (RegurgitatorException e) {
            sendMessage("parameter type not found", 400, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter(NAME);

        if(name != null && name.length() > 0) {
            boolean deleted = removeGlobalParameter(name);

            if(deleted) {
                sendMessage("parameter removed", resp);
            }

            sendMessage("parameter not found", 400, resp);
            return;
        }

        sendMessage("removed " + removeAllGlobalParameters() + " parameters", resp);
    }
}
