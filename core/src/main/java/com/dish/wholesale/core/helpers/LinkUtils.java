package com.dish.wholesale.core.helpers;

import com.day.cq.wcm.api.Page;
import org.jetbrains.annotations.NotNull;

public final class LinkUtils {

    private LinkUtils() {
    }

    /**
     * Given a {@link Page}, this method returns the correct URL with the extension
     * @param page the page
     *
     * @return the URL of the provided (@code page}
     */
    @NotNull
    public static String getPageLinkURL(@NotNull Page page) {
        return page.getPath() + Constants.HTML_EXTENSION;
    }

}
