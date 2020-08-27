/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2020.
 */

package ch.sbb.esta.scs.configuration.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class SwaggerRedirectController {

    private final String swaggerUIPath;

    public SwaggerRedirectController(@Value("${springdoc.swagger-ui.path}") final String swaggerUIPath) {
        this.swaggerUIPath = swaggerUIPath;
    }

    @GetMapping("/")
    public RedirectView redirectToSwaggerUi() {
        RedirectView redirectView = new RedirectView(swaggerUIPath);
        // is necessary to keep HTTPS for redirects (otherwise it redirects to HTTP even if request was HTTPS)
        redirectView.setHttp10Compatible(false);
        return redirectView;
    }
}
