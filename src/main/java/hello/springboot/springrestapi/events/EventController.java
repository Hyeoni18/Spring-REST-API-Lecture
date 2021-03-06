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
        event.update(); //free, offline ?????? ??????
        event.setManager(currentUser);
        Event newEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource); //????????? ?????? ????????? ???????????? ????????? ????????? ??? ??????.
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable
            , PagedResourcesAssembler<Event> assembler
            , @CurrentUser Account account) { //getPrincipal??? ????????? ??? ?????? ????????? ?????? ?????? ?????? ??? ??????.

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
        //Optional??? ??? ??? ???????????? ?????? ?????????,
        if(optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        //???????????? ?????? ??? ????????? ?????? ????????? ??? ??? ????????? link??? ???????????????.
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile")); //resources-events-get ????????? index.adoc??? ????????????.
        if(event.getManager().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event")); //???????????? ????????? ???????????? ????????? ??????
        }
        return ResponseEntity.ok(eventResource); //resource??? ???????????? ????????? ???.
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
        //????????? ???????????????, ?????? ????????? ????????? ?????????
        if(errors.hasErrors()) {
            return badRequest(errors); //????????? ?????? ?????????
        }

        this.eventValidator.validator(eventDto,errors); //???????????? ??? ????????? ??????.
        if(errors.hasErrors()) {    //????????? ????????? ????????? ???????????????.
            return badRequest(errors);
        }
        //???????????? ????????? ????????? update

        Event existingEvent = optionalEvent.get();
        if(!existingEvent.getManager().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED); //???????????? ??????.
        }
        // existingEvent??? ?????? ?????? ??????????????? ????????? eventDto??? ?????? ???????????? ???.
        this.modelMapper.map(eventDto, existingEvent); //source??? ??????. ???????????? - ?????????. (??????)
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }
}
