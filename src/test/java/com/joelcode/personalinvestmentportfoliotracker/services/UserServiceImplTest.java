package com.joelcode.personalinvestmentportfoliotracker.services;

import com.joelcode.personalinvestmentportfoliotracker.dto.account.AccountDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.user.UserCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.user.UserDTO;
import com.joelcode.personalinvestmentportfoliotracker.dto.user.UserUpdateRequest;
import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.entities.User;
import com.joelcode.personalinvestmentportfoliotracker.repositories.UserRepository;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.AccountMapper;
import com.joelcode.personalinvestmentportfoliotracker.services.mapping.UserMapper;
import com.joelcode.personalinvestmentportfoliotracker.services.user.UserServiceImpl;
import com.joelcode.personalinvestmentportfoliotracker.services.user.UserValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Testing User service layer business logic
public class UserServiceImplTest {

    // Define mock key fields
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID userId;

    private Account testAccount;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();

        testAccount = new Account();
        testAccount.setAccountId(UUID.randomUUID());

        testUser = new User();
        testUser.setUserId(userId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setAccounts(new ArrayList<>()); // Avoid NPE
        testUser.getAccounts().add(testAccount);
    }

    // Test user creation
    @Test
    void testCreateUser_Success() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");

        doNothing().when(userValidationService).validateEmailUnique(request.getEmail());
        doNothing().when(userValidationService).validateUsernameUnique(request.getUsername());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        try (MockedStatic<UserMapper> mapperMock = Mockito.mockStatic(UserMapper.class)) {
            mapperMock.when(() -> UserMapper.toEntity(request)).thenReturn(testUser);
            mapperMock.when(() -> UserMapper.toDTO(testUser)).thenReturn(new UserDTO());

            UserDTO result = userService.createUser(request);

            assertNotNull(result);
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    // Test get user by ID
    @Test
    void testGetUserById_ReturnsCorrectDTO() {
        when(userValidationService.validateUserExists(userId)).thenReturn(testUser);

        try (MockedStatic<UserMapper> mapperMock = Mockito.mockStatic(UserMapper.class)) {
            mapperMock.when(() -> UserMapper.toDTO(testUser)).thenReturn(new UserDTO());

            UserDTO result = userService.getUserById(userId);
            assertNotNull(result);
        }
    }

    // Test get all users
    @Test
    void testGetAllUsers_ReturnsCorrectList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        try (MockedStatic<UserMapper> mapperMock = Mockito.mockStatic(UserMapper.class)) {
            mapperMock.when(() -> UserMapper.toDTO(testUser)).thenReturn(new UserDTO());

            List<UserDTO> result = userService.getAllUsers();
            assertEquals(1, result.size());
        }
    }

    // Test update user
    @Test
    void testUpdateUser_Success() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("updatedUser");

        when(userValidationService.validateUserExists(userId)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        try (MockedStatic<UserMapper> mapperMock = Mockito.mockStatic(UserMapper.class)) {
            mapperMock.when(() -> UserMapper.updateEntity(testUser, request)).thenAnswer(invocation -> null);
            mapperMock.when(() -> UserMapper.toDTO(testUser)).thenReturn(new UserDTO());

            UserDTO result = userService.updateUser(userId, request);

            assertNotNull(result);
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    // Test delete user
    @Test
    void testDeleteUser_Success() {
        when(userValidationService.validateUserExists(userId)).thenReturn(testUser);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).delete(testUser);
    }

    // Test get all accounts for user
    @Test
    void testGetAllAccountsForUser_ReturnsCorrectList() {
        when(userValidationService.validateUserExists(userId)).thenReturn(testUser);

        try (MockedStatic<AccountMapper> mapperMock = Mockito.mockStatic(AccountMapper.class)) {
            mapperMock.when(() -> AccountMapper.toDTO(testAccount)).thenReturn(new AccountDTO());

            List<AccountDTO> result = userService.getAllAccountsForUser(userId);

            assertEquals(1, result.size());
        }
    }
}
