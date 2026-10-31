package com.orlandini.academiawebflux.config;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
public final class UriHelper {
    private UriHelper() { }
    public static URI created(URI request, String id) {
        return UriComponentsBuilder.fromUri(request).replaceQuery(null).fragment(null)
                .pathSegment(id).build().toUri();
    }
}
