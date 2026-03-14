package club.tempvs.gateway.filter

import club.tempvs.gateway.helper.CryptoHelper
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import static org.springframework.http.server.reactive.ServerHttpRequest.Builder
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.server.ServerWebExchange
import spock.lang.Specification
import spock.lang.Subject

class AuthFilterSpec extends Specification {

    private static final String AUTH_COOKIE_NAME = "TEMPVS_AUTH"
    private static final String USER_INFO_HEADER_NAME = "User-Info"

    CryptoHelper cryptoHelper = Mock CryptoHelper

    @Subject
    private AuthFilter authFilter = new AuthFilter(cryptoHelper)

    ServerWebExchange exchange = Mock ServerWebExchange
    GatewayFilterChain chain = Mock GatewayFilterChain

    ServerHttpRequest request = Mock ServerHttpRequest
    ServerHttpRequest mutatedRequest = Mock ServerHttpRequest
    Builder requestBuilder = Mock Builder
    ServerWebExchange.Builder exchangeBuilder = Mock ServerWebExchange.Builder
    HttpCookie cookie = Mock HttpCookie

    def "authFilter adds 'User-Info' header downstream"() {
        given:
        MultiValueMap<String, HttpCookie> cookies = [(AUTH_COOKIE_NAME): [cookie]] as LinkedMultiValueMap
        String userInfoValue = 'my-cookie'
        String authCookieValue = userInfoValue.bytes.encodeBase64().toString()

        when:
        authFilter.filter(exchange, chain)

        then:
        1 * cryptoHelper.decrypt(authCookieValue) >> userInfoValue
        1 * exchange.request >> request
        1 * request.cookies >> cookies
        1 * cookie.value >> authCookieValue
        1 * request.mutate() >> requestBuilder
        1 * requestBuilder.header(USER_INFO_HEADER_NAME, userInfoValue) >> requestBuilder
        1 * requestBuilder.build() >> mutatedRequest
        1 * exchange.mutate() >> exchangeBuilder
        1 * exchangeBuilder.request(mutatedRequest) >> exchangeBuilder
        1 * exchangeBuilder.build() >> exchange
        1 * chain.filter(exchange)
        0 * _
    }
}
