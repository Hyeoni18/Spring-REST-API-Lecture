package hello.springboot.springrestapi.events;

import lombok.*;

import javax.persistence.*;
import java.security.cert.CertPathBuilder;
import java.time.LocalDateTime;

@Builder @NoArgsConstructor @AllArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;

    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime; //시작일시
    private LocalDateTime closeEnrollmentDateTime; //종료일시
    private LocalDateTime beginEventDateTime; //이벤트 시작일시
    private LocalDateTime endEventDateTime; //이벤트 종료일시
    private String location; // 위치 (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;

    private boolean offline; //온,오프라인 여부
    private boolean free; //모임의 유,무료 여부

    @Enumerated(EnumType.STRING) //기본값 ORDINAL인데 STRING으로 바꿔주는게 좋음. ORDINAL은 Enum 순서에 따라서 0,1,2로 숫자 값이 저장됨. 근데 나중에 순서가 바뀌면 데이터가 꼬일 수 있음.
    private EventStatus eventStatus = EventStatus.DRAFT; //이벤트 상태

    public void update() {
        //update free
        if(this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        } else {
            this.free = false;
        }
        //update offline
        if(this.location == null || this.location.isBlank()) {
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}
