package com.example.job_recruiters_fireb_r7;

public class Applicant {
    private String address, qualification, name, email, jobtitle, phone;

    public Applicant() {
    }

    public Applicant(String address, String qualification, String name, String email, String jobtitle, String phone) {
        this.address = address;
        this.qualification = qualification;
        this.name = name;
        this.email = email;
        this.jobtitle = jobtitle;
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobtitle() {
        return jobtitle;
    }

    public void setJobtitle(String jobtitle) {
        this.jobtitle = jobtitle;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
