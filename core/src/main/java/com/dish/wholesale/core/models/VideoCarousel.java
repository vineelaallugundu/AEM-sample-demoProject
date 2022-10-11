package com.dish.wholesale.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@Model(adaptables = { Resource.class }, resourceType = {
        "wholesale/components/content/video-carousel" }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true") })
public class VideoCarousel {

    @Inject
    private String allowShare;

    @ChildResource
    private List<CarouselItem> carouselData;

    public List<CarouselPage> getCarouselDataByPage() {
        int position = 1;
        boolean startNewLine = false;
        List<CarouselPage> pages = new ArrayList<>();
        CarouselPage page = null;
        for (CarouselItem carouselItem : carouselData) {
            if (!carouselItem.getVideo().isEmpty()) {
                if ((position == 1) || startNewLine) {
                    carouselItem.newLine = true;
                    startNewLine = false;
                    page = new CarouselPage();
                    page.items = new ArrayList<CarouselItem>();
                    pages.add(page);
                }
                if (position % 4 == 0) {
                    startNewLine = true;
                }
                carouselItem.position = position;
                page.items.add(carouselItem);
                position++;
            }
        }
        return pages;
    }

    public String getAllowShare() {
        return allowShare;
    }
}
