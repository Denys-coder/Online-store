package Onlinestorerestapi.service;

import Onlinestorerestapi.dto.user.UserResponseDTO;
import Onlinestorerestapi.entity.User;
import Onlinestorerestapi.mapper.UserMapper;
import Onlinestorerestapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
