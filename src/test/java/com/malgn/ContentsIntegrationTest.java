package com.malgn;
import com.malgn.configure.auth.dto.LoginRequest;
import com.malgn.configure.contents.dto.ContentsCreateRequest;
import com.malgn.configure.contents.dto.ContentsUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContentsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    /**
     * 각 테스트 전에 admin, user1 로그인하여 토큰을 미리 발급받습니다.
     * (h2-data.sql의 초기 데이터 사용)
     */
    @BeforeEach
    void setUp() throws Exception {
        adminToken = login("admin", "admin1234");
        userToken = login("user1", "user1234");
    }

    // ──────────────────────────────────────────────
    // 로그인
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("로그인 성공 시 accessToken과 refreshToken을 반환한다")
    void loginSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin", "admin1234"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인하면 실패한다")
    void loginFail_wrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin", "wrongpassword"))))
                .andExpect(status().isUnauthorized());
    }

    // ──────────────────────────────────────────────
    // 콘텐츠 목록 조회
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("콘텐츠 목록을 페이징하여 조회할 수 있다")
    void getContentsList() throws Exception {
        mockMvc.perform(get("/api/contents")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("인증 없이 콘텐츠 목록 조회 시 401을 반환한다")
    void getContentsList_unauthorized() throws Exception {
        mockMvc.perform(get("/api/contents"))
                .andExpect(status().isUnauthorized());
    }

    // ──────────────────────────────────────────────
    // 콘텐츠 상세 조회
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("콘텐츠 상세 조회 시 조회수가 1 증가한다")
    void getContentsDetail_viewCountIncrement() throws Exception {
        // 첫 번째 조회
        mockMvc.perform(get("/api/contents/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.viewCount").value(1));

        // 두 번째 조회
        mockMvc.perform(get("/api/contents/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.viewCount").value(2));
    }

    @Test
    @DisplayName("존재하지 않는 콘텐츠 조회 시 404를 반환한다")
    void getContentsDetail_notFound() throws Exception {
        mockMvc.perform(get("/api/contents/999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ──────────────────────────────────────────────
    // 콘텐츠 생성
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("인증된 사용자는 콘텐츠를 생성할 수 있다")
    void createContents() throws Exception {
        ContentsCreateRequest request = new ContentsCreateRequest("테스트 제목", "테스트 내용");

        mockMvc.perform(post("/api/contents")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    @DisplayName("제목 없이 콘텐츠 생성 시 400을 반환한다")
    void createContents_blankTitle() throws Exception {
        ContentsCreateRequest request = new ContentsCreateRequest("", "내용");

        mockMvc.perform(post("/api/contents")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ──────────────────────────────────────────────
    // 콘텐츠 수정
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("ADMIN은 다른 사람의 콘텐츠도 수정할 수 있다")
    void updateContents_adminCanUpdateAny() throws Exception {
        // 초기 데이터 id=3번은 user1이 작성한 콘텐츠
        ContentsUpdateRequest request = new ContentsUpdateRequest("관리자가 수정", "수정된 내용");

        mockMvc.perform(patch("/api/contents/3")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("관리자가 수정"));
    }

    @Test
    @DisplayName("USER는 본인 콘텐츠만 수정할 수 있다")
    void updateContents_userCannotUpdateOthers() throws Exception {
        // id=1번은 admin이 작성한 콘텐츠
        ContentsUpdateRequest request = new ContentsUpdateRequest("무단 수정", "무단 내용");

        mockMvc.perform(patch("/api/contents/1")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ──────────────────────────────────────────────
    // 콘텐츠 삭제
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("작성자는 본인 콘텐츠를 삭제할 수 있다")
    void deleteContents_ownerCanDelete() throws Exception {
        // id=3번은 user1이 작성한 콘텐츠
        mockMvc.perform(delete("/api/contents/3")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("삭제된 콘텐츠를 다시 삭제하면 400을 반환한다")
    void deleteContents_alreadyDeleted() throws Exception {
        // 먼저 삭제
        mockMvc.perform(delete("/api/contents/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // 다시 삭제 시도
        mockMvc.perform(delete("/api/contents/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ──────────────────────────────────────────────
    // 헬퍼 메서드
    // ──────────────────────────────────────────────

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(username, password))))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("data").get("accessToken").asText();
    }
}
