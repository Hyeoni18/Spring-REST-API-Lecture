package hello.springboot.springrestapi.events;

import lombok.*;

import java.security.cert.CertPathBuilder;
import java.time.LocalDateTime;

@Builder @NoArgsConstructor @AllArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Data
public class Event {

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
    private EventStatus eventStatus = EventStatus.DRAFT; //이벤트 상태

}
