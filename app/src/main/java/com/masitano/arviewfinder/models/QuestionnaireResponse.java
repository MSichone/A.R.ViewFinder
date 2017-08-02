package com.masitano.arviewfinder.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Masitano on 8/2/2017.
 */

public class QuestionnaireResponse {
    private String userId;
    private String gender;
    private String age;
    private String knowledgeOfGps;
    private boolean usedGoogleMaps;
    private boolean usedFourSquare;
    private boolean usedUber;
    private boolean usedSnapMap;

    public QuestionnaireResponse() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getKnowledgeOfGps() {
        return knowledgeOfGps;
    }

    public void setKnowledgeOfGps(String knowledgeOfGps) {
        this.knowledgeOfGps = knowledgeOfGps;
    }

    public boolean isUsedGoogleMaps() {
        return usedGoogleMaps;
    }

    public void setUsedGoogleMaps(boolean usedGoogleMaps) {
        this.usedGoogleMaps = usedGoogleMaps;
    }

    public boolean isUsedFourSquare() {
        return usedFourSquare;
    }

    public void setUsedFourSquare(boolean usedFourSquare) {
        this.usedFourSquare = usedFourSquare;
    }

    public boolean isUsedUber() {
        return usedUber;
    }

    public void setUsedUber(boolean usedUber) {
        this.usedUber = usedUber;
    }

    public boolean isUsedSnapMap() {
        return usedSnapMap;
    }

    public void setUsedSnapMap(boolean usedSnapMap) {
        this.usedSnapMap = usedSnapMap;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        result.put("gender",gender);
        result.put("age",age);
        result.put("knowledgeOfGps",knowledgeOfGps);
        result.put("usedGoogleMaps",usedGoogleMaps);
        result.put("usedFourSquare",usedFourSquare);
        result.put("usedUber",usedUber);
        result.put("usedSnapMap",usedSnapMap);
        return result;
    }

}
