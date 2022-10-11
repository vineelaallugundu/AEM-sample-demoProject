package com.dish.wholesale.core.beans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JobsTest {

    private Jobs jobs;

    @BeforeEach
    void setUp() {
        jobs = new Jobs();
        jobs.setApplyUrl("/content/jobs");
        jobs.setCity("Englewood");
        jobs.setTitle("consultant");
        jobs.setState("Colorado");
    }

    @Test
    void getMethods() {
        assertEquals("/content/jobs", jobs.getApplyUrl());
        assertEquals("Englewood", jobs.getCity());
        assertEquals("consultant", jobs.getTitle());
        assertEquals("Colorado", jobs.getState());
        assertNotNull(jobs.toString());
    }
}
