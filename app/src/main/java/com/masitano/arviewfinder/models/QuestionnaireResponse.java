package com.masitano.arviewfinder.models;

import com.google.firebase.database.Exclude;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Masitano on 8/2/2017.
 */

public class QuestionnaireResponse {
    private String userId;
    private String time;
    private String gender;
    private String age;
    private String knowledgeOfGps;
    private boolean usedGoogleMaps;
    private boolean usedFourSquare;
    private boolean usedUber;
    private boolean usedSnapMap;
    private String knowledgeOfAr;
    private boolean usedPokemon;
    private boolean usedYelp;
    private boolean usedWallame;
    private boolean usedSnapFace;
    private String privacyConcerns;
    private String prototypeUsability;
    private boolean selectedAdvertising;
    private boolean selectedNavigation;
    private boolean selectedTourism;
    private boolean selectedTracking;
    private boolean selectedSocial;

    public QuestionnaireResponse() {
        time = new Timestamp(System.currentTimeMillis()).toString();
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

    public String getKnowledgeOfAr() {
        return knowledgeOfAr;
    }

    public void setKnowledgeOfAr(String knowledgeOfAr) {
        this.knowledgeOfAr = knowledgeOfAr;
    }

    public boolean isUsedPokemon() {
        return usedPokemon;
    }

    public void setUsedPokemon(boolean usedPokemon) {
        this.usedPokemon = usedPokemon;
    }

    public boolean isUsedYelp() {
        return usedYelp;
    }

    public void setUsedYelp(boolean usedYelp) {
        this.usedYelp = usedYelp;
    }

    public boolean isUsedWallame() {
        return usedWallame;
    }

    public void setUsedWallame(boolean usedWallame) {
        this.usedWallame = usedWallame;
    }

    public boolean isUsedSnapFace() {
        return usedSnapFace;
    }

    public void setUsedSnapFace(boolean usedSnapFace) {
        this.usedSnapFace = usedSnapFace;
    }

    public String getPrivacyConcerns() {
        return privacyConcerns;
    }

    public void setPrivacyConcerns(String privacyConcerns) {
        this.privacyConcerns = privacyConcerns;
    }

    public String getPrototypeUsability() {
        return prototypeUsability;
    }

    public void setPrototypeUsability(String prototypeUsability) {
        this.prototypeUsability = prototypeUsability;
    }

    public boolean isSelectedAdvertising() {
        return selectedAdvertising;
    }

    public void setSelectedAdvertising(boolean selectedAdvertising) {
        this.selectedAdvertising = selectedAdvertising;
    }

    public boolean isSelectedNavigation() {
        return selectedNavigation;
    }

    public void setSelectedNavigation(boolean selectedNavigation) {
        this.selectedNavigation = selectedNavigation;
    }

    public boolean isSelectedTourism() {
        return selectedTourism;
    }

    public void setSelectedTourism(boolean selectedTourism) {
        this.selectedTourism = selectedTourism;
    }

    public boolean isSelectedTracking() {
        return selectedTracking;
    }

    public void setSelectedTracking(boolean selectedTracking) {
        this.selectedTracking = selectedTracking;
    }

    public boolean isSelectedSocial() {
        return selectedSocial;
    }

    public void setSelectedSocial(boolean selectedSocial) {
        this.selectedSocial = selectedSocial;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        result.put("time",time);
        result.put("gender",gender);
        result.put("age",age);
        result.put("knowledgeOfGps",knowledgeOfGps);
        result.put("usedGoogleMaps",usedGoogleMaps);
        result.put("usedFourSquare",usedFourSquare);
        result.put("usedUber",usedUber);
        result.put("usedSnapMap",usedSnapMap);
        result.put("knowledgeOfAr",knowledgeOfAr);
        result.put("usedPokemon",usedPokemon);
        result.put("usedYelp",usedYelp);
        result.put("usedWallaMe",usedWallame);
        result.put("usedSnapFace",usedSnapFace);
        result.put("privacyConcerns",privacyConcerns);
        result.put("prototypeUsability",prototypeUsability);
        result.put("selectedAdvertising",selectedAdvertising);
        result.put("selectedNavigation",selectedNavigation);
        result.put("selectedSocial",selectedSocial);
        result.put("selectedTourism",selectedTourism);
        result.put("selectedTracking",selectedTracking);
        return result;
    }

}
