import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.attendancetracking.controller.PtRegistrationController;
import pt.attendancetracking.dto.CreateMemberRequest;
import pt.attendancetracking.dto.MemberResponse;
import pt.attendancetracking.model.PersonalTrainer;
import pt.attendancetracking.model.UserRole;
import pt.attendancetracking.service.MemberService;
import pt.attendancetracking.service.PersonalTrainerService;
import pt.attendancetracking.service.RegistrationLinkService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PtRegistrationController.class)
class PtRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationLinkService registrationLinkService;

    @MockBean
    private MemberService memberService;

    @MockBean
    private PersonalTrainerService personalTrainerService;

    @Autowired
    private ObjectMapper objectMapper;

    private PersonalTrainer samplePt;
    private CreateMemberRequest sampleCreateMemberRequest;
    private MemberResponse sampleMemberResponse;

    @BeforeEach
    void setUp() {
        // Create a PersonalTrainer instance without using builder
        samplePt = new PersonalTrainer();
        samplePt.setUsername("testpt");
        samplePt.setName("Test PT");
        samplePt.setEmail("testpt@example.com");
        samplePt.setRole(UserRole.ROLE_PT);

        // Create a CreateMemberRequest instance
        sampleCreateMemberRequest = new CreateMemberRequest(
                "newmember",
                "password123",
                "New Member",
                "newmember@example.com",
                false,
                null
        );

        // Create a MemberResponse instance
        sampleMemberResponse = new MemberResponse(1L, "newmember", "newmember@example.com", "New Member");
    }

    @Test
    @WithMockUser(username = "testpt", roles = "PT")
    void generateRegistrationLink_Success() throws Exception {
        // Arrange
        String expectedToken = "test-token";
        when(personalTrainerService.getPersonalTrainerByUsername("testpt")).thenReturn(samplePt);
        when(registrationLinkService.generateRegistrationLink(samplePt)).thenReturn(expectedToken);

        // Act & Assert
        mockMvc.perform(post("/api/registration/generate-link")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration link generated successfully"))
                .andExpect(jsonPath("$.registrationLink").value(containsString(expectedToken)))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testpt", roles = "PT")
    void generateRegistrationLink_PTNotFound() throws Exception {
        // Arrange
        when(personalTrainerService.getPersonalTrainerByUsername("testpt"))
                .thenThrow(new RuntimeException("Personal trainer not found"));

        // Act & Assert
        mockMvc.perform(post("/api/registration/generate-link")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Personal trainer not found"))
                .andDo(print());
    }

    @Test
    void registerWithToken_Success() throws Exception {
        // Arrange
        String token = "valid-token";
        when(registrationLinkService.validateAndRegisterMember(token, sampleCreateMemberRequest))
                .thenReturn(sampleMemberResponse);

        // Act & Assert
        mockMvc.perform(post("/api/registration/register/{token}", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCreateMemberRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.memberId").value(1))
                .andDo(print());
    }

    @Test
    void registerWithToken_InvalidToken() throws Exception {
        // Arrange
        String token = "invalid-token";
        when(registrationLinkService.validateAndRegisterMember(token, sampleCreateMemberRequest))
                .thenThrow(new RuntimeException("Invalid or expired registration link"));

        // Act & Assert
        mockMvc.perform(post("/api/registration/register/{token}", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCreateMemberRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid or expired registration link"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "regular_user", roles = "USER")
    void generateRegistrationLink_NonPTUser() throws Exception {
        mockMvc.perform(post("/api/registration/generate-link")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}