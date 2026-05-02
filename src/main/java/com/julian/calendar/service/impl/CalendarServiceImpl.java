package com.julian.calendar.service.impl;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.julian.calendar.config.GoogleCalendarProperties;
import com.julian.calendar.config.GoogleCalendarProperties.Item;
import com.julian.calendar.model.EventDto;
import com.julian.calendar.service.CalendarService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalendarServiceImpl implements CalendarService {

    private final Calendar googleCalendar;
    private final GoogleCalendarProperties properties;

    public CalendarServiceImpl(Calendar googleCalendar,
                               GoogleCalendarProperties properties) {
        this.googleCalendar = googleCalendar;
        this.properties = properties;
    }

    @Override
    public Map<String, List<EventDto>> getEventsByDate(LocalDate date) throws Exception {

        List<Item> calendars = properties.getItems();

        log.info("Consultando calendarios {} para la fecha {}", calendars, date);

        ZoneId zone = ZoneId.of("Europe/Madrid");

        ZonedDateTime start = date.atStartOfDay(zone);
        ZonedDateTime end = start.plusDays(1);

        DateTime min = new DateTime(start.toInstant().toString());
        DateTime max = new DateTime(end.toInstant().toString());

        return calendars.stream().flatMap(calendar -> {

            String calendarId = calendar.getId();
            String calendarName = calendar.getName();

            try {
                List<Event> events = googleCalendar.events()
                        .list(calendarId)
                        .setTimeMin(min)
                        .setTimeMax(max)
                        .setTimeZone("Europe/Madrid")
                        .setSingleEvents(true)
                        .execute()
                        .getItems();

                log.info("Calendario [{}] → {} eventos",
                        calendarName,
                        events != null ? events.size() : 0);

                if (events == null) return Stream.empty();

                return events.stream()
                        .map(e -> new EventDto(
                                calendarName,
                                e.getSummary(),
                                getStart(e),  
                                getEnd(e)
                        ));

            } catch (Exception e) {
                log.error("Error en calendario [{}]: {}", calendarName, e.getMessage());
                return Stream.empty();
            }
        }).collect(Collectors.groupingBy(EventDto::calendarName));
    }

    @Override
    public String getAgendaMessage(LocalDate date) throws Exception {

        Map<String, List<EventDto>> data = getEventsByDate(date);

        StringBuilder sb = new StringBuilder();

        sb.append("📅 Agenda de hoy — ")
          .append(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
          .append("\n\n");

        int total = data.values().stream().mapToInt(List::size).sum();

        sb.append("🧠 Total eventos: ").append(total).append("\n\n");

        if (total == 0) {
            sb.append("✅ No hay eventos para hoy");
            return sb.toString();
        }

        data.forEach((calendar, events) -> {

            sb.append(getEmoji(calendar)).append(" ").append(calendar).append("\n");

            events.stream()
                    .sorted(Comparator.comparing(this::safeDate))
                    .forEach(event -> {

                        String hour = formatHour(event.start());

                        if (hour != null) {
                            sb.append("• ").append(hour).append(" ");
                        } else {
                            sb.append("• ");
                        }

                        sb.append(event.title()).append("\n");
                    });

            sb.append("\n");
        });

        return sb.toString();
    }

    private DateTime getStart(Event e) {
        if (e.getStart() == null) return null;

        return e.getStart().getDateTime() != null
                ? e.getStart().getDateTime()
                : e.getStart().getDate();
    }

    private DateTime getEnd(Event e) {
        if (e.getEnd() == null) return null;

        return e.getEnd().getDateTime() != null
                ? e.getEnd().getDateTime()
                : e.getEnd().getDate();
    }

    private OffsetDateTime safeDate(EventDto e) {
        try {
            return e.start() != null
                    ? OffsetDateTime.parse(e.start().toString())
                    : OffsetDateTime.MAX;
        } catch (Exception ex) {
            return OffsetDateTime.MAX;
        }
    }

    private String formatHour(DateTime start) {
        try {
            if (start == null) return null;

            String raw = start.toString();

            if (!raw.contains("T")) return null; 

            return OffsetDateTime.parse(raw)
                    .toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm"));

        } catch (Exception e) {
            return null;
        }
    }

    private String getEmoji(String calendar) {

        String name = calendar.toLowerCase();

        if (name.contains("facturas")) return "💳";
        if (name.contains("cumpleaños")) return "🎂";
        if (name.contains("efemérides")) return "📜";
        if (name.contains("juli calendario")) return "👤";
        if (name.contains("médico") || name.contains("medico")) return "🏥";
        if (name.contains("santa pola")) return "🏖️";
        if (name.contains("vacaciones")) return "✈️";
        if (name.contains("festivos cristianos")) return "⛪";
        if (name.contains("festivos en españa")) return "🇪🇸";

        return "📂";
    }
}