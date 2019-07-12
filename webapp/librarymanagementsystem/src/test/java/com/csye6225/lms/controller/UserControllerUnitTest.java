package com.csye6225.lms.controller;
import com.csye6225.lms.dao.UserRepository;
import com.csye6225.lms.pojo.User;
import com.csye6225.lms.service.CustomUserDetailsService;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerUnitTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private Gson gson;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @Test
    public void register_ValidData_ReturnsSuccess() throws Exception {
        //User does not exist in database
        User userDetails = new User();
        userDetails.setEmail("honraoa@yahoo.com");
        userDetails.setPassword("Abcd@123");
        when(userDetailsService.validatePassword("Abcd@123")).thenReturn(true);
        when(bCryptPasswordEncoder.encode("Abcd@123")).thenReturn("EncodedPasswo6rd");
        when(userRepository.findByEmail(userDetails.getEmail())).thenReturn(null);
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(userDetails))).andExpect(status().isCreated());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).saveAndFlush(captor.capture());
        User actual = captor.getValue();
        assertEquals("EncodedPasswo6rd", actual.getPassword());
        assertEquals("honraoa@yahoo.com", actual.getEmail());
        System.out.println("Test passed successfully");
    }

    @Test
    public void register_ExistingUser_ReturnsConflict() throws Exception {
        User userDetails = new User();
        userDetails.setEmail("honraoa@yahoo.com");
        userDetails.setPassword("Abcd@123");
        when(userDetailsService.validatePassword("Abcd@123")).thenReturn(true);
        //User already exists in database
        when(userRepository.findByEmail(userDetails.getEmail())).thenReturn(userDetails);
        mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(userDetails)))
                .andExpect(status().isConflict());
        System.out.println("Test passed successfully");
    }

}
