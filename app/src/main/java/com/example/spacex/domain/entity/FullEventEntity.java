package com.example.spacex.domain.entity;

import androidx.annotation.NonNull;

public class FullEventEntity {
    @NonNull
    private final String id;

    @NonNull
    private final String title;

    @NonNull
    private final String eventDateUtc;

    @NonNull
    private final String eventDetails;

    @NonNull
    private final String flightNumber;

    public FullEventEntity(@NonNull String id,
                           @NonNull String title,
                           @NonNull String eventDateUtc,
                           @NonNull String eventDetails,
                           @NonNull String flightNumber
    ) {
        this.id = id;
        this.title = title;
        this.eventDateUtc = eventDateUtc;
        this.eventDetails = eventDetails;
        this.flightNumber = flightNumber;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getEventDateUtc() {
        return eventDateUtc;
    }

    @NonNull
    public String getEventDetails() {
        return eventDetails;
    }

    @NonNull
    public String getFlightNumber() {
        return flightNumber;
    }
}
