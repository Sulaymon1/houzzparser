package com.models;

public class HouzzStatus {
    private Long id;
    private Boolean isInProgress;
    private Integer status;
    private Integer on_page;
    private String houzzLink;
    private Long houzzId;

    public Long getHouzzId() {
        return houzzId;
    }

    public void setHouzzId(Long houzzId) {
        this.houzzId = houzzId;
    }

    @Override
    public String toString() {
        return "HouzzStatus{" +
                "id=" + id +
                ", isInProgress=" + isInProgress +
                ", status=" + status +
                ", on_page=" + on_page +
                ", houzzLink='" + houzzLink + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getInProgress() {
        return isInProgress;
    }

    public void setInProgress(Boolean inProgress) {
        isInProgress = inProgress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOn_page() {
        return on_page;
    }

    public void setOn_page(Integer on_page) {
        this.on_page = on_page;
    }

    public String getHouzzLink() {
        return houzzLink;
    }

    public void setHouzzLink(String houzzLink) {
        this.houzzLink = houzzLink;
    }
}
