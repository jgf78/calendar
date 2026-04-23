package com.julian.calendar.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.DateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventDto(
    
    @Schema(description = "Nombre del calendario al que pertenece el evento", example = "Personal")
    String calendarName,
    
    @Schema(description = "Título o resumen del evento", example = "Cena con amigos")
    String title,
    
    @Schema(description = "Fecha y hora de inicio")
    DateTime start,
    
    @Schema(description = "Fecha y hora de fin")
    DateTime end
) {}