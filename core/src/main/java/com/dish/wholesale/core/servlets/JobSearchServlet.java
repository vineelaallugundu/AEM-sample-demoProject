package com.dish.wholesale.core.servlets;

import com.dish.wholesale.core.beans.Jobs;
import com.dish.wholesale.core.enums.State;
import com.dish.wholesale.core.models.CareersModel;
import com.google.gson.Gson;
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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.*;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Implementation for Jobs Search Servlet.
 *
 ** Author : Gaurav Mishra
 */
@Component(immediate = true,
		service = Servlet.class,
		configurationPid = "com.dish.wholesale.core.servlets.JobSearchServlet",
		property = {
				"sling.servlet.resourceTypes=" + "sling/servlet/default",
				"sling.servlet.selectors=" + "searchj",
				"sling.servlet.extensions=" + "json",
				"sling.servlet.methods=GET"})
public class JobSearchServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 987651233165521707L;

	private static final Logger LOGGER = LoggerFactory.getLogger(JobSearchServlet.class);
	private static String baseCareerAPIUrl = StringUtils.EMPTY;
	private static final String API_KEY = "prefix-dish";
	private static final String API_ID = "b8ddc10c";
	private static final String LANGUAGE = "en-US";

	private static String toCacheVar = StringUtils.EMPTY;

	private static LoadingCache<String, List<Jobs>> cache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterAccess(24, TimeUnit.HOURS)
			.recordStats()
			.build(new CacheLoader<String, List<Jobs>>() {
				public List<Jobs> load(String jobsQuery) throws IOException, JSONException, URISyntaxException, ExecutionException {
					return getJobs(toCacheVar);
				}
			});

	public static List<Jobs> getJobsCache(String jobsQuery) throws ExecutionException {
		return cache.get(jobsQuery);
	}

	/**
	 * Do post.
	 *
	 * @param request  the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
		toCacheVar = request.getParameter("query").toLowerCase();
		CareersModel careersModel = request.adaptTo(CareersModel.class);
		baseCareerAPIUrl = careersModel.getJobsApiUrl();

		try {
			List<Jobs> jobsCache = this.getJobsCache(toCacheVar);
			Gson gson = new Gson();
			String jobsJson = gson.toJson(jobsCache);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jobsJson);
			response.getWriter().close();
			return;
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		try {
			Gson gson = new Gson();
			String jobsJson = gson.toJson(getJobs(toCacheVar));
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jobsJson);
			response.getWriter().close();
		} catch (JSONException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private static List<Jobs> getJobs(String page) throws IOException, JSONException, URISyntaxException {

		JSONObject jobByLanguage;
		JSONArray enLang;
		String count;
		List<Jobs> jobsList = new ArrayList<>();

		for (int off = 0; off < 1000; off = off + 10) {
			try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
				HttpGet getRequest = new HttpGet(baseCareerAPIUrl);
				URI uri = new URIBuilder(getRequest.getURI())
						.addParameter("offset", String.valueOf(off))
						.addParameter("tags4", "DISH Wireless")
						.addParameter("keywords", page)
						.build();
				getRequest.setURI(uri);
				getRequest.addHeader("accept", "application/json;charset=UTF-8");
				getRequest.addHeader("app_key", API_KEY);
				getRequest.addHeader("app_id", API_ID);
				getRequest.addHeader("accept-language", LANGUAGE);

				HttpResponse httpResponse = httpClient.execute(getRequest);

				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					LOGGER.info("inside if condition");
					throw new RuntimeException("Failed with HTTP error code: " + httpResponse.getStatusLine().getStatusCode());
				} else {
					try (BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())))) {
						String line = "";
						StringBuilder responseStrBuilder = new StringBuilder();
						while ((line = br.readLine()) != null) {
							responseStrBuilder.append(line);
						}
						JSONObject result = new JSONObject(responseStrBuilder.toString());
						count = result.get("count").toString();
						if (off > Integer.parseInt(count)) {
							break;
						}

						jobByLanguage = new JSONObject(result.get("jobs_by_language").toString());

						enLang = jobByLanguage.getJSONArray("en-us");

						for (int i = 0; i < enLang.length(); i++) {
							JSONObject json = enLang.getJSONObject(i);
							String city = json.getString("city");
							String state = State.valueOfName(json.getString("state")).getAbbreviation();
							String title = json.getString("title");
							String applyUrl = json.getString("apply_url");
							Jobs jobs = new Jobs();
							jobs.setTitle(title);
							jobs.setCity(city);
							jobs.setState(state);
							jobs.setApplyUrl(applyUrl);
							jobsList.add(jobs);
						}
					 }
					}
				}
			}
			return jobsList;
		}
}
