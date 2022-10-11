package com.dish.wholesale.core.models;

import com.dish.wholesale.core.helpers.Constants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Objects;

@Model(adaptables = Resource.class, resourceType = {
        "wholesale/components/content/wholesale-image" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class ImageModel {

    private static final Logger log = LoggerFactory.getLogger(ImageModel.class);

    @Inject
    private String fileReference;

    @Inject
    private String altText;

    @Inject
    private String alignment;

    @Inject
    private String widthUnit;

    @Inject
    private Long widthInPercentage;

    @Inject
    private Long widthInPixels;

    @Inject
    private String imageLink;

    private String width;

    @PostConstruct
    public void init() {
        log.debug(":: Image Model initialized ::");
        width = Constants.AUTO;
        if (Objects.nonNull(widthUnit)) {
            if (widthUnit.equalsIgnoreCase(Constants.PERCENTAGE) && Objects.nonNull(widthInPercentage)) {
                width = widthInPercentage.toString().concat(Constants.SYMBOL_PERCENTAGE);
            } else if (widthUnit.equalsIgnoreCase(Constants.PIXELS) && Objects.nonNull(widthInPixels)) {
                width = widthInPixels.toString().concat(Constants.SYMBOL_PIXELS);
            }
        }
    }

    public String getFileReference() {
        return fileReference;
    }

    public String getAltText() {
        return altText;
    }

    public String getAlignment() {
        return alignment;
    }

    public String getWidth() {
        return width;
    }

    public String getImageLink() {
        return imageLink;
    }
}
