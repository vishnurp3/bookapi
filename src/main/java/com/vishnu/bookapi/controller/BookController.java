package com.vishnu.bookapi.controller;

import com.vishnu.bookapi.dto.ApiResponse;
import com.vishnu.bookapi.dto.BookRequestDto;
import com.vishnu.bookapi.dto.BookResponseDto;
import com.vishnu.bookapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "Add a new book",
            description = "Creates a new book resource. Accessible only by users with ADMIN role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Book created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<BookResponseDto>> addBook(@Valid @RequestBody BookRequestDto bookRequestDto) {
        log.info("Admin adding a new book");
        BookResponseDto createdBook = bookService.addBook(bookRequestDto);
        ApiResponse<BookResponseDto> response = ApiResponse.<BookResponseDto>builder()
                .success(true)
                .data(createdBook)
                .message("Book created successfully")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update an existing book",
            description = "Updates a book's details by its ID. Accessible only by users with ADMIN role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponseDto>> updateBook(@PathVariable Long id,
                                                                   @Valid @RequestBody BookRequestDto bookRequestDto) {
        log.info("Admin updating book with id: {}", id);
        BookResponseDto updatedBook = bookService.updateBook(id, bookRequestDto);
        ApiResponse<BookResponseDto> response = ApiResponse.<BookResponseDto>builder()
                .success(true)
                .data(updatedBook)
                .message("Book updated successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a book",
            description = "Deletes a book resource by its ID. Accessible only by users with ADMIN role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBook(@PathVariable Long id) {
        log.info("Admin deleting book with id: {}", id);
        bookService.deleteBook(id);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Book deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a book",
            description = "Fetches a book by its ID. Accessible by both ADMIN and USER roles."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found", content = @Content)
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponseDto>> getBook(@PathVariable Long id) {
        log.info("Fetching book with id: {}", id);
        BookResponseDto book = bookService.getBook(id);
        ApiResponse<BookResponseDto> response = ApiResponse.<BookResponseDto>builder()
                .success(true)
                .data(book)
                .message("Book fetched successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all books",
            description = "Fetches all available books. Accessible by both ADMIN and USER roles."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Books fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponseDto>>> getAllBooks() {
        log.info("Fetching all books");
        List<BookResponseDto> books = bookService.getAllBooks();
        ApiResponse<List<BookResponseDto>> response = ApiResponse.<List<BookResponseDto>>builder()
                .success(true)
                .data(books)
                .message("Books fetched successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
