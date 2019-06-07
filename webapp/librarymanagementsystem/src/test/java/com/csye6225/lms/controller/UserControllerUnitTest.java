package com.csye6225.lms.controller;

import com.csye6225.lms.dao.UserRepository;
import com.csye6225.lms.pojo.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc //need this in Spring Boot test
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;
    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        //User does not exist in database
        User userDetails = new User();
        userDetails.setEmail("honraoa@yahoo.com");
        userDetails.setPassword("Abcd@123");
        //when(userRepository.findByEmail(userDetails.getEmail())).thenReturn(null);
        mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).param("email","honraoa@yahoo.com").param("password","Abcd@123"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterFail() throws Exception {
        User userDetails = new User();
        userDetails.setEmail("honraoa@yahoo.com");
        userDetails.setPassword("Abcd@123");
        //User already exists in database
        when(userRepository.findByEmail(userDetails.getEmail())).thenReturn(userDetails);
        mockMvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).param("email","honraoa@yahoo.com").param("password","Abcd@123"))
                .andExpect(status().isConflict());
    }
}
