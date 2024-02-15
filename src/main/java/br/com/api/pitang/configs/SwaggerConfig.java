package br.com.api.pitang.configs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;
import static springfox.documentation.spi.service.contexts.SecurityContext.builder;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(SWAGGER_2).select()
                .apis(basePackage("br.com.api.pitang.controllers"))
                .paths(any()).build().useDefaultResponseMessages(false)
                .securityContexts(newArrayList(securityContext()))
                .securitySchemes(newArrayList(apiKey()))
                .globalResponseMessage(POST, responseMessageForPOST()).apiInfo(apiInfo());
    }

    private List<ResponseMessage> responseMessageForPOST() {
        return new ArrayList<ResponseMessage>() {
            private static final long serialVersionUID = 1L;
            {
                add(new ResponseMessageBuilder().code(INTERNAL_SERVER_ERROR.value()).message(INTERNAL_SERVER_ERROR.getReasonPhrase()).build());
                add(new ResponseMessageBuilder().code(BAD_REQUEST.value()).message(BAD_REQUEST.getReasonPhrase()).build());
            }
        };
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/api/.*"))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return newArrayList(
                new SecurityReference("JWT", authorizationScopes));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Pitang Api")
                .description("Projeto do desafio Pitang TCE.")
                .version("0.1-SNAPSHOT")
                .contact(new Contact("Ricardo Lima",
                        "https://github.com/zlimaaa",
                        "ricardolima.dev@gmail.com"))
                .build();
    }
}
