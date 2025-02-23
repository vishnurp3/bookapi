package com.vishnu.bookapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishnu.bookapi.controller.AuthenticationController;
import com.vishnu.bookapi.dto.BookRequestDto;
import com.vishnu.bookapi.entity.Book;
import com.vishnu.bookapi.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integration Tests for Book API")
class BookapiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setUpDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("ADMIN_PASSWORD", () -> "adminpass");
        registry.add("USER_PASSWORD", () -> "userpass");
        registry.add("JWT_SECRET", () -> "a8F3zX9qP1mLb6YwT4VkN2rJ7dCgQ0MH");
    }

    @BeforeEach
    void setupDatabase() {
        bookRepository.deleteAll();
    }

    private String obtainAccessToken(String username, String password) throws Exception {
        AuthenticationController.AuthRequest authRequest = new AuthenticationController.AuthRequest(username, password);
        String loginRequestJson = objectMapper.writeValueAsString(authRequest);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseJson);
        return jsonNode.get("data").get("accessToken").asText();
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/books without authentication returns 401 Unauthorized")
    void givenNoAuthentication_whenGetBooks_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    @DisplayName("ADMIN can add a new book via POST /api/books")
    void givenAdminCredentials_whenAddBook_thenReturnCreatedBook() throws Exception {
        String token = obtainAccessToken("admin", "adminpass");
        BookRequestDto newBook = new BookRequestDto("Integration Test Book", "Test Author", "Test Description");
        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Integration Test Book")))
                .andExpect(jsonPath("$.data.author", is("Test Author")));
    }

    @Test
    @Order(3)
    @DisplayName("USER cannot add a book via POST /api/books (should return 403 Forbidden)")
    void givenUserCredentials_whenAddBook_thenReturnForbidden() throws Exception {
        String token = obtainAccessToken("user", "userpass");
        BookRequestDto newBook = new BookRequestDto("Book Title", "Book Author", "Book Description");
        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    @DisplayName("ADMIN can update an existing book via PUT /api/books/{id}")
    void givenAdminCredentials_whenUpdateExistingBook_thenReturnUpdatedBook() throws Exception {
        String token = obtainAccessToken("admin", "adminpass");
        Book existingBook = Book.builder()
                .title("Old Title")
                .author("Old Author")
                .description("Old Description")
                .build();
        Book savedBook = bookRepository.save(existingBook);
        BookRequestDto updateRequest = new BookRequestDto("New Title", "New Author", "New Description");
        mockMvc.perform(put("/api/books/{id}", savedBook.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("New Title")))
                .andExpect(jsonPath("$.data.author", is("New Author")));
    }

    @Test
    @Order(5)
    @DisplayName("ADMIN updating a non-existent book returns 404 Not Found")
    void givenAdminCredentials_whenUpdateNonexistentBook_thenReturnNotFound() throws Exception {
        String token = obtainAccessToken("admin", "adminpass");
        BookRequestDto updateRequest = new BookRequestDto("New Title", "New Author", "New Description");
        mockMvc.perform(put("/api/books/{id}", 999L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    @DisplayName("ADMIN can delete an existing book via DELETE /api/books/{id}")
    void givenAdminCredentials_whenDeleteExistingBook_thenReturnSuccess() throws Exception {
        String token = obtainAccessToken("admin", "adminpass");
        Book bookToDelete = Book.builder()
                .title("Delete Me")
                .author("Author")
                .description("Description")
                .build();
        Book savedBook = bookRepository.save(bookToDelete);
        mockMvc.perform(delete("/api/books/{id}", savedBook.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @Order(7)
    @DisplayName("ADMIN deleting a non-existent book returns 404 Not Found")
    void givenAdminCredentials_whenDeleteNonexistentBook_thenReturnNotFound() throws Exception {
        String token = obtainAccessToken("admin", "adminpass");
        mockMvc.perform(delete("/api/books/{id}", 999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    @DisplayName("USER can fetch a book via GET /api/books/{id}")
    void givenUserCredentials_whenGetExistingBook_thenReturnBook() throws Exception {
        String token = obtainAccessToken("user", "userpass");
        Book existingBook = Book.builder()
                .title("Fetch Me")
                .author("Author")
                .description("Description")
                .build();
        Book savedBook = bookRepository.save(existingBook);
        mockMvc.perform(get("/api/books/{id}", savedBook.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Fetch Me")));
    }

    @Test
    @Order(9)
    @DisplayName("GET /api/books/{id} for a non-existent book returns 404 Not Found")
    void givenUserCredentials_whenGetNonexistentBook_thenReturnNotFound() throws Exception {
        String token = obtainAccessToken("user", "userpass");
        mockMvc.perform(get("/api/books/{id}", 999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    @DisplayName("USER can fetch all books via GET /api/books")
    void givenUserCredentials_whenGetAllBooks_thenReturnListOfBooks() throws Exception {
        String token = obtainAccessToken("user", "userpass");
        Book bookOne = Book.builder().title("Book One").author("Author One").description("Desc One").build();
        Book bookTwo = Book.builder().title("Book Two").author("Author Two").description("Desc Two").build();
        bookRepository.saveAll(List.of(bookOne, bookTwo));
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    @Order(11)
    @DisplayName("Input validation: POST /api/books with invalid data returns 400 Bad Request")
    void givenAdminCredentials_whenPostBookWithInvalidData_thenReturnBadRequest() throws Exception {
        String token = obtainAccessToken("admin", "adminpass");
        BookRequestDto invalidBook = new BookRequestDto("", "Author", "Description");
        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Title must not be blank")));
    }
}
