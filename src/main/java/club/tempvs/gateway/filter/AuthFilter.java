package club.tempvs.gateway.filter;

import club.tempvs.gateway.helper.CryptoHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter {

    private static final String AUTH_COOKIE_NAME = "TEMPVS_AUTH";
    private static final String USER_INFO_HEADER_NAME = "User-Info";

    private final CryptoHelper cryptoHelper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpCookie authCookie = request.getCookies()
                .getFirst(AUTH_COOKIE_NAME);

        String userInfoValue = Optional.ofNullable(authCookie)
                .map(HttpCookie::getValue)
                .map(cryptoHelper::decrypt)
                .map(String::new)
                .orElse(null);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(USER_INFO_HEADER_NAME, userInfoValue)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
