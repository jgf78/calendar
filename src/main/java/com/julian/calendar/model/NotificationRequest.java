package com.julian.calendar.model;

public record NotificationRequest(
        String message,
        String destination,
        String destinationTelegram
) {
}
