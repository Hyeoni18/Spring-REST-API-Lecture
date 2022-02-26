package hello.springboot.springrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.springboot.springrestapi.common.RestDocsConfiguration;
import hello.springboot.springrestapi.common.TestDescription;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("spring")
                .description("rest api dev")
                .beginEnrollmentDateTime(LocalDateTime.of(2022,2,25,12,33))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,2,26,12,33))
                .beginEventDateTime(LocalDateTime.of(2022,2,27,12,33))
                .endEventDateTime(LocalDateTime.of(2022,2,28,12,33))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(new MediaType(MediaTypes.HAL_JSON, StandardCharsets.UTF_8))
                                .content(objectMapper.writer().writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isCreated()) //201
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON+";charset="+StandardCharsets.UTF_8))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event"
                    ,links(
                            linkWithRel("self").description("link to self")
                            ,linkWithRel("query-events").description("link to query events")
                            ,linkWithRel("update-event").description("link to update an existing event")
                            ,linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                                ,headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event")
                                ,fieldWithPath("description").description("description of new event")
                                ,fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event")
                                ,fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event")
                                ,fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event")
                                ,fieldWithPath("endEventDateTime").description("endEventDateTime of new event")
                                ,fieldWithPath("location").description("location of new event")
                                ,fieldWithPath("basePrice").description("basePrice of new event")
                                ,fieldWithPath("maxPrice").description("maxPrice of new event")
                                ,fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header")
                                ,headerWithName(HttpHeaders.CONTENT_TYPE).description("content Type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of new event")
                                ,fieldWithPath("name").description("Name of new event")
                                ,fieldWithPath("description").description("description of new event")
                                ,fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event")
                                ,fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event")
                                ,fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event")
                                ,fieldWithPath("endEventDateTime").description("endEventDateTime of new event")
                                ,fieldWithPath("location").description("location of new event")
                                ,fieldWithPath("basePrice").description("basePrice of new event")
                                ,fieldWithPath("maxPrice").description("maxPrice of new event")
                                ,fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                                ,fieldWithPath("free").description("free of new event")
                                ,fieldWithPath("offline").description("offline of new event")
                                ,fieldWithPath("eventStatus").description("eventStatus of new event")
                                ,fieldWithPath("_links.self.href").description("self")
                                ,fieldWithPath("_links.query-events.href").description("query-events")
                                ,fieldWithPath("_links.update-event.href").description("update-event")
                                ,fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
                ;
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용했을 때 에러 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("spring")
                .description("rest api dev")
                .beginEnrollmentDateTime(LocalDateTime.of(2022,2,25,12,33))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,2,26,12,33))
                .beginEventDateTime(LocalDateTime.of(2022,2,27,12,33))
                .endEventDateTime(LocalDateTime.of(2022,2,28,12,33))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writer().writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력값이 비어있을 때 에러 발생 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못됐을 경우 에러 발생하는 테스트") //주석으로 달아도 괜찮음.
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("rest api dev")
                .beginEnrollmentDateTime(LocalDateTime.of(2022,2,25,12,33))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,2,24,12,33))
                .beginEventDateTime(LocalDateTime.of(2022,2,23,12,33))
                .endEventDateTime(LocalDateTime.of(2022,2,22,12,33))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
                ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given (이벤트 30개 있어야 함)
        IntStream.range(0,30).forEach(this::generateEvent);

        // When (조회, 페이징과 솔팅이 가능 해야 함)
        this.mockMvc.perform(get("/api/events")
                        .param("page","1")
                        .param("size","10")
                        .param("sort","name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    private void generateEvent(int index) {
        Event event = Event.builder()
                .name("event "+index)
                .description("test event")
                .build();

        this.eventRepository.save(event);
    }
}
