package com.apettigrew.user.controllers;

import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.dtos.UserRegisterDto;
import com.apettigrew.user.exceptions.UserAlreadyExistsException;
import com.apettigrew.user.jsonapi.SingleResourceResponse;
import com.apettigrew.user.jsonapi.requests.CreateRequest;
import com.apettigrew.user.jsonapi.requests.UserCreateRequest;
import com.apettigrew.user.jsonapi.resources.UserResource;
import com.apettigrew.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Registration Tests")
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private UserRegisterDto userRegisterDto;
    private UserDto userDto;
    private CreateRequest<UserCreateRequest> createRequest;
    private UserCreateRequest userCreateRequest;

    @BeforeEach
    void setUp() throws Exception {
        // Setup UserRegisterDto
        userRegisterDto = new UserRegisterDto();
        userRegisterDto.setEmail("test@example.com");
        userRegisterDto.setFirstName("John");
        userRegisterDto.setLastName("Doe");
        userRegisterDto.setPassword("password123");

        // Setup UserDto (returned from service)
        userDto = new UserDto();
        userDto.setId("user-123");
        userDto.setEmail("test@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");

        // Setup UserCreateRequest
        userCreateRequest = new UserCreateRequest();
        userCreateRequest.setAttributes(userRegisterDto);

        // Setup CreateRequest using reflection to set package-private field
        createRequest = new CreateRequest<>();
        setFieldValue(createRequest, "data", userCreateRequest);
    }

    /**
     * Helper method to set field values using reflection
     */
    private void setFieldValue(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    @DisplayName("Should successfully register a new user and return CREATED status")
    void testCreateUser_Success() {
        // Given
        when(userService.createUser(any(UserRegisterDto.class))).thenReturn(userDto);

        // When
        SingleResourceResponse<UserResource> response = authController.createUser(createRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals("users", response.getData().getType());
        assertEquals("user-123", response.getData().getId());
        assertNotNull(response.getData().getAttributes());
        assertEquals("test@example.com", response.getData().getAttributes().getEmail());
        assertEquals("John", response.getData().getAttributes().getFirstName());
        assertEquals("Doe", response.getData().getAttributes().getLastName());

        // Verify service was called with correct DTO
        verify(userService, times(1)).createUser(any(UserRegisterDto.class));
        verify(userService, times(1)).createUser(argThat(dto ->
                dto.getEmail().equals("test@example.com") &&
                dto.getFirstName().equals("John") &&
                dto.getLastName().equals("Doe") &&
                dto.getPassword().equals("password123")
        ));
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when user already exists")
    void testCreateUser_UserAlreadyExists() {
        // Given
        when(userService.createUser(any(UserRegisterDto.class)))
                .thenThrow(new UserAlreadyExistsException("User with this email already exists"));

        // When & Then
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authController.createUser(createRequest)
        );

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userService, times(1)).createUser(any(UserRegisterDto.class));
    }

    @Test
    @DisplayName("Should correctly convert CreateRequest to UserRegisterDto")
    void testCreateUser_DtoConversion() {
        // Given
        when(userService.createUser(any(UserRegisterDto.class))).thenReturn(userDto);

        // When
        authController.createUser(createRequest);

        // Then
        verify(userService, times(1)).createUser(argThat(dto -> {
            assertNotNull(dto);
            assertEquals("test@example.com", dto.getEmail());
            assertEquals("John", dto.getFirstName());
            assertEquals("Doe", dto.getLastName());
            assertEquals("password123", dto.getPassword());
            return true;
        }));
    }

    @Test
    @DisplayName("Should return response with correct UserResource structure")
    void testCreateUser_ResponseStructure() {
        // Given
        when(userService.createUser(any(UserRegisterDto.class))).thenReturn(userDto);

        // When
        SingleResourceResponse<UserResource> response = authController.createUser(createRequest);

        // Then
        assertNotNull(response);
        UserResource userResource = response.getData();
        assertNotNull(userResource);
        assertEquals("users", userResource.getType());
        assertEquals("user-123", userResource.getId());
        
        UserDto attributes = userResource.getAttributes();
        assertNotNull(attributes);
        assertEquals("test@example.com", attributes.getEmail());
        assertEquals("John", attributes.getFirstName());
        assertEquals("Doe", attributes.getLastName());
    }

    @Test
    @DisplayName("Should handle different user data correctly")
    void testCreateUser_DifferentUserData() throws Exception {
        // Given
        UserRegisterDto differentUserDto = new UserRegisterDto();
        differentUserDto.setEmail("jane@example.com");
        differentUserDto.setFirstName("Jane");
        differentUserDto.setLastName("Smith");
        differentUserDto.setPassword("securePassword456");

        UserDto differentUser = new UserDto();
        differentUser.setId("user-456");
        differentUser.setEmail("jane@example.com");
        differentUser.setFirstName("Jane");
        differentUser.setLastName("Smith");

        UserCreateRequest differentRequest = new UserCreateRequest();
        differentRequest.setAttributes(differentUserDto);
        CreateRequest<UserCreateRequest> differentCreateRequest = new CreateRequest<>();
        setFieldValue(differentCreateRequest, "data", differentRequest);

        when(userService.createUser(any(UserRegisterDto.class))).thenReturn(differentUser);

        // When
        SingleResourceResponse<UserResource> response = authController.createUser(differentCreateRequest);

        // Then
        assertNotNull(response);
        assertEquals("user-456", response.getData().getId());
        assertEquals("jane@example.com", response.getData().getAttributes().getEmail());
        assertEquals("Jane", response.getData().getAttributes().getFirstName());
        assertEquals("Smith", response.getData().getAttributes().getLastName());
        
        verify(userService, times(1)).createUser(argThat(dto ->
                dto.getEmail().equals("jane@example.com") &&
                dto.getFirstName().equals("Jane") &&
                dto.getLastName().equals("Smith")
        ));
    }
}

