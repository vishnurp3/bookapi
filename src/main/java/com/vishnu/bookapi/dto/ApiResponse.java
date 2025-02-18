package com.vishnu.bookapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Generic API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Indicates whether the operation was successful", example = "true")
    private boolean success;

    @Schema(description = "Payload of the response")
    private T data;

    @Schema(description = "Message providing additional details about the response", example = "Operation completed successfully")
    private String message;
}
