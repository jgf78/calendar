package com.julian.calendar.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.julian.calendar.model.EventDto;
import com.julian.calendar.service.CalendarService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/calendar")
@Tag(name = "Calendar API", description = "Endpoints para Google Calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @Operation(summary = "Test de conexión con Google Calendar", description = "Verifica que el servicio está funcionando correctamente")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Servicio OK"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor") })
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Servicio Calendar OK");
    }

    @Operation(summary = "Obtener eventos por fecha", description = "Devuelve todos los eventos agrupados por calendario para una fecha concreta")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Eventos obtenidos correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetro de fecha incorrecto"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor") })
    @GetMapping("/day")
    public ResponseEntity<Map<String, List<EventDto>>> getEventsByDate(

            @Parameter(description = "Fecha a consultar en formato ISO", example = "2026-04-23", required = true, schema = @Schema(type = "string", format = "date")) @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date

    ) throws Exception {

        return ResponseEntity.ok(calendarService.getEventsByDate(date));
    }

    @Operation(summary = "Obtener eventos por fecha", description = "Devuelve todos los eventos agrupados por calendario para una fecha concreta")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Eventos obtenidos correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetro de fecha incorrecto"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor") })
    @GetMapping("/day/message")
    public ResponseEntity<String> getAgendaMessage(
            @Parameter(description = "Fecha a consultar en formato ISO", example = "2026-04-23", required = true, schema = @Schema(type = "string", format = "date")) @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
            throws Exception {

        return ResponseEntity.ok(calendarService.getAgendaMessage(date));
    }
}