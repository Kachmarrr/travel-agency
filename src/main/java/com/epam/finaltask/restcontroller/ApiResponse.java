package com.epam.finaltask.restcontroller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String statusCode;      // наприклад "OK" або "ERROR"
    private String statusMessage;   // читабельне повідомлення
    private T data;                 // одиничний об'єкт (create/update)
    private List<T> results;        // список (для findAll / findByUser)
}
