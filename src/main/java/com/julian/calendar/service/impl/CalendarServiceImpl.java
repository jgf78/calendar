package com.julian.calendar.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.julian.calendar.model.EventDto;
import com.julian.calendar.service.CalendarService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalendarServiceImpl implements CalendarService {

    private final Calendar googleCalendar;

    public CalendarServiceImpl(Calendar googleCalendar) {
        this.googleCalendar = googleCalendar;
    }

    @Override
    public Map<String, List<EventDto>> getEventsByDate(LocalDate date) throws Exception {

        log.info("Iniciando consulta multicalendario para la fecha: {}", date);

        ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        DateTime min = new DateTime(startOfDay.toInstant().toEpochMilli());
        DateTime max = new DateTime(endOfDay.toInstant().toEpochMilli());

        List<CalendarListEntry> calendarList =
                googleCalendar.calendarList().list().execute().getItems();

        if (calendarList == null || calendarList.isEmpty()) {
            log.warn("No se encontraron calendarios para el usuario.");
            return Map.of();
        }

        return calendarList.stream()
                .flatMap(calendarEntry -> {
                    try {
                        List<Event> items = googleCalendar.events()
                                .list(calendarEntry.getId())
                                .setTimeMin(min)
                                .setTimeMax(max)
                                .setSingleEvents(true)
                                .execute()
                                .getItems();

                        if (items == null) return Stream.empty();

                        return items.stream().map(e -> new EventDto(
                                calendarEntry.getSummary(),
                                e.getSummary(),
                                e.getStart() != null ? e.getStart().getDateTime() : null,
                                e.getEnd() != null ? e.getEnd().getDateTime() : null
                        ));

                    } catch (Exception e) {
                        log.error("Error al consultar calendario [{}]: {}",
                                calendarEntry.getSummary(), e.getMessage());
                        return Stream.empty();
                    }
                })
                .collect(Collectors.groupingBy(EventDto::calendarName));
    }
}