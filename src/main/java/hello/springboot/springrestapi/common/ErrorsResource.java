package hello.springboot.springrestapi.common;

import hello.springboot.springrestapi.events.EventController;
import hello.springboot.springrestapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {

    public static EntityModel<Errors> modelOf(Errors errors) {
        EntityModel<Errors> errorsEntityModel = EntityModel.of(errors);
        errorsEntityModel.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
        return errorsEntityModel;
    }
}
