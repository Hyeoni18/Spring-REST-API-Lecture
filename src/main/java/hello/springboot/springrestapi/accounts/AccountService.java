package hello.springboot.springrestapi.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Account saveAccount(Account account) {
        //인코더를 사용해서 패스워드를 저장.
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        return this.accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new AccountAdapter(account);
//        return new User(account.getEmail(), account.getPassword(), getAuthorities(account.getRoles()));
    }

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
