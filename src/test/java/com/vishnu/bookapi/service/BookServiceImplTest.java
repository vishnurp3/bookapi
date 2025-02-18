package com.vishnu.bookapi.service;

import com.vishnu.bookapi.dto.BookRequestDto;
import com.vishnu.bookapi.dto.BookResponseDto;
import com.vishnu.bookapi.entity.Book;
import com.vishnu.bookapi.exception.BookNotFoundException;
import com.vishnu.bookapi.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("addBook: Should create and return a new BookResponseDto")
    void testAddBook_Success() {
        BookRequestDto request = new BookRequestDto("Effective Java", "Joshua Bloch", "Best practices in Java");
        Book savedBook = Book.builder()
                .id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .description("Best practices in Java")
                .build();
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        BookResponseDto response = bookService.addBook(request);
        assertNotNull(response, "The response should not be null");
        assertEquals(1L, response.id(), "Book ID should be 1L");
        assertEquals("Effective Java", response.title(), "Book title mismatch");
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook: Should update existing book and return updated BookResponseDto")
    void testUpdateBook_Success() {
        Long bookId = 1L;
        Book existingBook = Book.builder()
                .id(bookId)
                .title("Old Title")
                .author("Old Author")
                .description("Old Description")
                .build();
        BookRequestDto updateRequest = new BookRequestDto("New Title", "New Author", "New Description");
        Book updatedBook = Book.builder()
                .id(bookId)
                .title("New Title")
                .author("New Author")
                .description("New Description")
                .build();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);
        BookResponseDto response = bookService.updateBook(bookId, updateRequest);
        assertNotNull(response, "The updated response should not be null");
        assertEquals("New Title", response.title(), "Updated title mismatch");
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    @DisplayName("updateBook: Should throw BookNotFoundException when book does not exist")
    void testUpdateBook_BookNotFound() {
        Long bookId = 100L;
        BookRequestDto updateRequest = new BookRequestDto("New Title", "New Author", "New Description");
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(bookId, updateRequest),
                "Expected BookNotFoundException for non-existent book");
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteBook: Should delete the book successfully")
    void testDeleteBook_Success() {
        Long bookId = 1L;
        Book existingBook = Book.builder()
                .id(bookId)
                .title("Test Title")
                .author("Test Author")
                .description("Test Description")
                .build();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        bookService.deleteBook(bookId);
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).delete(existingBook);
    }

    @Test
    @DisplayName("deleteBook: Should throw BookNotFoundException when trying to delete non-existent book")
    void testDeleteBook_BookNotFound() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(bookId),
                "Expected BookNotFoundException when book is not found");
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getBook: Should return BookResponseDto for existing book")
    void testGetBook_Success() {
        Long bookId = 1L;
        Book existingBook = Book.builder()
                .id(bookId)
                .title("Test Title")
                .author("Test Author")
                .description("Test Description")
                .build();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        BookResponseDto response = bookService.getBook(bookId);
        assertNotNull(response, "Response should not be null");
        assertEquals("Test Title", response.title(), "Book title should match");
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("getBook: Should throw BookNotFoundException when book is not found")
    void testGetBook_BookNotFound() {
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.getBook(bookId),
                "Expected exception when book is not found");
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("getAllBooks: Should return list of BookResponseDto")
    void testGetAllBooks_Success() {
        Book book1 = Book.builder().id(1L).title("Title1").author("Author1").description("Description1").build();
        Book book2 = Book.builder().id(2L).title("Title2").author("Author2").description("Description2").build();
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        List<BookResponseDto> books = bookService.getAllBooks();
        assertNotNull(books, "The list of books should not be null");
        assertEquals(2, books.size(), "There should be two books in the list");
        verify(bookRepository, times(1)).findAll();
    }
}
