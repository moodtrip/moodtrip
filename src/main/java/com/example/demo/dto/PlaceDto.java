package com.example.demo.dto;

import com.example.demo.model.PlaceInfo;

import java.util.List;

public class PlaceDto {
    private List<PlaceInfo> places;
    private String comment;

    public PlaceDto(List<PlaceInfo> places, String comment) {
        this.places = places;
        this.comment = comment;
    }

    public List<PlaceInfo> getPlaces() {
        return places;
    }
    public void setPlaces(List<PlaceInfo> places) {
        this.places = places;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
