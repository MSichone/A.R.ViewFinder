package com.masitano.arviewfinder.models;

/**
 * Created by Masitano on 6/21/2017.
 *
 * This class stores data on the source of an Urban Observatory Sensor
 *
 */

public class Source {
    private String document;
    private String webDisplayName;
    private String fancyName;
    private String dbName;
    private Boolean thirdParty;

    public Source() {
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getWebDisplayName() {
        return webDisplayName;
    }

    public void setWebDisplayName(String webDisplayName) {
        this.webDisplayName = webDisplayName;
    }

    public String getFancyName() {
        return fancyName;
    }

    public void setFancyName(String fancyName) {
        this.fancyName = fancyName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Boolean getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(Boolean thirdParty) {
        this.thirdParty = thirdParty;
    }
}
