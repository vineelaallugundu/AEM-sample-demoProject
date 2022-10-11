package com.dish.wholesale.core.servlets;

import com.dish.wholesale.core.configs.HcaptchaConfig;
import com.dish.wholesale.core.helpers.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class, property = { ServletResolverConstants.SLING_SERVLET_NAME + "=" + "Hcaptcha Servlet",
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
		ServletResolverConstants.SLING_SERVLET_PATHS + "=" + "/bin/hcaptcha" })
public class HcaptchaServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 6644337233165521707L;

	private static final Logger log = LoggerFactory.getLogger(HcaptchaServlet.class);

	@Reference
	private transient HcaptchaConfig hcaptchaConfig;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		String response = getResponse(req);
		resp.getWriter().write(response);
	}

	private String getResponse(SlingHttpServletRequest req) throws IOException {
		log.info("Inside getResponse() Method");
		String response = StringUtils.EMPTY;
		String hCaptchaResponse = req.getParameter(Constants.HCAPTCHA_RESPONSE) != null ? req.getParameter(Constants.HCAPTCHA_RESPONSE)
				: StringUtils.EMPTY;
		log.info(Constants.HCAPTCHA_RESPONSE + hCaptchaResponse);
		String configUrl = "https://hcaptcha.com/siteverify";
		String secret = hcaptchaConfig.getHcaptchaSecretKey();
		if (hCaptchaResponse != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("response=");
			sb.append(hCaptchaResponse);
			sb.append("&secret=");
			sb.append(secret);
			try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
				HttpPost post = new HttpPost(configUrl);
				post.addHeader("Content-Type", "application/x-www-form-urlencoded");
				post.setEntity(new StringEntity(sb.toString()));
				HttpResponse httpResponse = client.execute(post);
				if (httpResponse != null && httpResponse.getEntity() != null
						&& httpResponse.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = httpResponse.getEntity();
					response = EntityUtils.toString(entity);
				}
				log.info("Response::: {}", response);
				return response;
			} catch (Exception e) {
				log.error("Error in HcaptchaServlet {0}", e);
			}
		}
		return response;
	}

}
