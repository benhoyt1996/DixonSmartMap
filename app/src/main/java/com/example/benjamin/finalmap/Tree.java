package com.example.benjamin.finalmap;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Tree {
    public String Coordinates;
    public String description;
    public String latinName;
    public String name;
    public String photos;

    public Tree() {

    }

    public Tree(String Coordinates, String description, String latinName, String name, String photos) {
        this.Coordinates = Coordinates;
        this.description = description;
        this.latinName = latinName;
        this.name = name;
        this.photos = photos;
    }

    public Tree( String description, String latinName, String name) {
        this.Coordinates = Coordinates;
        this.description = description;
        this.latinName = latinName;
        this.name = name;
        this.photos = photos;
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
       // result.put("Coordinates", Coordinates);
        result.put("description", description);
        result.put("latinName", latinName);
        result.put("name", name);
       // result.put("photos", photos);

        return result;
    }


    public String getCoordinates() {
        return Coordinates;
    }

    public void setCoordinates(String Coordinates) {
        this.Coordinates = Coordinates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getlatinName() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }


    @Override
    public String toString() {
        return "Name: "+name+ " Latin Name: "+latinName+ " Description: "+description;
    }
}
