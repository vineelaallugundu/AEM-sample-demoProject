package com.dish.wholesale.core.models;

import com.dish.wholesale.core.beans.NewsItems;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.resource.Resource;
import org.json.JSONException;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class NewsArticleModel.
 *
 * @author hemang
 */
@Model(adaptables = Resource.class, resourceType = {
		"wholesale/components/content/news-article" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsArticleModel {

	private static final Logger LOG = LoggerFactory.getLogger(NewsArticleModel.class);
	
	/** The article list. */
	private List<NewsItems> articleList = Collections.emptyList();

	/** The Constant NEWS_API_URL. */
	private static final String NEWS_API_URL = "https://about.dish.com/api/newsfeed_releases/list.php?category=779&fields=id,headline,released,releaseDate,summary,origin&limit=10&format=json";

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		try {
			newsAPIData();
		} catch (IOException | JSONException e) {
			LOG.error("Exception occurred :- {}", e.getMessage());
		}
	}

	/**
	 * News API data.
	 *
	 * @throws URISyntaxException      the URI syntax exception
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException             Signals that an I/O exception has occurred.
	 * @throws JSONException           the JSON exception
	 */
	private void newsAPIData() throws IOException, JSONException {
		int timeout = 5;
		RequestConfig config = RequestConfig.custom()
		  .setConnectTimeout(timeout * 1000)
		  .setConnectionRequestTimeout(timeout * 1000)
		  .setSocketTimeout(timeout * 1000).build();
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		HttpGet get = new HttpGet(NEWS_API_URL);
		HttpResponse response = httpClient.execute(get);
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity);
			JsonObject responseDataJO = (JsonObject) new JsonParser().parse(content);
			JsonArray newsArray = responseDataJO.getAsJsonArray("release");
			articleList = new ArrayList<>();
			for (int i = 0; i < newsArray.size(); i++) {
				NewsItems newsItems = new NewsItems();
				JsonObject obj = (JsonObject) newsArray.get(i);
				newsItems.setReleaseDate(obj.get("releaseDate").getAsString().substring(0, 16));
				newsItems.setHeadline(obj.get("headline").getAsString());
				newsItems.setSummary(obj.get("summary").getAsString());
				newsItems.setId(obj.get("id").getAsString());
				articleList.add(newsItems);
			}
		}
		httpClient.close();		
	}

	/**
	 * Gets the news items.
	 *
	 * @return the news items
	 */
	public List<NewsItems> getNewsItems() {
		return articleList;
	}
}
