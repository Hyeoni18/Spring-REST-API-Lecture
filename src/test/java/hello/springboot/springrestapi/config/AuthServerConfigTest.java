package hello.springboot.springrestapi.config;

import hello.springboot.springrestapi.accounts.Account;
import hello.springboot.springrestapi.accounts.AccountRole;
import hello.springboot.springrestapi.accounts.AccountService;
import hello.springboot.springrestapi.common.BaseControllerTest;
import hello.springboot.springrestapi.common.TestDescription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 받는 테스트")
    public void getAuthToken() throws Exception {
        //Given
        String username = "spring@boot.com";
        String password = "pass";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(account);

        String clientId = "myApp";
        String clientSecret = "pass";

        this.mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(clientId, clientSecret)) //httpBasic은 의존성 추가 필요
                        .param("username", username) //grant Type, username, password. 문서에서 봤던 내용.
                        .param("password", password)
                        .param("grant_type","password") //패스워드 인증 타입 사용할거야.
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
        ;
    }
}