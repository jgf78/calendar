package com.julian.calendar.scheduler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.julian.calendar.config.RestClient;
import com.julian.calendar.model.NotificationRequest;
import com.julian.calendar.service.CalendarService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DailyCalendar {
    
    private static final String BOT = "bot";

    private static final String TELEGRAM = "telegram";

    @Value("${notificator.url}")
    private String notificatorUrl;

    private final CalendarService calendarService;
    private final RestClient restClient;

    public DailyCalendar(CalendarService calendarService,
            RestClient restClient) {
        this.calendarService = calendarService;
        this.restClient = restClient;
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void sendDailyCalendar() {

        LocalDate date = LocalDate.now();

        log.info("sendDailyCalendar - Agenda del día: {}",
                date.format(DateTimeFormatter.ISO_LOCAL_DATE));

        try {
            String message = calendarService.getAgendaMessage(date);

            NotificationRequest body = new NotificationRequest(
                    message,
                    TELEGRAM,
                    BOT
            );
            
            ResponseEntity<String> response = restClient.post(
                    notificatorUrl,
                    body
                );
            
            log.info("Notificación enviada. Status: {}", response.getStatusCode());

        } catch (Exception e) {
            log.error("Excepción en DailyCalendar - sendDailyCalendar", e);
        }
    }
}