package com.dish.wholesale.core.models;

import com.day.cq.dam.api.Asset;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class CarouselItem {

    private static final String DEFAULT_ASSET_TAG = "video";

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    @Named("jcr:title")
    private String title;

    @Inject
    private String text;

    @Inject
    private String video;

    @Inject
    private String fileReference;

    private boolean isTagged = false;

    public int position = 0;

    public boolean newLine = false;

    @PostConstruct
    protected void init() {
        if (null != video && !video.isEmpty()) {
            Asset asset = resourceResolver.resolve(video).adaptTo(Asset.class);
            if (null != asset) {
                String assetTag = asset.getMetadataValue("cq:tags");
                if (null != assetTag && assetTag.equalsIgnoreCase(DEFAULT_ASSET_TAG)) {
                    isTagged = true;
                }
            } else {
                video = StringUtils.EMPTY;
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getVideo() {
        return video;
    }

    public String getFileReference() {
        return fileReference;
    }

    public boolean isTagged() {
        return isTagged;
    }

    public int getPosition() {
        return position;
    }

    public boolean getNewline() {
        return newLine;
    }
}
