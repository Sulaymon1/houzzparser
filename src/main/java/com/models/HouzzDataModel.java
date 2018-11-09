package com.models;

public class HouzzDataModel {
    private String houzzUrl;
    private String projectWebsite;
    private String phone;
    private String contact;
    private String location;
    private Long houzzLinkId;

    public Long getHouzzLinkId() {
        return houzzLinkId;
    }

    public void setHouzzLinkId(Long houzzLinkId) {
        this.houzzLinkId = houzzLinkId;
    }

    public HouzzDataModel() {
    }

    public String getHouzzUrl() {
        return houzzUrl;
    }

    public void setHouzzUrl(String houzzUrl) {
        this.houzzUrl = houzzUrl;
    }

    public String getProjectWebsite() {
        return projectWebsite;
    }

    public void setProjectWebsite(String projectWebsite) {
        this.projectWebsite = projectWebsite;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "HouzzDataModel{" +
                "houzzUrl='" + houzzUrl + '\'' +
                ", projectWebsite='" + projectWebsite + '\'' +
                ", phone='" + phone + '\'' +
                ", contact='" + contact + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
