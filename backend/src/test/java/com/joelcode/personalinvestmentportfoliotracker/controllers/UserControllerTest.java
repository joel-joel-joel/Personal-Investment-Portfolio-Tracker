package com.joelcode.personalinvestmentportfoliotracker.controllers;

import com.joelcode.personalinvestmentportfoliotracker.controllers.entitycontrollers.UserController;
import com.joelcode.personalinvestmentportfoliotracker.dto.account.AccountDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.user.UserDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.user.UserUpdateRequest;
import com.joelcode.personalinvestmentportfoliotracker.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    // Test controller can retrieve all users
    @Test
    void testGetAllUsers_Success() {
        // Setup an array of users
        List<UserDTO> users = new ArrayList<>();
        users.add(new UserDTO(UUID.randomUUID(), "john_doe", "joel", "john@example.com", LocalDateTime.now()));
        users.add(new UserDTO(UUID.randomUUID(), "jane_doe", "joel","jane@example.com", LocalDateTime.now()));

        // When service calls method, return the test array
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Assert testing variables are correct
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    // Test for null case
    @Test
    void testGetAllUsers_Empty() {
        // Initialize setup for testing
        when(userService.getAllUsers()).thenReturn(new ArrayList<>());

        // Run method
        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        // Assert testing variables are correct
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    // Test retrieving by userId
    @Test
    void testGetUserById_Success() {
        // Initialize setup for testing
        UUID userId = UUID.randomUUID();
        UserDTO user = new UserDTO(userId, "john_doe", "joel","john@example.com", LocalDateTime.now());
        when(userService.getUserById(userId)).thenReturn(user);

        // Run method
        ResponseEntity<UserDTO> response = userController.getUserById(userId);

        // Assert testing variables are correct
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("john_doe", response.getBody().getUsername());
        verify(userService, times(1)).getUserById(userId);
    }

    // Test null case
    @Test
    void testGetUserById_NotFound() {
        // Initialize setup for testing
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(userId)).thenReturn(null);

        // Run method
        ResponseEntity<UserDTO> response = userController.getUserById(userId);

        // Assert testing variables are correct
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).getUserById(userId);
    }

    // Test for updating user details
    @Test
    void testUpdateUser_Success() {
        // Initialize setup for testing
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest( "john_updated@example.com", "john_doe_updated","12345");
        UserDTO updated = new UserDTO(userId, "john_doe_updated", "joel", "john_updated@example.com", LocalDateTime.now());
        when(userService.updateUser(userId, request)).thenReturn(updated);

        // Run method
        ResponseEntity<UserDTO> response = userController.updateUser(userId, request);

        // Assert testing variables are correct
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("john_doe_updated", response.getBody().getUsername());
        verify(userService, times(1)).updateUser(userId, request);
    }

    @Test
    void testUpdateUser_NotFound() {
        // Initialize setup for testing
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest( "john_updated@example.com", "john_doe_updated","12345");
        when(userService.updateUser(userId, request)).thenReturn(null);

        // Run method
        ResponseEntity<UserDTO> response = userController.updateUser(userId, request);

        // Assert testing variables are correct
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, times(1)).updateUser(userId, request);
    }

    @Test
    void testDeleteUser_Success() {
        // Initialize setup for testing
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).deleteUser(userId);

        // Run method
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Assert testing variables are correct
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void testGetUserAccounts_Success() {
        // Initialize setup for testing
        UUID userId = UUID.randomUUID();
        List<AccountDTO> accounts = new ArrayList<>();
        when(userService.getAllAccountsForUser(userId)).thenReturn(accounts);

        // Run method
        ResponseEntity<?> response = userController.getUserAccounts(userId);

        // Assert testing variables are correct
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).getAllAccountsForUser(userId);
    }

    @Test
    void testGetUserAccounts_Empty() {
        // Initialize setup for testing
        UUID userId = UUID.randomUUID();
        when(userService.getAllAccountsForUser(userId)).thenReturn(new ArrayList<>());

        // Run method
        ResponseEntity<?> response = userController.getUserAccounts(userId);

        // Assert testing variables are correct
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).getAllAccountsForUser(userId);
    }
}