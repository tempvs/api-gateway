package club.tempvs.gateway.filter;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TokenFilter implements GlobalFilter {

    private static final String CHAR_ENCODING = "UTF-8";
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    private String token;

    public TokenFilter(@Value("${authorization.token}") String token) {
        this.token = token;
    }

    @Override
    @SneakyThrows
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        byte[] tokenBytes = token.getBytes(CHAR_ENCODING);
        String tokenHash = DigestUtils.md5DigestAsHex(tokenBytes);
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header(AUTHORIZATION_HEADER_NAME, tokenHash)
                .build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
