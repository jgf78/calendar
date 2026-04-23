package com.julian.calendar.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.julian.calendar.model.EventDto;

public interface CalendarService {
   
    Map<String, List<EventDto>> getEventsByDate(LocalDate date) throws Exception;
}