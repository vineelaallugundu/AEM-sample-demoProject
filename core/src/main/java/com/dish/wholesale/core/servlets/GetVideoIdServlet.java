package com.dish.wholesale.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class, property = { ServletResolverConstants.SLING_SERVLET_NAME + "=" + "GetVideoIdServlet Servlet",
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
		ServletResolverConstants.SLING_SERVLET_PATHS + "=" + "/bin/getvideoid" })
public class  GetVideoIdServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 6644337233165521707L;

	private static final Logger log = LoggerFactory.getLogger(GetVideoIdServlet.class);

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		String videoPath = request.getParameter("videopath");
		log.info("Requested video path -->{}", videoPath);
		String videoid = "";
		try {
			if (videoPath != null) {
				Resource resource = request.getResourceResolver().getResource(videoPath);
				ValueMap videoVm = resource.getValueMap();
				log.info("Video uuid-->", videoVm.get("jcr:uuid").toString());
				videoid = videoVm.get("jcr:uuid").toString();
				response.getWriter().write(videoid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(" Exception in GetVideoIdServlet", e);
		}

	}
}
