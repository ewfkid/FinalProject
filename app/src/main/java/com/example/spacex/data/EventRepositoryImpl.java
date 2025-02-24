package com.example.spacex.data;

import androidx.annotation.NonNull;

import com.example.spacex.data.dto.EventDto;
import com.example.spacex.data.network.RetrofitFactory;
import com.example.spacex.data.source.EventApi;
import com.example.spacex.data.utils.CallToConsumer;
import com.example.spacex.domain.event.EventRepository;
import com.example.spacex.domain.entity.FullEventEntity;
import com.example.spacex.domain.entity.ItemEventEntity;
import com.example.spacex.domain.entity.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventRepositoryImpl implements EventRepository {

    private EventApi eventApi = RetrofitFactory.getInstance().getEventApi();
    private static EventRepositoryImpl INSTANCE;
    private EventRepositoryImpl() {}

    public static synchronized EventRepositoryImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventRepositoryImpl();
        }
        return INSTANCE;
    }

    @Override
    public void getAllEvents(Consumer<Status<List<ItemEventEntity>>> callback) {
        eventApi.getAllEvents().enqueue(new CallToConsumer<>(
                callback,
                eventsDto -> {
                    ArrayList<ItemEventEntity> result = new ArrayList<>(eventsDto.size());
                    for (EventDto event : eventsDto) {
                        final String resultId = event.id;
                        final String title = event.title;
                        if (resultId != null && title != null) {
                            result.add(new ItemEventEntity(resultId, title));
                        }
                    }
                    return result;
                }));

    }

    @Override
    public void getEvent(@NonNull String id, Consumer<Status<FullEventEntity>> callback) {
        eventApi.getEventById(id).enqueue(new CallToConsumer<>(
                callback,
                event -> {
                    final String resultId = event.id;
                    final String title = event.title;
                    final String eventDateUtc = event.eventDateUtc;
                    final String eventDetails = event.eventDetails;
                    final String flightNumber = event.flightNumber;
                    if (
                            resultId != null
                            && title != null
                            && eventDateUtc != null
                            && eventDetails != null
                            && flightNumber != null) {
                        return new FullEventEntity(
                                resultId,
                                title,
                                eventDateUtc,
                                eventDetails,
                                flightNumber
                        );
                    } else {
                        return null;
                    }

                }
        ));
    }
}
