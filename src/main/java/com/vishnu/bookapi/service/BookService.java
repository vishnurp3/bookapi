package com.vishnu.bookapi.service;

import com.vishnu.bookapi.dto.BookRequestDto;
import com.vishnu.bookapi.dto.BookResponseDto;

import java.util.List;

public interface BookService {
    BookResponseDto addBook(BookRequestDto bookRequestDto);

    BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto);

    void deleteBook(Long id);

    BookResponseDto getBook(Long id);

    List<BookResponseDto> getAllBooks();
}
