package com.tr.ing.brokerage.config;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;

public class SwaggerOpenApiConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("V1 Brokerage API")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.addParametersItem(
                            new HeaderParameter()
                                    .name("x-api-key")
                                    .description("Authorization using a provided API Key")
                                    .required(false)
                                    .schema(new Schema<String>().type("string").format("alphanumeric"))
                    );
                    return operation;
                })
                .pathsToMatch("/**")
                .build();
    }
}
