package hello.springboot.springrestapi.accounts;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdapter extends User {

    private Account account;

    public AccountAdapter(Account account) {
        super(account.getEmail(), account.getPassword(), authrities(account.getRoles()));
        this.account = account;
    }

    //지난번 AccountService에서 생성했던 거와 같은 거임.
    private static Collection<? extends GrantedAuthority> authrities(Set<AccountRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet()); //role을 GrantedAuthority로 변경
    }

    public Account getAccount() {
        return account;
    }
}
