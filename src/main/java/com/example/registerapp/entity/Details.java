package com.example.registerapp.entity;

import org.springframework.data.annotation.Id;

import java.math.BigInteger;


import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "workDetails")
public class Details {
    @Id
    private BigInteger id;
    private String durationFrom;
    private String durationTo;
    private String type;
    private String project;
    private String description;

    public BigInteger getId() {
        return this.id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }


    public String getDurationFrom() {
        return this.durationFrom;
    }

    public void setDurationFrom(String durationFrom) {
        this.durationFrom = durationFrom;
    }


    public String getDurationTo() {
        return this.durationTo;
    }


    public void setDurationTo(String durationTo) {
        this.durationTo = durationTo;
    }


    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProject() {
        return this.project;
    }

    public void setProject(String project) {
        this.project = project;
    }


    public String getDescription() {
        return this.description;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}
