package com.vishnu.bookapi.security;

import com.vishnu.bookapi.entity.Role;
import com.vishnu.bookapi.entity.User;
import com.vishnu.bookapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("loadUserByUsername: Should return valid UserDetails when user exists")
    void testLoadUserByUsername_Success() {
        String username = "testUser";
        Role roleUser = Role.builder().id(1L).name("ROLE_USER").build();
        User user = User.builder()
                .id(1L)
                .username(username)
                .password("encodedPassword")
                .roles(Set.of(roleUser))
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        var userDetails = customUserDetailsService.loadUserByUsername(username);
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals(username, userDetails.getUsername(), "Username should match");
        assertEquals("encodedPassword", userDetails.getPassword(), "Password should match");
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")), "User should have ROLE_USER authority");
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("loadUserByUsername: Should throw UsernameNotFoundException when user does not exist")
    void testLoadUserByUsername_UserNotFound() {
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username),
                "Expected UsernameNotFoundException when user is not found");
        verify(userRepository, times(1)).findByUsername(username);
    }
}
