package com.vishnu.bookapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO representing a book resource")
public record BookResponseDto(
        @Schema(description = "Unique identifier of the book", example = "1")
        Long id,

        @Schema(description = "Title of the book", example = "Effective Java")
        String title,

        @Schema(description = "Author of the book", example = "Joshua Bloch")
        String author,

        @Schema(description = "Description of the book", example = "A comprehensive guide to best practices in Java")
        String description
) {
}
