package com.dish.wholesale.core.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dish.wholesale.core.servlets.CMNotificationServlet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


public final class CMNotificationUtil {
	
	private static String jwtToken;
	
	private CMNotificationUtil() {
		super();
	}

	private static final Logger log = LoggerFactory.getLogger(CMNotificationUtil.class);

		static long EXPIRATION = 60 * 60; // 1 hour

	/**
	 * 
	 * @param link
	 * @param paramName
	 * @return
	 * @throws URISyntaxException
	 */
	public static String getParamValue(String link, String paramName) throws URISyntaxException {
		List<NameValuePair> queryParams = new URIBuilder(link).getQueryParams();
		log.info("Inside getParamValue, queryParams :- " + queryParams);
		return queryParams.stream().filter(param -> param.getName().equalsIgnoreCase(paramName))
				.map(NameValuePair::getValue).findFirst().orElse("");
	}

	/**
	 * Get Access Token
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static String getAccessToken(CMNotificationServlet.Config config) {
		log.info("Inside getAccessToken :- ");
		String jwtToken = getJWTToken(config);

		String accessToken = "";
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

			HttpPost authPostRequest = new HttpPost("https://" + config.authServer() + "/ims/exchange/jwt");
			authPostRequest.addHeader("Cache-Control", "no-cache");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("client_id", config.apiKey()));
			params.add(new BasicNameValuePair("client_secret", config.clientSecret()));
			params.add(new BasicNameValuePair("jwt_token", jwtToken));
			authPostRequest.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			HttpResponse response = httpclient.execute(authPostRequest);
			if (200 != response.getStatusLine().getStatusCode()) {
				throw new IOException("Server returned error: " + response.getStatusLine().getReasonPhrase());
			}
			HttpEntity entity = response.getEntity();

			JsonElement jelement = new JsonParser().parse(EntityUtils.toString(entity));
			JsonObject jobject = jelement.getAsJsonObject();

			accessToken = jobject.get("access_token").getAsString();
			log.info("Access Token :- " + accessToken);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return accessToken;

	}

	/**
	 * Get JWT Token
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	static String getJWTToken(CMNotificationServlet.Config config) {
		try {
			log.info("Inside getJWTToken");
			Long expirationTime = Math.round(new Date().getTime() / 1000) + EXPIRATION;

			HashMap<String, Object> jwtClaims = new HashMap<String, Object>();
			jwtClaims.put("iss", config.organizationId());
			jwtClaims.put("sub", config.technicalAccountId());
			jwtClaims.put("exp", expirationTime);
			jwtClaims.put("aud", "https://" + config.authServer() + "/c/" + config.apiKey());
			jwtClaims.put("https://" + config.authServer() + "/s/ent_cloudmgr_sdk", true);

			String privateKeyContent;

			log.info("InputStream :- " + CMNotificationUtil.class.getResourceAsStream(config.privateKeyPath()));
			try (InputStream keyStoreStream = CMNotificationUtil.class.getResourceAsStream(config.privateKeyPath())) {

				privateKeyContent = new BufferedReader(new InputStreamReader(keyStoreStream, StandardCharsets.UTF_8))
						.lines().collect(Collectors.joining("\n"));
			}
			
			
			privateKeyContent = privateKeyContent.replace("-----BEGIN PRIVATE KEY-----", "");
			privateKeyContent = privateKeyContent.replace("-----END PRIVATE KEY-----", "");
			privateKeyContent = privateKeyContent.replaceAll("\\s+", "");
			log.info("NEW privateKeyContent:- " + privateKeyContent);

			byte[] pkcs8EncodedBytes = DatatypeConverter.parseBase64Binary(privateKeyContent);
			log.info("byte[] pkcs8EncodedBytes :- " + pkcs8EncodedBytes);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
			log.info("keySpec :- " + keySpec);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			log.info("KeyFactory :- " + kf);
			PrivateKey privateKey = kf.generatePrivate(keySpec);
			log.info("Inside getJWTToken privateKey :- " + privateKey);

			jwtToken = Jwts.builder().setClaims(jwtClaims).signWith(SignatureAlgorithm.RS256, privateKey).compact();

			log.info("JWT Token :- " + jwtToken);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return jwtToken;
	}

	/**
	 * Cloud Manager API Calls
	 * 
	 * @param accessToken
	 * @param url
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static String makeApiCall(String accessToken, String url, CMNotificationServlet.Config config)
			throws Exception {

		try (CloseableHttpClient httpclient = HttpClients.custom().
				setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).
				setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()
				{
					public boolean isTrusted(X509Certificate[] arg0, String arg1)
					{
						return true;
					}
				}).build()).build()) {

			HttpGet apiRequest = new HttpGet(url);
			apiRequest.addHeader("x-gw-ims-org-id", config.organizationId());
			apiRequest.addHeader("x-api-key", config.apiKey());
			apiRequest.addHeader("Authorization", "Bearer " + accessToken);
			apiRequest.addHeader("Content-Type", "application/json;charset=UTF-8");

			HttpResponse response = httpclient.execute(apiRequest);
			if (200 != response.getStatusLine().getStatusCode()) {
				throw new IOException("Server returned error: " + response.getStatusLine().getReasonPhrase());
			}
			HttpEntity entity = response.getEntity();

			return EntityUtils.toString(entity);

		}

	}

	/**
	 * Post Message to Teams
	 * 
	 * @param message
	 * @param config
	 * @throws IOException
	 */
	public static void notifyTeams(String message, CMNotificationServlet.Config config) throws IOException {
		
		
		try {
			StringBuilder sb = new StringBuilder();
		    sb.append("response=");
		    
		    StringBuilder json = new StringBuilder();
	        json.append("{");
	        json.append("\"text\":\"");
	        json.append(message);
	        json.append("\"");
	        json.append("}");
	        log.info("JSON  ----> " + json);
		    
			CloseableHttpClient client = null;
		    HttpClientBuilder httpClientBuilder = null;
		    httpClientBuilder = HttpClientBuilder.create();
		    client = httpClientBuilder.build();
		    log.info("WEBHOOK url  ----> " + config.teamsWebhook());
		    HttpPost post = new HttpPost(config.teamsWebhook());
	        post.setEntity(new StringEntity(json.toString()));
		    HttpResponse response1 = client.execute(post);
		    log.info("G-CHAT Endpoint HttpResponse  ----> " + response1);
		    if (response1 != null && response1.getEntity() != null && response1.getStatusLine().getStatusCode() == 200) {
			     HttpEntity entity = response1.getEntity();				     
			     String response = EntityUtils.toString(entity);
		    } 
		    log.info("Message posted on GOOGLE-CHAT ............. ");
		} catch (Exception e) {
			log.error("Mail Failed - " + e.getMessage());
		}
		log.info("Exiting notifyTeams :- ");
	}

	public static void verifySignature(String requestData, String signature, CMNotificationServlet.Config config)
			throws Exception {

		if (signature != null && !signature.equals("")) {
			
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(config.clientSecret().getBytes(), "HmacSHA256");
			mac.init(secretKeySpec);
			String hmacSha256 = Base64.encodeBase64String(mac.doFinal(requestData.getBytes()));
			
			if (!signature.equals(hmacSha256)) {
				throw new Exception("x-adobe-signature HMAC check failed");
			}
		} else {
			throw new Exception("x-adobe-signature required");
		}

	}

	
}
