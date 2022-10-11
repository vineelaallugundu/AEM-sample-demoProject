package com.dish.wholesale.core.servlets;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;

/**
 * The Class FeedbackDataStoreServlet.
 */
@Component(immediate = true, service = Servlet.class, configurationPid = "com.dish.wholesale.core.servlets.FeedbackDataStoreServlet", property = {
		"sling.servlet.paths=" + "/bin/feedback", "sling.servlet.methods=GET", "sling.servlet.methods=POST" })
public class FeedbackDataStoreServlet extends SlingAllMethodsServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6644337233165521707L;

	private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackDataStoreServlet.class);

	/**
	 * Do post.
	 *
	 * @param request  the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {
		String feedbackData = request.getParameter("feedbackData");
		String url = request.getParameter("url");
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new java.util.Date());
		ResourceResolver resolver = request.getResourceResolver();
		Session session = resolver.adaptTo(Session.class);
		SecureRandom rnd = new SecureRandom();
		int number = rnd.nextInt(999999);

		try {
			Node node = JcrUtil.createPath("/content/usergenerated/content/sites" + "/feedbacks",
					JcrConstants.NT_UNSTRUCTURED, session);
			node.getSession().save();
			Node monthFolderNode = JcrUtil.createPath("/content/usergenerated/content/sites/feedbacks" + "/feedback-"
					+ new SimpleDateFormat("MMM-yyyy").format(new java.util.Date()), "sling:Folder", session);
			monthFolderNode.getSession().save();
			Node childNode = monthFolderNode.addNode("feedback-" + String.format("%06d", number),
					JcrConstants.NT_UNSTRUCTURED);
			childNode.getSession().save();
			childNode.setProperty("timeStamp", timeStamp);
			childNode.setProperty("feedbackData", feedbackData);
			childNode.setProperty("urlLocation", url);
			childNode.getSession().save();
		} catch (RepositoryException e) {
			LOGGER.error("exception occured {}", e);
		}

	}

	/**
	 * Do get.
	 *
	 * @param request  the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {
		doPost(request, response);
	}
}
