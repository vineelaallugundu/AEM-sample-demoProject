package com.dish.wholesale.core.beans;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.dish.wholesale.core.helpers.LinkUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class PageList implements ListItem {
    protected Page page;
    protected String parentId;

    public PageList(@NotNull Page page, String parentId) {
        this.parentId = parentId;
        this.page = page;

    }


    @Override
    public String getURL() {
        return LinkUtils.getPageLinkURL(this.page);
    }

    @Override
    public String getTitle() {
        return getTitle(this.page);
    }

    public static String getTitle(@NotNull Page page) {
        String title = page.getNavigationTitle();
        if (title == null) {
            title = page.getPageTitle();
        }

        if (title == null) {
            title = page.getTitle();
        }

        if (title == null) {
            title = page.getName();
        }

        return title;
    }

    @Override
    public String getDescription() {
        return this.page.getDescription();
    }

    @Override
    public Calendar getLastModified() {
        return this.page.getLastModified();
    }

    @Override
    public String getPath() {
        return this.page.getPath();
    }

    @Override
    @JsonIgnore
    public String getName() {
        return this.page.getName();
    }

}
