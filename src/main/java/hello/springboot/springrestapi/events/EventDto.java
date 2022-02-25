package hello.springboot.springrestapi.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventDto {

    //이만큼만 입력을 받을 수 있다. 명시 하는거임.
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
}
