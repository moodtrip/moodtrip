package com.example.demo.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class PlaceInfo {
    private String name;
    private String category;
    private String address;
    private String imageUrl;
    private String placeUrl;
    private double rating; // 평점
    private String description; // 간단 요약

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPlaceUrl() { return placeUrl; }
    public void setPlaceUrl(String placeUrl) { this.placeUrl = placeUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public static List<PlaceInfo> fromJsonArray(String jsonArray) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonArray, new TypeReference<>() {});
    }
}
