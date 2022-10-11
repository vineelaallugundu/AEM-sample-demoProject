package com.dish.wholesale.core.servlets;

import com.dish.wholesale.core.helpers.CMNotificationUtil;
import com.dish.wholesale.core.helpers.Constants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.auth.core.AuthConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Collectors;

@Component(service = Servlet.class, property = {
		ServletResolverConstants.SLING_SERVLET_NAME + Constants.SYMBOL_EQUALS + "CM Notification Servlet",
		ServletResolverConstants.SLING_SERVLET_METHODS + Constants.SYMBOL_EQUALS + HttpConstants.METHOD_GET,
		ServletResolverConstants.SLING_SERVLET_METHODS + Constants.SYMBOL_EQUALS + HttpConstants.METHOD_POST,
		ServletResolverConstants.SLING_SERVLET_PATHS + Constants.SYMBOL_EQUALS + CMNotificationServlet.SERVLET_PATH,
		AuthConstants.AUTH_REQUIREMENTS + "=-" + CMNotificationServlet.SERVLET_PATH
		})

@Designate(ocd = CMNotificationServlet.Config.class)
public class CMNotificationServlet extends SlingAllMethodsServlet {

	private static final Logger log = LoggerFactory.getLogger(CMNotificationServlet.class);

	static final String SERVLET_PATH = "/bin/cmnotification";

	private transient CMNotificationServlet.Config config;

	@ObjectClassDefinition(name = "CMNotificationServlet", description = "Configuration Details to Send CM Notification")
	public @interface Config {
		@AttributeDefinition(name = "ORGANIZATION_ID", description = "ORGANIZATION_ID")
		String organizationId() default "093D0BC1606B79A70A495CB4@AdobeOrg";

		@AttributeDefinition(name = "TECHNICAL_ACCOUNT_EMAIL", description = "TECHNICAL_ACCOUNT_EMAIL")
		String technicalAccountEmail() default "0ce2582d-1928-4499-b004-791efda8b834@techacct.adobe.com";

		@AttributeDefinition(name = "TECHNICAL_ACCOUNT_ID", description = "TECHNICAL_ACCOUNT_ID")
		String technicalAccountId() default "E5FD10ED60F6B0790A495C1C@techacct.adobe.com";

		@AttributeDefinition(name = "API_KEY", description = "API_KEY")
		String apiKey() default "8fac8496de26412ca38ccdd807373556";

		@AttributeDefinition(name = "PRIVATE_KEY_PATH", description = "PRIVATE_KEY_PATH")
		String privateKeyPath() default "/META-INF/keys/private.key";

		@AttributeDefinition(name = "CLIENT_SECRET", description = "CLIENT_SECRET")
		String clientSecret() default "p8e-Ox4ZjY6R5WJTFgrx5nsRB9YKKX_oxTAI";

		@AttributeDefinition(name = "GCHAT_WEBHOOK", description = "GCHAT_WEBHOOK")
		String teamsWebhook() default "https://chat.googleapis.com/v1/spaces/AAAAjBpcK6w/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=ZfuvgNSFo3ZmM-3akATG1Jo38VdxrS327jF3sfnzyZ0%3D";

		@AttributeDefinition(name = "AUTH_SERVER", description = "AUTH_SERVER")
		String authServer() default "ims-na1.adobelogin.com";
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws IOException {

		PrintWriter writer = resp.getWriter();

		String challenge = StringUtils.EMPTY;
		try {
			challenge = CMNotificationUtil.getParamValue(req.getRequestURI() + Constants.SYMBOL_QUESTION_MARK + req.getQueryString(), "challenge");
			log.info("doGet challenge {}", challenge);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (!challenge.equals(StringUtils.EMPTY)) {
			writer.print(challenge);
		} else {
			resp.sendError(400);
		}

	}

	/**
	 * Receive Adobe IO Events
	 */
	@Override
	protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws IOException {

		ResourceResolver resolver = req.getResourceResolver();
		log.info("ResourceResolver object {}", resolver);
		log.info("NEW Inside doPost() {}", req.getPathInfo());
		log.info("Request Headers {} " + req.getHeader(Constants.USER_AGENT) + "***" + req.getHeader(Constants.X_ADOBE_DIGITAL_SIGNATURE) + "***" + req.getHeader(Constants.X_ADOBE_PROVIDER));
		resp.setContentType(Constants.APPLICATION_SLASH_TEXT);
		PrintWriter printWriter = resp.getWriter();
		printWriter.print("Request Received");

		try {
			if (req.getHeader(Constants.USER_AGENT).equalsIgnoreCase(Constants.ADOBE_IO_USER_AGENT) && req.getHeader(Constants.X_ADOBE_PROVIDER).equalsIgnoreCase(Constants.CLOUD_MANAGER)) {
				String requestData = req.getReader().lines().collect(Collectors.joining());
				log.info("POST Request data :- {}", requestData);

				JsonElement jelement = new JsonParser().parse(requestData);
				JsonObject jobject = jelement.getAsJsonObject();

				String eventType = jobject.get(Constants.EVENT).getAsJsonObject().get(Constants.SYMBOL_AT_THE_RATE.concat(Constants.TYPE)).getAsString();
				log.info("POST EventType :- {}", eventType);
				String executionType = jobject.get(Constants.EVENT).getAsJsonObject().get(Constants.XDM_EVENT_ENVELOPE_OBJECTTYPE)
						.getAsString();
				log.info("POST ExecutionType :- {}", executionType);

				if ((Constants.ADOBE_IO_EVENT_STARTED.equalsIgnoreCase(eventType) || Constants.ADOBE_IO_EVENT_ENDED.equalsIgnoreCase(eventType)) && Constants.ADOBE_IO_PIPELINE_EXECUTION.equals(executionType)) {

					String executionUrl = jobject.get(Constants.EVENT).getAsJsonObject().get(Constants.ACTIVITY_STREAMS_OBJECT)
							.getAsJsonObject().get(Constants.SYMBOL_AT_THE_RATE.concat(Constants.ID)).getAsString();
					log.info("POST executionUrl :- {}", executionUrl);

					String executionResponse = CMNotificationUtil.makeApiCall(CMNotificationUtil.getAccessToken(config),
							executionUrl, config);

					log.info("POST executionResponse :- {}", executionResponse);
					JsonElement jelementer = new JsonParser().parse(executionResponse);
					JsonObject jobjecter = jelementer.getAsJsonObject();

					String pipelineurl = jobjecter.get(Constants.SYMBOL_UNDERSCORE.concat(Constants.LINKS)).getAsJsonObject()
							.get(Constants.ADOBE_IO_PIPELINE).getAsJsonObject().get(Constants.HREF).getAsString();
					log.info("POST pipeline Url :- {}", pipelineurl);

					URI uri = new URL(executionUrl).toURI();

					String pipelineResponse = CMNotificationUtil.makeApiCall(CMNotificationUtil.getAccessToken(config),
							uri.resolve(pipelineurl).toURL().toString(), config);
					log.info("POST pipeline Response :- {}", pipelineResponse);

					JsonElement jelementpipeline = new JsonParser().parse(pipelineResponse);
					JsonObject jobjectpipeline = jelementpipeline.getAsJsonObject();

					log.info("POST jobjectpipeline - {}", jobjectpipeline);
					String buildStatus = Constants.ADOBE_IO_EVENT_STARTED.equalsIgnoreCase(eventType) ? Constants.STARTED : Constants.ENDED;
					CMNotificationUtil.notifyTeams(jobjectpipeline.get(Constants.NAME).getAsString() + " Pipeline has been ".concat(buildStatus), config);
				}
			}
		} catch (Exception e) {
			log.error("Error in CMNotificationServlet {0}", e);
		}
	}

	@Activate
	@Modified
	public void activate(CMNotificationServlet.Config config) {
		this.config = config;
	}
}
