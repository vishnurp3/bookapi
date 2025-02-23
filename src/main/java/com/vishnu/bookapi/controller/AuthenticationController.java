package com.vishnu.bookapi.controller;

import com.vishnu.bookapi.dto.ApiResponse;
import com.vishnu.bookapi.exception.JwtAuthenticationException;
import com.vishnu.bookapi.security.CustomUserDetailsService;
import com.vishnu.bookapi.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;


    @Operation(
            summary = "User Login",
            description = "Authenticate user credentials and retrieve JWT access and refresh tokens."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully authenticated. Tokens generated.",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials.", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String accessToken = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .success(true)
                .data(new AuthResponse(accessToken, refreshToken))
                .message("Login successful")
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Refresh Access Token",
            description = "Generate a new access token using a valid refresh token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Access token refreshed successfully.",
                    content = @Content(schema = @Schema(implementation = RefreshResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid refresh token.", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        String username = jwtUtil.extractUsername(refreshRequest.getRefreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwtUtil.validateToken(refreshRequest.getRefreshToken(), userDetails)) {
            final String newAccessToken = jwtUtil.generateToken(userDetails);
            RefreshResponse refreshTokenResponse = new RefreshResponse(newAccessToken);
            ApiResponse<RefreshResponse> response = ApiResponse.<RefreshResponse>builder()
                    .success(true)
                    .data(refreshTokenResponse)
                    .message("Token refreshed successfully")
                    .build();
            return ResponseEntity.ok(response);
        } else {
            throw new JwtAuthenticationException("Invalid refresh token");
        }
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Request payload for user authentication")
    public static class AuthRequest {
        @Schema(description = "The username of the user", example = "johndoe")
        private String username;
        @Schema(description = "The password of the user", example = "password123", format = "password")
        private String password;
    }

    @AllArgsConstructor
    @Getter
    @Schema(description = "Response containing JWT access and refresh tokens")
    public static class AuthResponse {

        @Schema(description = "JWT access token")
        private String accessToken;
        @Schema(description = "JWT refresh token")
        private String refreshToken;
    }

    @Data
    @Schema(description = "Request payload for refreshing the JWT access token")
    public static class RefreshRequest {
        @Schema(description = "Refresh token to be validated")
        private String refreshToken;
    }

    @AllArgsConstructor
    @Getter
    @Schema(description = "Response containing the new JWT access token")
    public static class RefreshResponse {
        @Schema(description = "Newly issued JWT access token")
        private String accessToken;
    }
}
