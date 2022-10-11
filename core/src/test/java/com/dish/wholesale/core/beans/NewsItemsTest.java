package com.dish.wholesale.core.beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewsItemsTest {
    private NewsItems newsItems;

    @BeforeEach
    void setUp() {
        newsItems = new NewsItems();
        newsItems.setId("idem");
        newsItems.setHeadline("headlines");
        newsItems.setSummary("summary");
        newsItems.setReleaseDate("1/31/20221");
    }

    @Test
    void getMethods() {
        assertEquals("idem", newsItems.getId());
        assertEquals("headlines", newsItems.getHeadline());
        assertEquals("summary", newsItems.getSummary());
        assertEquals("1/31/20221", newsItems.getReleaseDate());
    }
}
