package com.goomez.CosmosX.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cosmosXOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("CosmosX API")
                .description("REST API for a fictional space agency: astronauts, spacecraft, planets, missions, statistics and exploration.")
                .version("v1.0.0")
                .license(new License().name("MIT")));
    }
}