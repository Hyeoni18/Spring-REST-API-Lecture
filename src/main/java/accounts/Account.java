package accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Integer id;

    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER) //여러 개의 enum을 가질 수 있으니까. 그리고 기본 role은 LAZY인데 가져올 롤이 적고 매번 가져와야 하니까 EAGER로 변경.
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
    
    //그리고 event에서 단방향으로 연결
}
