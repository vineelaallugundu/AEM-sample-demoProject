package com.dish.wholesale.core.models;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class)
public class VideoPageModel {

    private static final Logger logger = LoggerFactory.getLogger(VideoPageModel.class);

    @Inject
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    private Resource resource;

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    private String videoid;

    String videoPath = null;

    @PostConstruct
    protected void init() {
         try {
             if ((videoid != null) && DamUtil.getAssetFromID(request.getResourceResolver(), videoid) != null) {
                Asset assetUUID = DamUtil.getAssetFromID(request.getResourceResolver(), videoid);
                String assetPath = assetUUID.getPath();
                Resource resource = request.getResourceResolver().getResource(assetPath);
                Asset asset = resource.adaptTo(Asset.class);
                List<String> videotags = new ArrayList<>();
                Object[] tagsArray = null;
                if (asset.getMetadata().containsKey("cq:tags")) {
                    Object tagObj = asset.getMetadata("cq:tags");
                    if (tagObj instanceof Object[]) {
                        tagsArray = (Object[]) tagObj;
                    }
                    for (Object o : tagsArray) {
                        String ab = o.toString();
                        TagManager tagManager = null;
                        tagManager = resourceResolver.adaptTo(TagManager.class);
                        Tag videotag = tagManager.resolve(ab);
                        videotags.add(videotag.getTitle());
                    }
                    if (videotags.contains("Video")) {
                        videoPath = assetPath;
                    }
                }
            } else {
                 String redirectPath;
                 videoPath = StringUtils.EMPTY;
                 request.getRemoteHost();
                 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                 if (request.getServerPort() < 1) {
                     redirectPath = request.getScheme() + "://" + request.getServerName() + "/404";
                 } else {
                    redirectPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()  + "/404";
                 }
                 response.sendRedirect(redirectPath);
             }
         } catch (RepositoryException | IOException e) {
                e.printStackTrace();
                logger.error("Error in Video Model", e);
            }
    }

    public String getValue() {
        return videoPath;
    }

}
