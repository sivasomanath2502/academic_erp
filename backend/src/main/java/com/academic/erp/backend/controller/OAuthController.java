package com.academic.erp.backend.controller;

import com.academic.erp.backend.dto.TokenExchangeResponse;
import com.academic.erp.backend.dto.TokenInfoResponse;
import com.academic.erp.backend.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    private final TokenService tokenService;

    @GetMapping("/login")
    public RedirectView login() {
        String scope = "openid email profile";
        String authUrl = String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=%s&access_type=offline&prompt=consent",
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode(scope, StandardCharsets.UTF_8)
        );
        return new RedirectView(authUrl);
    }

    @GetMapping("/oauth2/callback")
    public RedirectView callback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if (error != null) {
            log.error("OAuth error: {}", error);
            return new RedirectView("http://localhost:5173?error=" + URLEncoder.encode(error, StandardCharsets.UTF_8));
        }

        if (code == null) {
            log.error("No authorization code received");
            return new RedirectView("http://localhost:5173?error=no_code");
        }

        try {
            // Exchange authorization code for tokens
            TokenExchangeResponse tokenResponse = tokenService.exchangeCode(code);

            // Validate ID token and get user info
            TokenInfoResponse tokenInfo = tokenService.validateIdToken(tokenResponse.getIdToken());

            // Store access token and refresh token in session
            HttpSession session = request.getSession(true);
            session.setAttribute("access_token", tokenResponse.getAccessToken());
            if (tokenResponse.getRefreshToken() != null) {
                session.setAttribute("refresh_token", tokenResponse.getRefreshToken());
            }

            // Set ID token in HTTP-only cookie (allow login for all emails)
            Cookie idTokenCookie = new Cookie("id_token", tokenResponse.getIdToken());
            idTokenCookie.setHttpOnly(true);
            idTokenCookie.setSecure(false); // Set to true in production with HTTPS
            idTokenCookie.setPath("/");
            idTokenCookie.setMaxAge(3600); // 1 hour
            response.addCookie(idTokenCookie);

            // Check if email starts with erphead for redirect
            if (tokenInfo.getEmail() == null || 
                !tokenInfo.getEmail().toLowerCase().startsWith("erphead")) {
                log.warn("User logged in but not authorized: {}", tokenInfo.getEmail());
                // Still allow login, but redirect to access-denied page
                return new RedirectView("http://localhost:5173/access-denied?reason=" + 
                    URLEncoder.encode("Access denied: Not ERP admin", StandardCharsets.UTF_8));
            }

            // Redirect authorized users to add-student page
            return new RedirectView("http://localhost:5173/add-student");

        } catch (Exception e) {
            log.error("Error during OAuth callback", e);
            return new RedirectView("http://localhost:5173?error=" + 
                URLEncoder.encode("Authentication failed", StandardCharsets.UTF_8));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signout(HttpServletRequest request, HttpServletResponse response) {
        // Clear session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Delete ID token cookie
        Cookie idTokenCookie = new Cookie("id_token", "");
        idTokenCookie.setHttpOnly(true);
        idTokenCookie.setSecure(false);
        idTokenCookie.setPath("/");
        idTokenCookie.setMaxAge(0);
        response.addCookie(idTokenCookie);

        // Clear security context
        org.springframework.security.core.context.SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<TokenInfoResponse> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String idToken = extractIdTokenFromCookie(request);

        if (idToken != null) {
            try {
                TokenInfoResponse tokenInfo = tokenService.validateIdToken(idToken);
                return ResponseEntity.ok(tokenInfo);
            } catch (Exception e) {
                log.error("Error getting current user", e);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private String extractIdTokenFromCookie(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("id_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

