package com.julian.calendar.config;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

@Configuration
public class GoogleCalendarConfig {

    private static final String APPLICATION_NAME = "Calendar App";

    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);

    @Value("${google.credentials.path}")
    private String credentialsPath;

    @Value("${google.tokens.path}")
    private String tokensPath;

    @Bean
    public Calendar googleCalendarService() throws Exception {

        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var jsonFactory = GsonFactory.getDefaultInstance();

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                jsonFactory,
                new InputStreamReader(new FileInputStream(credentialsPath))
        );

        FileDataStoreFactory dataStoreFactory =
                new FileDataStoreFactory(new java.io.File(tokensPath));

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        httpTransport,
                        jsonFactory,
                        clientSecrets,
                        SCOPES
                )
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();

        Credential credential = flow.loadCredential("user");

        if (credential == null) {
            throw new RuntimeException(
                    "No existe token OAuth en " + tokensPath +
                    ". Genera primero login en local."
            );
        }

        return new Calendar.Builder(
                httpTransport,
                jsonFactory,
                credential
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }
}