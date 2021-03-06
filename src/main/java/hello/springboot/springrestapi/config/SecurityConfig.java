package hello.springboot.springrestapi.config;

import hello.springboot.springrestapi.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService; //지난 시간 설명한 UserDetailsService

    @Autowired
    PasswordEncoder passwordEncoder; //방금 만든 PasswordEncoder

    @Bean
    public TokenStore tokenStore() {    // 토큰을 저장하는 곳, OAuth토큰.
        return new InMemoryTokenStore(); // 저장소는 InMemoryTokenStore을 사용
    }

    //AuthenticationManager를 Bean으로 노출해줘야 함. 다른 AuthorizationServer와 ResourceServer가 참조할 수 있도록. AuthenticationManager으로 bean으로 노출하기 위해 오버라이딩 해서 @Bean 어노테이션을 붙여주면 된다. 그러면 빈으로 노출됨.
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //AuthenticationManager를 어떻게 만들것인지 AuthenticationManagerBuilder를 재정의
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
        //이렇게 내가 만든 것으로 사용할 것을 정의.
    }

    //다음은 필터를 적용할지 말지 web에서 걸러낼 수 있음
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().mvcMatchers("/docs/index.html"); //index.html 무시
//        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); //기본 정적 리소스의 위치를 다 가져와서 적용 무시
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .anonymous() //익명사용자 허용
                .and()
                .formLogin() //폼인증을 사용할거야, 로그인 화면은 어디야 이런거.
                .and()
                .authorizeRequests() //내가 허용할 메소드가 있는데
                .mvcMatchers(HttpMethod.GET, "/api/**").authenticated() //get요청으로 api로 시작하는 모든걸 anonymous로 허용
                .anyRequest().authenticated(); //나머지는 인증필요
    }

}
