package com.orlandini.academiawebflux;
import com.orlandini.academiawebflux.dto.*;
import com.orlandini.academiawebflux.exception.*;
import com.orlandini.academiawebflux.model.*;
import com.orlandini.academiawebflux.repo.*;
import com.orlandini.academiawebflux.security.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.*;
import reactor.core.publisher.Mono;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"app.bootstrap.enabled=false", "security.jwt.expiration=PT1H"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AcademiaApiIntegrationTest {
    private static final String DB = "academia_it_" + UUID.randomUUID().toString().replace("-", "");
    private static final String SECRET = Base64.getEncoder().encodeToString("12345678901234567890123456789012".getBytes(java.nio.charset.StandardCharsets.UTF_8));
    private static final String PASSWORD = "Prueba-JWT-123";
    @DynamicPropertySource static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", () -> "mongodb://localhost:27017/" + DB);
        registry.add("security.jwt.secret", () -> SECRET);
    }
    @Autowired Environment environment;
    @Autowired IEstudianteRepo estudiantes;
    @Autowired ICursoRepo cursos;
    @Autowired IMatriculaRepo matriculas;
    @Autowired IUserRepo users;
    @Autowired IRoleRepo roles;
    @Autowired PasswordEncoder encoder;
    @Autowired ReactiveMongoTemplate mongo;
    @Autowired JwtUtil jwt;
    @Autowired JwtAccessDeniedHandler denied;
    WebTestClient client;
    String accessToken;
    Role adminRole;

    @BeforeAll void prepare() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + environment.getProperty("local.server.port"))
                .responseTimeout(Duration.ofSeconds(20)).build();
        adminRole = roles.save(new Role(null, "ADMIN")).block();
        Role regular = roles.save(new Role(null, "USER")).block();
        String hash = encoder.encode(PASSWORD);
        users.save(new User(null, "test-admin", hash, true, List.of(new Role(adminRole.getId(), null)))).block();
        users.save(new User(null, "test-disabled", hash, false, List.of(new Role(adminRole.getId(), null)))).block();
        users.save(new User(null, "test-user", hash, true, List.of(new Role(regular.getId(), null)))).block();
        accessToken = login("/login", "test-admin", PASSWORD);
        assertNotNull(accessToken);
    }
    @BeforeEach void clearBusinessData() {
        matriculas.deleteAll().then(estudiantes.deleteAll()).then(cursos.deleteAll()).block();
    }
    @AfterAll void cleanOnlyGeneratedDatabase() {
        mongo.getMongoDatabase().flatMap(database -> {
            if (!database.getName().equals(DB) || !DB.startsWith("academia_it_"))
                return Mono.error(new IllegalStateException("Se impidio borrar una base ajena a la prueba"));
            return Mono.from(database.drop());
        }).block(Duration.ofSeconds(20));
    }
    private String login(String path, String username, String password) {
        AuthResponse response = client.post().uri(path).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("username", username, "password", password)).exchange()
                .expectStatus().isOk().expectBody(AuthResponse.class).returnResult().getResponseBody();
        return Objects.requireNonNull(response).access_token();
    }
    private WebTestClient.ResponseSpec call(HttpMethod method, String path, Object body, String token) {
        var request = client.method(method).uri(path).contentType(MediaType.APPLICATION_JSON);
        if (token != null) request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return body == null ? request.exchange() : request.bodyValue(body).exchange();
    }
    private WebTestClient.ResponseSpec call(HttpMethod method, String path, Object body) {
        return call(method, path, body, accessToken);
    }
    private Map<String, Object> student(String name, int age) {
        return Map.of("nombres", name, "apellidos", "Perez", "dni", "00123456", "edad", age);
    }
    private Map<String, Object> course(String name, boolean active) {
        return Map.of("nombre", name, "siglas", "CUR", "estado", active);
    }
    @SuppressWarnings("unchecked")
    private Map<String, Object> created(String path, Object body) {
        return call(HttpMethod.POST, path, body).expectStatus().isCreated()
                .expectBody(Map.class).returnResult().getResponseBody();
    }
    @SuppressWarnings("unchecked")
    private Map<String, Object> error(WebTestClient.ResponseSpec response, int status) {
        Map<String, Object> value = response.expectStatus().isEqualTo(status).expectHeader()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON).expectBody(Map.class).returnResult().getResponseBody();
        assertNotNull(value);
        assertEquals(Set.of("datetime", "message"), value.keySet());
        assertDoesNotThrow(() -> LocalDateTime.parse((String)value.get("datetime")));
        assertEquals(ErrorMessages.forStatus(status), value.get("message"));
        return value;
    }
    private Map<String, Object> enrollment(String studentId, List<String> courseIds) {
        return Map.of("fechaMatricula", "2026-10-30T09:15:00", "estudiante", Map.of("id", studentId),
                "cursos", courseIds.stream().map(id -> Map.of("id", id)).toList(), "estado", false);
    }
    private String signedToken(String subject, Instant expiration, boolean wrongKey) {
        String secret = wrongKey ? Base64.getEncoder().encodeToString("abcdefghijklmnopqrstuvwxyz123456".getBytes(java.nio.charset.StandardCharsets.UTF_8)) : SECRET;
        var builder = Jwts.builder().subject(subject).claim("roles", List.of("ADMIN"))
                .issuedAt(Date.from(Instant.now().minusSeconds(120)));
        if (expiration != null) builder.expiration(Date.from(expiration));
        return builder.signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)), Jwts.SIG.HS256).compact();
    }

    @ParameterizedTest @ValueSource(strings={"/v1", "/v2"})
    void crudEstudiantesYLocation(String prefix) {
        var body = new HashMap<>(student("Ana", 0)); body.put("id", "id-del-cliente");
        var result = call(HttpMethod.POST, prefix + "/estudiantes?origen=prueba", body)
                .expectStatus().isCreated().expectBody(Map.class).returnResult();
        var dto = Objects.requireNonNull(result.getResponseBody());
        // Leer el JSON enviado por el servidor: READ_ONLY del DTO de entrada
        // también excluye el ID si ese mismo tipo se usa como cliente para deserializar.
        assertNotNull(dto.get("id"));
        assertNotEquals("id-del-cliente", dto.get("id")); assertEquals("00123456", dto.get("dni")); assertEquals(0, dto.get("edad"));
        assertEquals(Set.of("id","nombres","apellidos","dni","edad"), dto.keySet());
        var location = Objects.requireNonNull(result.getResponseHeaders().getLocation());
        assertEquals("http", location.getScheme()); assertEquals(Integer.parseInt(environment.getProperty("local.server.port")), location.getPort());
        assertEquals(prefix + "/estudiantes/" + dto.get("id"), location.getPath()); assertNull(location.getQuery());
        call(HttpMethod.GET, prefix + "/estudiantes", null).expectStatus().isOk().expectBody().jsonPath("$.length()").isEqualTo(1);
        call(HttpMethod.GET, location.getPath(), null).expectStatus().isOk().expectBody()
                .jsonPath("$.nombres").isEqualTo("Ana").jsonPath("$.apellidos").isEqualTo("Perez");
        call(HttpMethod.PUT, location.getPath(), Map.of("id", "otro", "nombres", "Luisa", "apellidos", "Diaz", "dni", "00008", "edad", 27))
                .expectStatus().isOk().expectBody().jsonPath("$.id").isEqualTo(dto.get("id"))
                .jsonPath("$.nombres").isEqualTo("Luisa").jsonPath("$.apellidos").isEqualTo("Diaz")
                .jsonPath("$.dni").isEqualTo("00008").jsonPath("$.edad").isEqualTo(27);
        call(HttpMethod.DELETE, location.getPath(), null).expectStatus().isNoContent().expectBody().isEmpty();
        error(call(HttpMethod.GET, location.getPath(), null), 404);
        error(call(HttpMethod.PUT, location.getPath(), student("Ana", 20)), 404);
        error(call(HttpMethod.DELETE, location.getPath(), null), 404);
        assertEquals(0L, estudiantes.count().block());
    }
    @ParameterizedTest @ValueSource(strings={"/v1", "/v2"})
    void crudCursosAceptaFalse(String prefix) {
        var dto = created(prefix + "/cursos", course("Java", false));
        String path = prefix + "/cursos/" + dto.get("id");
        assertEquals(false, dto.get("estado")); assertEquals("CUR", dto.get("siglas"));
        call(HttpMethod.GET, prefix + "/cursos", null).expectStatus().isOk().expectBody().jsonPath("$.length()").isEqualTo(1);
        call(HttpMethod.GET, path, null).expectStatus().isOk().expectBody().jsonPath("$.nombre").isEqualTo("Java");
        call(HttpMethod.PUT, path, Map.of("nombre", "WebFlux", "siglas", "WF", "estado", true))
                .expectStatus().isOk().expectBody().jsonPath("$.id").isEqualTo(dto.get("id"))
                .jsonPath("$.nombre").isEqualTo("WebFlux").jsonPath("$.siglas").isEqualTo("WF").jsonPath("$.estado").isEqualTo(true);
        call(HttpMethod.DELETE, path, null).expectStatus().isNoContent().expectBody().isEmpty();
        error(call(HttpMethod.GET, path, null), 404);
        error(call(HttpMethod.PUT, path, course("Java", true)), 404);
        error(call(HttpMethod.DELETE, path, null), 404);
        assertEquals(0L, cursos.count().block());
    }
    @ParameterizedTest @ValueSource(strings={"/v1", "/v2"})
    void coleccionesYOrdenamientoVacios(String prefix) {
        for (String path : List.of("/estudiantes", "/cursos", "/estudiantes/edad/asc", "/estudiantes/edad/desc"))
            call(HttpMethod.GET, prefix + path, null).expectStatus().isOk().expectBody().json("[]");
    }
    @ParameterizedTest @ValueSource(strings={"/v1", "/v2"})
    void ordenPorEdadYDesempate(String prefix) {
        for (int age : List.of(45,18,30,18)) created(prefix + "/estudiantes", student("Ana", age));
        var asc = call(HttpMethod.GET, prefix + "/estudiantes/edad/asc", null).expectStatus().isOk()
                .expectBodyList(Map.class).returnResult().getResponseBody();
        var desc = call(HttpMethod.GET, prefix + "/estudiantes/edad/desc", null).expectStatus().isOk()
                .expectBodyList(Map.class).returnResult().getResponseBody();
        assertEquals(List.of(18,18,30,45), asc.stream().map(e -> e.get("edad")).toList());
        assertEquals(List.of(45,30,18,18), desc.stream().map(e -> e.get("edad")).toList());
        var ties = asc.stream().filter(e -> ((Number)e.get("edad")).intValue()==18).map(e -> (String)e.get("id")).toList();
        assertEquals(ties.stream().sorted().toList(), ties);
        assertEquals(ties, desc.stream().filter(e -> ((Number)e.get("edad")).intValue()==18).map(e -> (String)e.get("id")).toList());
    }
    @ParameterizedTest @ValueSource(strings={"/v1", "/v2"})
    void matriculaCompletaPersistidaEHistorica(String prefix) {
        String other = prefix.equals("/v1") ? "/v2" : "/v1";
        String e = (String)created(prefix + "/estudiantes", student("Ana",20)).get("id");
        String c1 = (String)created(prefix + "/cursos", course("Java",true)).get("id");
        String c2 = (String)created(prefix + "/cursos", course("Reactividad",false)).get("id");
        var created = call(HttpMethod.POST, prefix + "/matriculas?origen=prueba", enrollment(e,List.of(c2,c1)))
                .expectStatus().isCreated().expectBody(Map.class).returnResult();
        Map dto = Objects.requireNonNull(created.getResponseBody());
        Map alumno = (Map)dto.get("estudiante");
        @SuppressWarnings("unchecked") List<Map<String,Object>> cursosJson = (List<Map<String,Object>>)dto.get("cursos");
        assertEquals(false,dto.get("estado")); assertEquals(LocalDateTime.of(2026,10,30,9,15),LocalDateTime.parse((String)dto.get("fechaMatricula")));
        assertEquals(e,alumno.get("id")); assertEquals("Ana",alumno.get("nombres"));
        assertEquals("00123456",alumno.get("dni")); assertEquals(20,alumno.get("edad"));
        assertEquals(Set.of("id","nombres","apellidos","dni","edad"),alumno.keySet());
        assertEquals(List.of(c2,c1),cursosJson.stream().map(c -> c.get("id")).toList());
        assertEquals("Reactividad",cursosJson.getFirst().get("nombre")); assertEquals(false,cursosJson.getFirst().get("estado"));
        assertEquals(Set.of("id","nombre","siglas","estado"),cursosJson.getFirst().keySet());
        assertNull(created.getResponseHeaders().getLocation().getQuery());
        Matricula persisted = matriculas.findById((String)dto.get("id")).block();
        assertNotNull(persisted); assertEquals("Ana",persisted.getEstudiante().getNombres());
        assertEquals("Java",persisted.getCursos().get(1).getNombre()); assertEquals(1L,matriculas.count().block());
        call(HttpMethod.PUT,other + "/estudiantes/" + e,student("Modificada",55)).expectStatus().isOk();
        call(HttpMethod.PUT,other + "/cursos/" + c2,course("Otro nombre",true)).expectStatus().isOk();
        call(HttpMethod.DELETE,other + "/estudiantes/" + e,null).expectStatus().isNoContent();
        call(HttpMethod.DELETE,other + "/cursos/" + c2,null).expectStatus().isNoContent();
        call(HttpMethod.GET,other + "/matriculas/" + dto.get("id"),null).expectStatus().isOk().expectBody()
                .jsonPath("$.estudiante.nombres").isEqualTo("Ana").jsonPath("$.estudiante.edad").isEqualTo(20)
                .jsonPath("$.cursos[0].nombre").isEqualTo("Reactividad").jsonPath("$.cursos[0].estado").isEqualTo(false);
        error(call(HttpMethod.GET,prefix + "/matriculas/inexistente",null),404);
    }
    @ParameterizedTest @ValueSource(strings={"/v1", "/v2"})
    void matriculaRechazaReferenciasSinEscrituraParcial(String prefix) {
        String e=(String)created(prefix+"/estudiantes",student("Ana",20)).get("id");
        String c=(String)created(prefix+"/cursos",course("Java",true)).get("id");
        error(call(HttpMethod.POST,prefix+"/matriculas",enrollment("inexistente",List.of(c))),404);
        assertEquals(0L,matriculas.count().block());
        error(call(HttpMethod.POST,prefix+"/matriculas",enrollment(e,List.of(c,"inexistente"))),404);
        assertEquals(0L,matriculas.count().block());
        error(call(HttpMethod.POST,prefix+"/matriculas",enrollment(e,List.of(c,c))),400);
        assertEquals(0L,matriculas.count().block());
    }
    @ParameterizedTest @ValueSource(strings={"/v1", "/v2"})
    void validacionAnidadaFechaEstadoYLista(String prefix) {
        var valid=new HashMap<String,Object>(enrollment("e",List.of("c")));
        var bad=new HashMap<>(valid); bad.remove("fechaMatricula"); error(call(HttpMethod.POST,prefix+"/matriculas",bad),400);
        bad=new HashMap<>(valid); bad.remove("estado"); error(call(HttpMethod.POST,prefix+"/matriculas",bad),400);
        bad=new HashMap<>(valid); bad.put("estudiante",Map.of()); error(call(HttpMethod.POST,prefix+"/matriculas",bad),400);
        bad=new HashMap<>(valid); bad.put("cursos",List.of(Map.of("id"," "))); error(call(HttpMethod.POST,prefix+"/matriculas",bad),400);
        bad=new HashMap<>(valid); bad.put("cursos",Arrays.asList((Object)null)); error(call(HttpMethod.POST,prefix+"/matriculas",bad),400);
        bad=new HashMap<>(valid); bad.put("cursos",List.of()); error(call(HttpMethod.POST,prefix+"/matriculas",bad),400);
        assertEquals(0L,matriculas.count().block());
    }
    @ParameterizedTest @ValueSource(strings={"/v1", "/v2"})
    void validacionesYTiposDeEntrada(String prefix) {
        error(call(HttpMethod.POST,prefix+"/estudiantes",student(" ",20)),400);
        error(call(HttpMethod.POST,prefix+"/estudiantes",student("Ana",-1)),400);
        var missing=new HashMap<>(student("Ana",20)); missing.remove("edad");
        error(call(HttpMethod.POST,prefix+"/estudiantes",missing),400);
        error(call(HttpMethod.POST,prefix+"/estudiantes",Map.of("nombres","Ana","apellidos","Perez","dni","001","edad","texto")),400);
        error(call(HttpMethod.POST,prefix+"/cursos",Map.of("nombre","Java","siglas","JV")),400);
        error(call(HttpMethod.POST,prefix+"/cursos",Map.of("nombre"," ","siglas","JV","estado",true)),400);
        error(call(HttpMethod.POST,prefix+"/estudiantes","{"),400);
        error(call(HttpMethod.POST,prefix+"/estudiantes","null"),400);
        var unsupported=client.post().uri(prefix+"/cursos").header("Authorization","Bearer "+accessToken)
                .contentType(MediaType.TEXT_PLAIN).bodyValue("texto").exchange();
        error(unsupported,415);
    }
    @ParameterizedTest @ValueSource(strings={"/v1", "/v2"})
    void cuerposAusentesSon400(String prefix) {
        for(String resource:List.of("estudiantes","cursos","matriculas"))
            error(call(HttpMethod.POST,prefix+"/"+resource,null),400);
        for(String resource:List.of("estudiantes","cursos"))
            error(call(HttpMethod.PUT,prefix+"/"+resource+"/inexistente",null),400);
    }
    @ParameterizedTest @ValueSource(strings={"/login", "/v2/login"})
    void contratoLoginYCredenciales(String path) {
        String token=login(path,"test-admin",PASSWORD);
        assertEquals("test-admin",jwt.validate(token).getSubject()); assertEquals(List.of("ADMIN"),jwt.validate(token).get("roles"));
        error(call(HttpMethod.POST,path,Map.of("username","test-admin","password","incorrecta"),null),401);
        error(call(HttpMethod.POST,path,Map.of("username","desconocido","password",PASSWORD),null),401);
        error(call(HttpMethod.POST,path,Map.of("username","test-disabled","password",PASSWORD),null),401);
        error(call(HttpMethod.POST,path,Map.of("username"," ","password",PASSWORD),null),400);
        error(call(HttpMethod.POST,path,null,null),400);
    }
    @ParameterizedTest @ValueSource(strings={"/v1", "/v2"})
    void todosLosEndpointsExigenTokenVigente(String prefix) {
        List<String> invalid=Arrays.asList(null,"malformado",
                signedToken("test-admin",Instant.now().minusSeconds(60),false),
                signedToken("test-admin",Instant.now().plusSeconds(600),true),
                signedToken("test-admin",null,false));
        Map<String,List<HttpMethod>> endpoints=new LinkedHashMap<>();
        endpoints.put("/estudiantes",List.of(HttpMethod.GET,HttpMethod.POST));
        endpoints.put("/estudiantes/id",List.of(HttpMethod.GET,HttpMethod.PUT,HttpMethod.DELETE));
        endpoints.put("/estudiantes/edad/asc",List.of(HttpMethod.GET));
        endpoints.put("/estudiantes/edad/desc",List.of(HttpMethod.GET));
        endpoints.put("/cursos",List.of(HttpMethod.GET,HttpMethod.POST));
        endpoints.put("/cursos/id",List.of(HttpMethod.GET,HttpMethod.PUT,HttpMethod.DELETE));
        endpoints.put("/matriculas",List.of(HttpMethod.POST));
        endpoints.put("/matriculas/id",List.of(HttpMethod.GET));
        endpoints.forEach((path,methods)->methods.forEach(method->invalid.forEach(token->
                error(call(method,prefix+path,null,token),401))));
    }
    @Test void paridadDeErroresEntreAmbosEstilos() {
        var a=error(call(HttpMethod.POST,"/v1/estudiantes",student(" ",20)),400);
        var b=error(call(HttpMethod.POST,"/v2/estudiantes",student(" ",20)),400);
        assertEquals(a.get("message"),b.get("message"));
        a=error(call(HttpMethod.GET,"/v1/cursos/inexistente",null),404);
        b=error(call(HttpMethod.GET,"/v2/cursos/inexistente",null),404);
        assertEquals(a.get("message"),b.get("message"));
    }
    @Test void ambosEstilosCompartenDatos() {
        String id=(String)created("/v1/estudiantes",student("Compartida",20)).get("id");
        call(HttpMethod.GET,"/v2/estudiantes/"+id,null).expectStatus().isOk().expectBody().jsonPath("$.nombres").isEqualTo("Compartida");
        call(HttpMethod.PUT,"/v2/estudiantes/"+id,student("Actualizada",21)).expectStatus().isOk();
        call(HttpMethod.GET,"/v1/estudiantes/"+id,null).expectStatus().isOk().expectBody().jsonPath("$.nombres").isEqualTo("Actualizada");
    }
    @Test void usuarioDeshabilitadoTrasEmitirTokenEsRechazado() {
        User user=users.save(new User(null,"revocable",encoder.encode(PASSWORD),true,List.of(new Role(adminRole.getId(),null)))).block();
        String token=login("/login","revocable",PASSWORD);
        user.setStatus(false); users.save(user).block();
        error(call(HttpMethod.GET,"/v1/cursos",null,token),401);
        error(call(HttpMethod.GET,"/v2/cursos",null,token),401);
    }
    @Test void rolUserTambienPuedeConsumirServicios() {
        String token=login("/login","test-user",PASSWORD);
        call(HttpMethod.GET,"/v1/cursos",null,token).expectStatus().isOk();
        call(HttpMethod.GET,"/v2/cursos",null,token).expectStatus().isOk();
    }
    @Test void accesoDenegadoUsaElMismoFormatoJson() {
        var exchange=MockServerWebExchange.from(MockServerHttpRequest.get("/protegido"));
        denied.handle(exchange,new AccessDeniedException("detalle interno")).block();
        assertEquals(HttpStatus.FORBIDDEN,exchange.getResponse().getStatusCode());
        String body=exchange.getResponse().getBodyAsString().block();
        assertTrue(body.contains("datetime")); assertTrue(body.contains(ErrorMessages.forStatus(403)));
        assertFalse(body.contains("detalle interno"));
    }
}
