package io.micronaut.jaxrs.container;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/micronaut")
public class MicronautController {
    @Get("/test")
    String test() {
        return "ok";
    }
}
