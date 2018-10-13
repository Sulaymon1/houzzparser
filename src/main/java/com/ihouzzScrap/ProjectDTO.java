package com.ihouzzScrap;

/**
 * Date 14.10.2018
 *
 * @author Hursanov Sulaymon
 * @version v1.0
 **/
public class ProjectDTO {
    private String houzzUrl;
    private String projectWebsite;
    private String phone;
    private String contact;
    private String location;

    private ProjectDTO(){

    }

    @Override
    public String toString() {
        return "ProjectDTO{" +
                "houzzUrl='" + houzzUrl + '\'' +
                ", projectWebsite='" + projectWebsite + '\'' +
                ", phone='" + phone + '\'' +
                ", contact='" + contact + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public class Builder{
        private Builder(){

        }

        public Builder houzzUrl(String houzzUrl){
            ProjectDTO.this.houzzUrl = houzzUrl;
            return this;
        }

        public Builder projectWebsite(String projectWebsite){
            ProjectDTO.this.projectWebsite = projectWebsite;
            return this;
        }

        public Builder phone(String phone){
            ProjectDTO.this.phone = phone;
            return this;
        }

        public Builder contact(String contact){
            ProjectDTO.this.contact = contact;
            return this;
        }

        public Builder location(String location){
            ProjectDTO.this.location = location;
            return this;
        }

        public ProjectDTO build(){
            return ProjectDTO.this;
        }

    }

    public static Builder builder(){
        return new ProjectDTO().new Builder();
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
}
