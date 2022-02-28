package hello.springboot.springrestapi.accounts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {


    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername() {
        // Given
        String email = "spring@boot.com";
        String password = "pass";
        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(account); //테스트도 repository로 저장하는게 아니라 service를 통해 저장

        // When
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();

    }

    @Test
    public void findByUsernameFail() {
        //username을 불러오려다 실패해서 예외가 발생하는 테스트를 추가해야 해
        String username = "random@name.com";

        try {
            accountService.loadUserByUsername(username);
            fail("supposed to be failed"); //여기 오면 테스트 실패하게 만드는거야.
        } catch (UsernameNotFoundException e) {
            assertThat(e instanceof UsernameNotFoundException).isTrue(); //이 타입으로 받았으니 당연히 맞고,
            assertThat(e.getMessage()).containsSequence(username);
            //에러객체를 받아오기 때문에 더 많은 것을 확인할 수 있음. 단, 코드가 장황해짐.
        }
    }
}