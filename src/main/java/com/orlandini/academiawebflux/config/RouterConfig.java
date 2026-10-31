package com.orlandini.academiawebflux.config;
import com.orlandini.academiawebflux.handler.*;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.server.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> estudianteRoutes(EstudianteHandler h) {
        return route(GET("/v2/estudiantes/edad/asc"), h::edadAsc)
                .andRoute(GET("/v2/estudiantes/edad/desc"), h::edadDesc)
                .andRoute(GET("/v2/estudiantes"), h::findAll)
                .andRoute(GET("/v2/estudiantes/{id}"), h::findById)
                .andRoute(POST("/v2/estudiantes"), h::save)
                .andRoute(PUT("/v2/estudiantes/{id}"), h::update)
                .andRoute(DELETE("/v2/estudiantes/{id}"), h::delete);
    }
    @Bean
    public RouterFunction<ServerResponse> cursoRoutes(CursoHandler h) {
        return route(GET("/v2/cursos"), h::findAll)
                .andRoute(GET("/v2/cursos/{id}"), h::findById)
                .andRoute(POST("/v2/cursos"), h::save)
                .andRoute(PUT("/v2/cursos/{id}"), h::update)
                .andRoute(DELETE("/v2/cursos/{id}"), h::delete);
    }
    @Bean
    public RouterFunction<ServerResponse> matriculaRoutes(MatriculaHandler h) {
        return route(POST("/v2/matriculas"), h::save)
                .andRoute(GET("/v2/matriculas/{id}"), h::findById);
    }
    @Bean
    public RouterFunction<ServerResponse> loginRoutes(LoginHandler h) {
        return route(POST("/v2/login"), h::login);
    }
}
