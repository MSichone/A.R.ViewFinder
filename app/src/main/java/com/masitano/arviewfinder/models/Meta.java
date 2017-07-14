package com.masitano.arviewfinder.models;

/**
 * Created by Masitano on 7/6/2017.
 */

public class Meta {
    private String name;
    private String theme;
    private String units;

    public Meta() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public String toString() {
        return "Meta{" +
                "name='" + name + '\'' +
                ", theme='" + theme + '\'' +
                ", units='" + units + '\'' +
                '}';
    }
}
