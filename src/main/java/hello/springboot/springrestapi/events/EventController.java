package hello.springboot.springrestapi.events;

import hello.springboot.springrestapi.accounts.Account;
import hello.springboot.springrestapi.accounts.AccountAdapter;
import hello.springboot.springrestapi.accounts.CurrentUser;
import hello.springboot.springrestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value="/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper=modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto
                                    , Errors errors
                                    ,@CurrentUser Account currentUser) {

        if(errors.hasErrors()) {
            return badRequest(errors);
        }

        eventValidator.validator(eventDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update(); //free, offline 여부 변경
        event.setManager(currentUser);
        Event newEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource); //이렇게 하면 이벤트 리소스를 본문에 넣어줄 수 있음.
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable
            , PagedResourcesAssembler<Event> assembler
            , @CurrentUser Account account) { //getPrincipal로 가져올 수 있는 객체를 바로 주입 받을 수 있음.

        Page<Event> page = this.eventRepository.findAll(pageable);
        //var pagedResources = assembler.toModel(page); //PagedModel<EntityModel<Event>>
        var pagedResources = assembler.toModel(page, e -> new EventResource(e));
        pagedResources.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        if(account != null) {
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok().body(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        //Optional로 할 수 있는것은 만약 없다면,
        if(optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        //리소스를 보낼 때 응답에 대한 해석을 할 수 있도록 link를 추가해야해.
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile")); //resources-events-get 정보는 index.adoc에 담겨있음.
        if(event.getManager().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event")); //업데이트 링크를 매니저일 경우만 노출
        }
        return ResponseEntity.ok(eventResource); //resource로 만들어서 보내야 해.
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto, Errors errors
                                    , @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        //옵션이 비어있다면, 이떄 바인딩 에러가 있다면
        if(errors.hasErrors()) {
            return badRequest(errors); //위에서 만든 메소드
        }

        this.eventValidator.validator(eventDto,errors); //바인딩이 잘 됐는지 확인.
        if(errors.hasErrors()) {    //이번엔 로직상 문제가 있는ㄱㅓ다.
            return badRequest(errors);
        }
        //여기까지 문제가 없다면 update

        Event existingEvent = optionalEvent.get();
        if(!existingEvent.getManager().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED); //인가되지 않음.
        }
        // existingEvent에 있는 값을 매개변수로 들어온 eventDto로 전부 변경하면 됨.
        this.modelMapper.map(eventDto, existingEvent); //source가 먼저. 어디에서 - 어디로. (순서)
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }
}
