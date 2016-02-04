package com.emarte.regurgitator.extensions.web;

import com.emarte.regurgitator.core.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Properties;

import static com.emarte.regurgitator.core.CoreConfigConstants.*;
import static com.emarte.regurgitator.core.EntityLookup.parameterType;
import static com.emarte.regurgitator.core.FileUtil.getInputStreamForFile;
import static com.emarte.regurgitator.extensions.web.HttpGlobalUtil.*;

public class GlobalDataServlet extends HttpServlet {
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init();
		String propertiesLocation = config.getInitParameter("global-location");

		if(propertiesLocation != null) {
			try {
				Properties properties = new Properties();
				properties.load(getInputStreamForFile(propertiesLocation));
				HttpGlobalUtil.addGlobalParametersFromProperties(propertiesLocation, properties);
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
		sendMessage(parameter != null ? parameterString(parameter) : "parameter not found", resp);
	}

	private void sendMessage(String message, HttpServletResponse resp) throws IOException {
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
			resp.setStatus(200);
			sendMessage("parameter set", resp);
		} catch (RegurgitatorException e) {
			sendMessage("parameter type not found", resp);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter(NAME);

		if(name != null && name.length() > 0) {
			sendMessage("parameter " + (removeGlobalParameter(name) ? "removed" : "not found"), resp);
			return;
		}

		sendMessage("removed " + removeAllGlobalParameters() + " parameters", resp);
	}
}
