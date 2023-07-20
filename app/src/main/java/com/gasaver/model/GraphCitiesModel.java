package com.gasaver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GraphCitiesModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("priority")
    @Expose
    private String priority;
    @SerializedName("log_date_created")
    @Expose
    private String logDateCreated;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("log_date_modified")
    @Expose
    private String logDateModified;
    @SerializedName("modified_by")
    @Expose
    private String modifiedBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getLogDateCreated() {
        return logDateCreated;
    }

    public void setLogDateCreated(String logDateCreated) {
        this.logDateCreated = logDateCreated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLogDateModified() {
        return logDateModified;
    }

    public void setLogDateModified(String logDateModified) {
        this.logDateModified = logDateModified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
