package com.bogecom.auth.service;

import com.bogecom.auth.dto.AuthResponse;
import com.bogecom.auth.dto.ChangePasswordRequest;
import com.bogecom.auth.dto.LoginRequest;
import com.bogecom.auth.dto.RegisterRequest;
import com.bogecom.auth.entity.RefreshToken;
import com.bogecom.auth.repository.RefreshTokenRepository;
import com.bogecom.exception.BusinessException;
import com.bogecom.security.JwtTokenProvider;
import com.bogecom.user.dto.UserResponse;
import com.bogecom.user.entity.User;
import com.bogecom.user.mapper.UserMapper;
import com.bogecom.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserMapper userMapper;

  @Value("${app.security.jwt.refresh-token-expiration-ms}")
  private long refreshTokenDurationMs;

  @Transactional
  public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
    if (userRepository.existsByEmailAndIsDeletedFalse(request.email())) {
      throw new BusinessException("Email already in use", HttpStatus.CONFLICT);
    }

    User user =
        User.builder()
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .role(request.role())
            .build();

    user = userRepository.save(user);
    log.info("New user registered: {}", user.getEmail());

    return new AuthResponse(userMapper.toResponse(user), "User registered successfully");
  }

  @Transactional
  public String[] login(LoginRequest request, HttpServletRequest httpRequest) {
    User user =
        userRepository
            .findByEmailAndIsDeletedFalse(request.email())
            .orElseThrow(
                () -> new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      log.warn("Failed login attempt for user: {}", user.getEmail());
      throw new BusinessException("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    log.info("User logged in successfully: {}", user.getEmail());
    return issueTokens(user, httpRequest);
  }

  @Transactional
  public String[] refresh(String refreshTokenCookie, HttpServletRequest httpRequest) {
    if (refreshTokenCookie == null || !jwtTokenProvider.validateToken(refreshTokenCookie)) {
      throw new BusinessException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
    }

    String tokenId = jwtTokenProvider.getTokenIdFromJWT(refreshTokenCookie);
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByTokenIdAndIsDeletedFalseAndRevokedFalse(tokenId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "Refresh token not found or revoked", HttpStatus.UNAUTHORIZED));

    if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new BusinessException("Refresh token expired", HttpStatus.UNAUTHORIZED);
    }

    User user = refreshToken.getUser();

    // Revoke the old token (Refresh token rotation)
    refreshToken.setRevoked(true);
    refreshTokenRepository.save(refreshToken);

    return issueTokens(user, httpRequest);
  }

  @Transactional
  public void logout(String refreshTokenCookie) {
    if (refreshTokenCookie != null && jwtTokenProvider.validateToken(refreshTokenCookie)) {
      String tokenId = jwtTokenProvider.getTokenIdFromJWT(refreshTokenCookie);
      refreshTokenRepository
          .findByTokenIdAndIsDeletedFalseAndRevokedFalse(tokenId)
          .ifPresent(
              token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
                log.info("Session logged out for device: {}", token.getDeviceName());
              });
    }
  }

  @Transactional
  public void logoutAll(Long userId) {
    refreshTokenRepository.revokeAllUserTokens(userId);
    log.info("All sessions revoked for user ID: {}", userId);
  }

  @Transactional(readOnly = true)
  public UserResponse getMe(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
    return userMapper.toResponse(user);
  }

  @Transactional
  public void changePassword(Long userId, ChangePasswordRequest request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

    if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
      throw new BusinessException("Invalid current password", HttpStatus.BAD_REQUEST);
    }

    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    userRepository.save(user);

    // Revoke all sessions to force re-login on devices
    logoutAll(userId);
    log.info("Password changed for user ID: {}", userId);
  }

  private String[] issueTokens(User user, HttpServletRequest request) {
    String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole().name());

    String tokenId = UUID.randomUUID().toString();
    String refreshTokenString = jwtTokenProvider.generateRefreshToken(tokenId);

    RefreshToken refreshToken =
        RefreshToken.builder()
            .tokenId(tokenId)
            .user(user)
            .tokenHash(
                passwordEncoder.encode(
                    refreshTokenString)) // In a real scenario we'd hash the raw string
            .deviceName(request.getHeader("User-Agent")) // Simplified extraction
            .ipAddress(request.getRemoteAddr())
            .userAgent(request.getHeader("User-Agent"))
            .expiresAt(LocalDateTime.now().plusNanos(refreshTokenDurationMs * 1_000_000))
            .build();

    refreshTokenRepository.save(refreshToken);

    return new String[] {accessToken, refreshTokenString};
  }
}
