package com.bogecom.auth.controller;

import com.bogecom.auth.dto.AuthResponse;
import com.bogecom.auth.dto.ChangePasswordRequest;
import com.bogecom.auth.dto.LoginRequest;
import com.bogecom.auth.dto.RegisterRequest;
import com.bogecom.auth.service.AuthService;
import com.bogecom.common.response.ApiResponse;
import com.bogecom.user.dto.UserResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Value("${app.security.cookie.secure}")
  private boolean secureCookie;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthResponse>> register(
      @Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
    AuthResponse response = authService.register(request, httpRequest);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    String[] tokens = authService.login(request, httpRequest);
    setCookie(httpResponse, "accessToken", tokens[0], 15 * 60);
    setCookie(httpResponse, "refreshToken", tokens[1], 7 * 24 * 60 * 60);
    return ResponseEntity.ok(ApiResponse.success("Login successful", null));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<Void>> refresh(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    String refreshToken = getCookie(httpRequest, "refreshToken");
    String[] tokens = authService.refresh(refreshToken, httpRequest);
    setCookie(httpResponse, "accessToken", tokens[0], 15 * 60);
    setCookie(httpResponse, "refreshToken", tokens[1], 7 * 24 * 60 * 60);
    return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", null));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    String refreshToken = getCookie(httpRequest, "refreshToken");
    authService.logout(refreshToken);
    clearCookie(httpResponse, "accessToken");
    clearCookie(httpResponse, "refreshToken");
    return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
  }

  @PostMapping("/logout-all")
  public ResponseEntity<ApiResponse<Void>> logoutAll(
      @AuthenticationPrincipal Long userId, HttpServletResponse httpResponse) {
    authService.logoutAll(userId);
    clearCookie(httpResponse, "accessToken");
    clearCookie(httpResponse, "refreshToken");
    return ResponseEntity.ok(ApiResponse.success("Logged out from all devices", null));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal Long userId) {
    UserResponse response = authService.getMe(userId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/change-password")
  public ResponseEntity<ApiResponse<Void>> changePassword(
      @AuthenticationPrincipal Long userId,
      @Valid @RequestBody ChangePasswordRequest request,
      HttpServletResponse httpResponse) {
    authService.changePassword(userId, request);
    clearCookie(httpResponse, "accessToken");
    clearCookie(httpResponse, "refreshToken");
    return ResponseEntity.ok(
        ApiResponse.success("Password changed successfully. Please log in again.", null));
  }

  private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setSecure(secureCookie);
    cookie.setPath("/");
    cookie.setMaxAge(maxAge);
    cookie.setAttribute("SameSite", "Lax");
    response.addCookie(cookie);
  }

  private void clearCookie(HttpServletResponse response, String name) {
    Cookie cookie = new Cookie(name, null);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  private String getCookie(HttpServletRequest request, String name) {
    if (request.getCookies() != null) {
      return Arrays.stream(request.getCookies())
          .filter(cookie -> name.equals(cookie.getName()))
          .map(Cookie::getValue)
          .findFirst()
          .orElse(null);
    }
    return null;
  }
}
