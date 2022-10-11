package com.dish.wholesale.core.servlets;

import com.dish.wholesale.core.beans.Jobs;
import com.dish.wholesale.core.enums.State;
import com.dish.wholesale.core.models.CareersModel;
import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for Careers Page Servlet.
 *
 ** Author : Gaurav Mishra
 */
@Component(immediate = true, service = Servlet.class, configurationPid = "com.dish.wholesale.core.servlets.CareersPageServlet", property = {
		"sling.servlet.resourceTypes=" + "sling/servlet/default", "sling.servlet.selectors=" + "jobs",
		"sling.servlet.extensions=" + "json", "sling.servlet.methods=GET" })
public class CareersPageServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 987651233165521707L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CareersPageServlet.class);
	private String baseCareerAPIUrl = StringUtils.EMPTY;
	private static final String API_KEY = "prefix-dish";
	private static final String API_ID = "b8ddc10c";
	private static final String LANGUAGE = "en-US";

	/**
	 * Do post.
	 *
	 * @param request  the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
		String pageNumber = request.getParameter("pageNum");
		CareersModel careersModel = request.adaptTo(CareersModel.class);
		baseCareerAPIUrl = careersModel.getJobsApiUrl();

			Gson gson = new Gson();
			String jobsJson = gson.toJson(getJobs(pageNumber));
			response.setContentType("application/json");
			response.setHeader("Dispatcher", "no-cache");
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jobsJson);
			response.getWriter().close();
	}

	private List<Jobs> getJobs(String page) throws IOException {
		JSONObject jobByLanguage;
		JSONArray enLang;
		List<Jobs> jobsList = new ArrayList<>();

		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpGet getRequest = new HttpGet(baseCareerAPIUrl);
			URI uri = new URIBuilder(getRequest.getURI()).addParameter("offset", page)
					.addParameter("tags4", "DISH Wireless").build();
			getRequest.setURI(uri);
			getRequest.addHeader("accept", "application/json;charset=UTF-8");
			getRequest.addHeader("app_key", API_KEY);
			getRequest.addHeader("app_id", API_ID);
			getRequest.addHeader("accept-language", LANGUAGE);

			HttpResponse httpResponse = httpClient.execute(getRequest);

			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				LOGGER.info("inside if condition");
				throw new HttpException(
						"Failed with HTTP error code: " + httpResponse.getStatusLine().getStatusCode());
			} else {
				try (BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())))) {
					String line = "";
					StringBuilder responseStrBuilder = new StringBuilder();
					while ((line = br.readLine()) != null) {
						responseStrBuilder.append(line);
					}
					JSONObject result = new JSONObject(responseStrBuilder.toString());
					jobByLanguage = new JSONObject(result.get("jobs_by_language").toString());
					enLang = jobByLanguage.getJSONArray("en-us");

					for (int i = 0; i < enLang.length(); i++) {
						JSONObject json = enLang.getJSONObject(i);
						String city = json.getString("city");
						String state = State.valueOfName(json.getString("state")).getAbbreviation();
						String title = json.getString("title");
						StringBuilder applyUrl = new StringBuilder("https://jobs.dish.com/jobs/");
						String reqId = json.getString("req_id");
						applyUrl.append(reqId);
						Jobs jobs = new Jobs();
						jobs.setTitle(title);
						jobs.setCity(city);
						jobs.setState(state);
						jobs.setApplyUrl(applyUrl.toString());
						jobsList.add(jobs);
					}

					return jobsList;
				}
			}
		} catch (URISyntaxException | JSONException e) {
			LOGGER.error("exception occured {0}", e);
		} finally {
			httpClient.close();
		}
		return jobsList;
	}
}
