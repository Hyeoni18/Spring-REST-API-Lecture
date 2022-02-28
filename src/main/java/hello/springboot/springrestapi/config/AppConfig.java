package hello.springboot.springrestapi.config;

import hello.springboot.springrestapi.accounts.Account;
import hello.springboot.springrestapi.accounts.AccountRole;
import hello.springboot.springrestapi.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.AccessType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        //PasswordEncoder는 약간 특이함. 시큐리티 최신버전에 들어간건데, PasswordEncoderFactories.class를 보면 패스워드 앞에 profix를 달아줌. 다양한 인코딩 타입을 지원하는 인코더인데 인코딩된 패스워드 앞에 prefix를 붙여줌. 어떤 방법으로 인코딩 된건지 알 수 있도록.
    }

    //애플리케이션 시작할 때 임의로 계정을 생성하여 사용
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account account = Account.builder()
                        .email("spring@boot.com")
                        .password("pass")
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                accountService.saveAccount(account);
            }
        };
    }
}
