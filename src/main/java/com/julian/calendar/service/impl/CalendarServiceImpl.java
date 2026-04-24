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

        List<CalendarListEntry> calendarList = googleCalendar.calendarList().list().execute().getItems();

        if (calendarList == null || calendarList.isEmpty()) {
            log.warn("No se encontraron calendarios para el usuario.");
            return Map.of();
        }

        return calendarList.stream().flatMap(calendarEntry -> {
            try {
                List<Event> items = googleCalendar.events().list(calendarEntry.getId()).setTimeMin(min).setTimeMax(max)
                        .setSingleEvents(true).execute().getItems();

                if (items == null)
                    return Stream.empty();

                return items.stream()
                        .map(e -> new EventDto(calendarEntry.getSummary(), e.getSummary(),
                                e.getStart() != null ? e.getStart().getDateTime() : null,
                                e.getEnd() != null ? e.getEnd().getDateTime() : null));

            } catch (Exception e) {
                log.error("Error al consultar calendario [{}]: {}", calendarEntry.getSummary(), e.getMessage());
                return Stream.empty();
            }
        }).collect(Collectors.groupingBy(EventDto::calendarName));
    }

    @Override
    public String getAgendaMessage(LocalDate date) throws Exception {
        Map<String, List<EventDto>> data = getEventsByDate(date);
        StringBuilder sb = new StringBuilder();
        sb.append("📅 Agenda de hoy — ").append(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        int total = data.values().stream().mapToInt(List::size).sum();
        sb.append("🧠 Total eventos: ").append(total).append("\n\n");
        if (total == 0) {
            sb.append("✅ No hay eventos para hoy");
            return sb.toString();
        }
        data.forEach((calendar, events) -> {
            sb.append(getEmoji(calendar)).append(" ").append(calendar).append("\n");
            events.stream().sorted(Comparator.comparing(this::safeDate)).forEach(event -> {
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

    private OffsetDateTime safeDate(EventDto e) {
        try {
            return e.start() != null ? OffsetDateTime.parse(e.start().toString()) : OffsetDateTime.MAX;
        } catch (Exception ex) {
            return OffsetDateTime.MAX;
        }
    }

    private String formatHour(Object start) {
        try {
            if (start == null)
                return null;
            String raw = start.toString();
            if (!raw.contains("T"))
                return null;
            return OffsetDateTime.parse(raw).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return null;
        }
    }

    private String getEmoji(String calendar) {

        String name = calendar.toLowerCase();

        if (name.contains("facturas"))
            return "💳";

        if (name.contains("cumpleaños"))
            return "🎂";

        if (name.contains("efemérides"))
            return "📜";

        if (name.contains("juli calendario"))
            return "👤";

        if (name.contains("médico mama") || name.contains("medico mama"))
            return "🏥";

        if (name.contains("santa pola"))
            return "🏖️";

        if (name.contains("vacaciones"))
            return "✈️";

        if (name.contains("festivos cristianos"))
            return "⛪";

        if (name.contains("festivos en españa"))
            return "🇪🇸";

        return "📂";
    }
}