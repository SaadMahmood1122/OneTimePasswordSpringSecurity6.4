Spring Security One-Time Token Authentication
Enhance your application's security with passwordless authentication using Spring Security's One-Time Token (OTT) feature. This project demonstrates how to implement magic link authentication, allowing users to securely log in through email-delivered tokens.

Overview
===========
This Spring Boot application showcases a modern approach to authentication by implementing magic links through Spring Security's OTT functionality. When users attempt to log in, they receive a secure one-time token via email that grants them temporary access to the application.

Project Requirements
=================
Java 17 or higher
Spring Boot 3.x
Maven or Gradle build tool
SendGrid account for email delivery

Dependencies
============
The project relies on the following key dependencies:
Spring Boot Starter Web
Spring Boot Starter Security
Spring Boot Starter Test
SendGrid Java Library
JTE (Java Template Engine)

Getting Started
===============
Set up your SendGrid API key:

sendgrid.api-key=${SENDGRID_API_KEY}
Configure email settings in EmailService.java:

Email from = new Email("your-sender@email.com");
Update user configuration in SecurityConfig.java as needed:

var user = User.withUsername("youruser")
    .password("{noop}password")
    .build();
    
How It Works
===========
Authentication Flow
User requests access to a protected resource
System generates a one-time token
Token is sent via email using SendGrid
User clicks the magic link containing the token
Spring Security validates the token and grants access
Key Components

Security Configuration
======================
The SecurityConfig class sets up the security filter chain and configures authentication:

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/ott/sent").permitAll()
                    .requestMatchers("/login/ott").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .oneTimeTokenLogin(Customizer.withDefaults())
            .build();
}

OTT Success Handler
===================
The OttSuccessHandler manages token generation and email delivery:

@Override
public void handle(HttpServletRequest request, HttpServletResponse response, 
                  OneTimeToken oneTimeToken) throws IOException, ServletException {
    UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
            .replacePath(request.getContextPath())
            .replaceQuery(null)
            .fragment(null)
            .path("/login/ott")
            .queryParam("token", oneTimeToken.getTokenValue());

    String magicLink = builder.toUriString();
    // Send magic link via email
}

Frontend Templates
==================
The application includes two JTE templates:

index.jte: Main application page (protected)
sent.jte: Confirmation page for token delivery
Testing
Run the included tests using your preferred build tool:

./mvnw test   # Maven
./gradlew test # Gradle
Security Considerations
