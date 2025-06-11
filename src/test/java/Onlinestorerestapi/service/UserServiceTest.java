package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.user.UserCreateDTO;
import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.exception.ApiException;
import Onlinestorerestapi.mapper.UserMapper;
import Onlinestorerestapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserMapper userMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    AuthService authService;

    @InjectMocks
    UserService userService;

    @Test
    void getUserResponseDTO_whenValidRequest_returnsUserResponseDTO() {
        // given
        User user = new User();
        String userResponseDTOName = "name 1";
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setName(userResponseDTOName);

        // when
        when(authService.getCurrentUser()).thenReturn(user);
        when(userMapper.userToUserResponseDTO(user)).thenReturn(userResponseDTO);

        // then
        UserResponseDTO userResponseDTOToReturn = userService.getUserResponseDTO();
        assertEquals(userResponseDTOName, userResponseDTOToReturn.getName());
    }

    @Test
    void createUser_whenEmailAlreadyExists_throwsApiException() {
        // given
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("email1@email.com");

        // when
        when(userRepository.existsByEmail(userCreateDTO.getEmail())).thenReturn(true);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> userService.createUser(userCreateDTO));
        assertEquals("Email address already in use", apiException.getMessage());
    }

    @Test
    void createUser_whenTelephoneNumberExists_throwsApiException() {
        // given
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("email1@email.com");
        userCreateDTO.setTelephoneNumber("123456789");

        // when
        when(userRepository.existsByEmail(userCreateDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByTelephoneNumber(userCreateDTO.getTelephoneNumber())).thenReturn(true);

        // then
        ApiException apiException = assertThrows(ApiException.class, () -> userService.createUser(userCreateDTO));
        assertEquals("Telephone number already in use", apiException.getMessage());
    }

    @Test
    void createUser_whenValidRequest_createsUser() {
        // given
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setEmail("email1@email.com");
        userCreateDTO.setTelephoneNumber("123456789");
        User user = new User();
        int userId = 1;
        user.setId(userId);

        // when
        when(userRepository.existsByEmail(userCreateDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByTelephoneNumber(userCreateDTO.getTelephoneNumber())).thenReturn(false);
        when(userMapper.userCreateDTOToUserMapper(userCreateDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        // then
        userService.createUser(userCreateDTO);
        verify(userRepository).save(user);
    }
}
