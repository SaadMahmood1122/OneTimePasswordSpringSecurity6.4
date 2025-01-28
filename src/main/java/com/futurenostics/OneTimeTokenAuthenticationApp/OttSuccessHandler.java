package com.futurenostics.OneTimeTokenAuthenticationApp;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.authentication.ott.RedirectOneTimeTokenGenerationSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
@Component
public class OttSuccessHandler implements OneTimeTokenGenerationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OttSuccessHandler.class);
    private final OneTimeTokenGenerationSuccessHandler delegate
            = new RedirectOneTimeTokenGenerationSuccessHandler("/ott/sent");

    private final EmailService emailService;

    public OttSuccessHandler(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, OneTimeToken oneTimeToken) throws IOException, ServletException {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .path("/login/ott")
                .queryParam("token", oneTimeToken.getTokenValue());

        String magicLink = uriComponentsBuilder.toUriString();

        // send through (email, SendGrid) or sms
        // sendGrid => watch tutorial on sendGrid => https://youtu.be/i8Hvvo4ZITg?si=kqD2q11tYiKfwbGQ
        System.out.println("magicLink :: "+magicLink);

        var body = """
                Hello, from Spring Security! 
                Below you will find your magic link to login to our super secret application!
                
                %s
                """.formatted(magicLink);

        try {
            var sendTo = oneTimeToken.getUsername();
            log.info("Sending One Time Token to username: {}", sendTo);
            emailService.sendEmail(
                    getEmail(sendTo),
                    "One Time Token Login",
                    body
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.delegate.handle(request, response, oneTimeToken);

    }

    private String getEmail(String username) {
        // this would be a database lookup for username
        log.info("Retrieving email for user: {}", username);
        return "sk8066709@gmail.com";
    }
}
