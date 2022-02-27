package hello.springboot.springrestapi.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
                //만약 user에 해당하는 account 객체가 없으면, 에러를 던짐. orElseThrow.
        return new User(account.getEmail(), account.getPassword(), getAuthorities(account.getRoles()));
    } //전반적으로 보면 우리가 사용하는 도메인을 스프링 시큐리티가 정의해놓은 인터페이스로 변환하는 작업.

    private Collection<? extends GrantedAuthority> getAuthorities(Set<AccountRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet()); //role을 GrantedAuthority로 변경
    }

//    private Collection<? extends GrantedAuthority> getAuthorities(Set<AccountRole> roles) {
//        return roles.stream().map(r -> {
//            return new SimpleGrantedAuthority("ROLE_" + r.name());
//        }).collect(Collectors.toSet());
//    }
}
