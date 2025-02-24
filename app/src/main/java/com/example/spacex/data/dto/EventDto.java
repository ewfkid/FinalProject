package com.example.spacex.data.dto;


import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class EventDto {
    @Nullable
    @SerializedName("id")
    public String id;

    @Nullable
    @SerializedName("title")
    public String title;

    @Nullable
    @SerializedName("event_date_utc")
    public String eventDateUtc;

    @Nullable
    @SerializedName("flight_number")
    public String flightNumber;

    @Nullable
    @SerializedName("details")
    public String eventDetails;
}
