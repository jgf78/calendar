package com.julian.calendar.config;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Configuration
public class GoogleCalendarConfig {

    private static final String APPLICATION_NAME = "Calendar App";

    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);

    @Value("${google.service.account.path}")
    private String serviceAccountPath;

    @Bean
    public Calendar googleCalendarService() throws Exception {

        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var jsonFactory = GsonFactory.getDefaultInstance();

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(serviceAccountPath))
                .createScoped(SCOPES);

        return new Calendar.Builder(
                httpTransport,
                jsonFactory,
                new HttpCredentialsAdapter(credentials)
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }
}