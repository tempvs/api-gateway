package club.tempvs.gateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.server.ServerWebExchange
import spock.lang.Specification
import spock.lang.Subject

class TokenFilterSpec extends Specification {

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization"
    private static final String TOKEN = "token"

    @Subject
    TokenFilter tokenFilter

    def exchange = Mock ServerWebExchange
    def chain = Mock GatewayFilterChain

    def request = Mock ServerHttpRequest
    def mutatedRequest = Mock ServerHttpRequest
    def requestBuilder = Mock ServerHttpRequest.Builder
    def exchangeBuilder = Mock ServerWebExchange.Builder

    def setup() {
        tokenFilter = new TokenFilter(TOKEN)
    }

    def "tokenFilter adds 'Authorization' header downstream"() {
        given:
        String encodedToken = TOKEN.md5()

        when:
        tokenFilter.filter(exchange, chain)

        then:
        1 * exchange.request >> request
        1 * request.mutate() >> requestBuilder
        1 * requestBuilder.header(AUTHORIZATION_HEADER_NAME, encodedToken) >> requestBuilder
        1 * requestBuilder.build() >> mutatedRequest
        1 * exchange.mutate() >> exchangeBuilder
        1 * exchangeBuilder.request(mutatedRequest) >> exchangeBuilder
        1 * exchangeBuilder.build() >> exchange
        1 * chain.filter(exchange)
        0 * _
    }
}
