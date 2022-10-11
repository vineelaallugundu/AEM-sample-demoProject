package com.dish.wholesale.core.beans;

public class Jobs {

    private String title;
    private String city;
    private String state;
    private String applyUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }

    @Override
    public String toString() {
        return "Jobs{"
                + "title='" + title + '\''
                + ", city='" + city + '\''
                + ", state='" + state + '\''
                + ", applyUrl='" + applyUrl + '\''
                + '}';
    }
}
