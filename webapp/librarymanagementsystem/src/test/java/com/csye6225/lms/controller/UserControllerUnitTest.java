package com.csye6225.lms.controller;

import com.csye6225.lms.dao.UserRepository;
import com.csye6225.lms.pojo.User;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private UserController userController;
    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterPath() throws Exception {


        // simulate the form submit (POST)
        User userDetails = new User();
        userDetails.setEmail("honraoa@yahoo.com");
        userDetails.setPassword("Abcd@123");
        when(userRepository.saveAndFlush(userDetails)).thenReturn(userDetails);
        mockMvc.perform(post("/user/register", userDetails))
                .andExpect(status().isOk());
    }
}
