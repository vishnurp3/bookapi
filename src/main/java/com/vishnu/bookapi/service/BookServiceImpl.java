package com.vishnu.bookapi.service;

import com.vishnu.bookapi.dto.BookRequestDto;
import com.vishnu.bookapi.dto.BookResponseDto;
import com.vishnu.bookapi.entity.Book;
import com.vishnu.bookapi.exception.BookNotFoundException;
import com.vishnu.bookapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookResponseDto addBook(BookRequestDto bookRequestDto) {
        log.info("Adding new book with title: {}", bookRequestDto.title());
        Book book = Book.builder()
                .title(bookRequestDto.title())
                .author(bookRequestDto.author())
                .description(bookRequestDto.description())
                .build();
        Book saved = bookRepository.save(book);
        return mapToDto(saved);
    }

    @Override
    public BookResponseDto updateBook(Long id, BookRequestDto bookRequestDto) {
        log.info("Updating book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        book.setTitle(bookRequestDto.title());
        book.setAuthor(bookRequestDto.author());
        book.setDescription(bookRequestDto.description());
        Book updated = bookRepository.save(book);
        return mapToDto(updated);
    }

    @Override
    public void deleteBook(Long id) {
        log.info("Deleting book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        bookRepository.delete(book);
    }

    @Override
    public BookResponseDto getBook(Long id) {
        log.info("Fetching book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
        return mapToDto(book);
    }

    @Override
    public List<BookResponseDto> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    private BookResponseDto mapToDto(Book book) {
        return new BookResponseDto(book.getId(), book.getTitle(), book.getAuthor(), book.getDescription());
    }
}
