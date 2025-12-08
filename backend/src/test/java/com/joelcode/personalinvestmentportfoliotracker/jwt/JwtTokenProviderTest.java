package com.joelcode.personalinvestmentportfoliotracker.jwt;

import com.joelcode.personalinvestmentportfoliotracker.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private User testUser;
    private String validSecret = "this-is-a-super-secret-key-that-is-at-least-256-bits-long-for-testing-purposes-only-please-use-a-strong-secret";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", validSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 3600000); // 1 hour

        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedpassword123");
        testUser.setFullName("Test User");
        testUser.setRoles(User.Role.ROLE_USER);
    }

    @Test
    void testGenerateToken_Success() {
        // Act
        String token = jwtTokenProvider.generateToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void testGenerateToken_ContainsCorrectFormat() {
        // Act
        String token = jwtTokenProvider.generateToken(testUser);

        // Assert
        // JWT format: header.payload.signature
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "Token should have 3 parts (header.payload.signature)");
    }

    @Test
    void testGenerateToken_DifferentTokensForSameUser() throws InterruptedException {
        // Act
        String token1 = jwtTokenProvider.generateToken(testUser);

        Thread.sleep(2000); // 2000 ms = 2 seconds

        String token2 = jwtTokenProvider.generateToken(testUser);

        // Assert
        assertNotEquals(token1, token2, "Tokens should be different due to different issuedAt times");
    }

    @Test
    void testGetClaims_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        Claims claims = jwtTokenProvider.getClaims(token);

        // Assert
        assertNotNull(claims);
        assertEquals(testUser.getUserId().toString(), claims.getSubject());
        assertEquals(testUser.getEmail(), claims.get("email", String.class));
        assertEquals(testUser.getUsername(), claims.get("username", String.class));
    }

    @Test
    void testGetClaims_ContainsAllUserInfo() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        Claims claims = jwtTokenProvider.getClaims(token);

        // Assert
        assertEquals(testUser.getEmail(), claims.get("email", String.class));
        // REMOVED: password is no longer stored in JWT for security reasons
        assertEquals(testUser.getUsername(), claims.get("username", String.class));
        assertEquals(testUser.getRoles().name(), claims.get("roles", String.class));
        assertEquals(testUser.getFullName(), claims.get("fullName", String.class));
    }

    @Test
    void testGetClaims_InvalidToken_ThrowsException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(MalformedJwtException.class, () -> {
            jwtTokenProvider.getClaims(invalidToken);
        });
    }

    @Test
    void testGetUserIdFromToken_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);

        // Assert
        assertNotNull(userId);
        assertEquals(testUser.getUserId(), userId);
    }

    @Test
    void testGetUserIdFromToken_CorrectType() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        UUID userId = jwtTokenProvider.getUserIdFromToken(token);

        // Assert
        assertInstanceOf(UUID.class, userId);
    }

    @Test
    void testGetUsernameFromToken_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Assert
        assertNotNull(username);
        assertEquals(testUser.getUsername(), username);
    }

    @Test
    void testGetUsernameFromToken_CorrectValue() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void testValidateToken_ValidToken_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken_Failure() {
        // Arrange
        String invalidToken = "invalid.token.here";

        assertThrows(MalformedJwtException.class, () -> {
            jwtTokenProvider.validateToken(invalidToken);
        });
    }

    @Test
    void testValidateToken_MalformedToken_Failure() {
        // Arrange
        String malformedToken = "this-is-not-a-valid-jwt";

        assertThrows(MalformedJwtException.class, () -> {
            jwtTokenProvider.validateToken(malformedToken);
        });
    }

    @Test
    void testValidateToken_EmptyToken_Failure() {
        // Arrange
        String emptyToken = "";

        // Act
        assertThrows(Exception.class, () -> {
            jwtTokenProvider.validateToken(emptyToken);
        });
    }

    @Test
    void testValidateToken_NullToken_Failure() {
        // Arrange
        String nullToken = null;

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtTokenProvider.validateToken(nullToken);
        });
    }

    @Test
    void testValidateToken_ExpiredToken_Failure() {
        // Arrange
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", validSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", -1000); // Already expired

        String expiredToken = jwtTokenProvider.generateToken(testUser);

        // Assert
        assertThrows(ExpiredJwtException.class, () -> {
            jwtTokenProvider.validateToken(expiredToken);
        });
    }

    @Test
    void testIsTokenExpired_NotExpired_Success() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        boolean isExpired = jwtTokenProvider.isTokenExpired(token);  // UPDATED: isTokenExpire -> isTokenExpired

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void testIsTokenExpired_ExpiredToken_Success() {
        // Arrange
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", validSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", -1000); // Already expired

        String expiredToken = jwtTokenProvider.generateToken(testUser);

        // Act
        boolean isExpired = jwtTokenProvider.isTokenExpired(expiredToken);  // UPDATED: isTokenExpire -> isTokenExpired

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void testIsTokenExpired_InvalidToken_ReturnsTrue() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isExpired = jwtTokenProvider.isTokenExpired(invalidToken);  // UPDATED: isTokenExpire -> isTokenExpired

        // Assert
        assertTrue(isExpired, "Invalid token should be treated as expired");
    }

    @Test
    void testIsTokenExpired_EmptyToken_ReturnsTrue() {
        // Arrange
        String emptyToken = "";

        // Act
        boolean isExpired = jwtTokenProvider.isTokenExpired(emptyToken);  // UPDATED: isTokenExpire -> isTokenExpired

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void testGetClaims_ExpirationDateIsSet() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        Claims claims = jwtTokenProvider.getClaims(token);

        // Assert
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date(System.currentTimeMillis())));
    }

    @Test
    void testGetClaims_IssuedAtIsSet() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        Claims claims = jwtTokenProvider.getClaims(token);

        // Assert
        assertNotNull(claims.getIssuedAt());
        assertTrue(claims.getIssuedAt().before(new Date(System.currentTimeMillis() + 1000)));
    }

    @Test
    void testGenerateToken_MultipleUsers() {
        // Arrange
        User user1 = new User();
        user1.setUserId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("pass1");
        user1.setFullName("User One");
        user1.setRoles(User.Role.ROLE_USER);

        User user2 = new User();
        user2.setUserId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("pass2");
        user2.setFullName("User Two");
        user2.setRoles(User.Role.ROLE_USER);

        // Act
        String token1 = jwtTokenProvider.generateToken(user1);
        String token2 = jwtTokenProvider.generateToken(user2);

        UUID userId1 = jwtTokenProvider.getUserIdFromToken(token1);
        UUID userId2 = jwtTokenProvider.getUserIdFromToken(token2);

        // Assert
        assertNotEquals(token1, token2);
        assertEquals(user1.getUserId(), userId1);
        assertEquals(user2.getUserId(), userId2);
    }

    @Test
    void testValidateToken_WrongSecret_Failure() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Create new provider with different secret
        JwtTokenProvider wrongSecretProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(wrongSecretProvider, "jwtSecret", "different-secret-key-for-testing-purposes-only");
        ReflectionTestUtils.setField(wrongSecretProvider, "jwtExpirationInMs", 3600000);

        // Act
        assertThrows(Exception.class, () -> {
            wrongSecretProvider.validateToken(token);
        });
    }

    @Test
    void testTokenContainsAllRequiredClaims() {
        // Arrange
        String token = jwtTokenProvider.generateToken(testUser);

        // Act
        Claims claims = jwtTokenProvider.getClaims(token);

        // Assert
        assertNotNull(claims.getSubject(), "Subject should not be null");
        assertNotNull(claims.get("email"), "Email claim should not be null");
        assertNotNull(claims.get("username"), "Username claim should not be null");
        assertNotNull(claims.get("roles"), "Roles claim should not be null");
        assertNotNull(claims.get("fullName"), "Full name claim should not be null");
        // REMOVED: Password claim test - password is no longer stored in JWT
    }
}