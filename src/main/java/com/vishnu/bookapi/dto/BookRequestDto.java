package com.vishnu.bookapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for creating or updating a book")
public record BookRequestDto(

        @Schema(description = "Title of the book", example = "Effective Java")
        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must be less than 255 characters")
        String title,

        @Schema(description = "Author of the book", example = "Joshua Bloch")
        @NotBlank(message = "Author must not be blank")
        @Size(max = 255, message = "Author must be less than 255 characters")
        String author,

        @Schema(description = "Description of the book", example = "A comprehensive guide to best practices in Java")
        @Size(max = 1000, message = "Description must be less than 1000 characters")
        String description
) {
}
