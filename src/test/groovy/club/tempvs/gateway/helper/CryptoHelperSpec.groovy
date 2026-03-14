package club.tempvs.gateway.helper

import spock.lang.Specification
import spock.lang.Subject

class CryptoHelperSpec extends Specification {

    private static final String KEY = 'thisisa128bitkey'

    @Subject
    private CryptoHelper cryptoHelper = new CryptoHelper(KEY)

    def "test encrypt"() {
        expect:
        cryptoHelper.encrypt('hello world!') == '07xI7leeYjRzyfoMrg6jbw=='
    }

    def "test decrypt"() {
        expect:
        cryptoHelper.decrypt('07xI7leeYjRzyfoMrg6jbw==') == 'hello world!'
    }

    def "test decrypt with url-encoded base64"() {
        when:
        String encoded = cryptoHelper.encrypt('hello world!').replace('/', '%2F')

        then:
        cryptoHelper.decrypt(encoded) == 'hello world!'
    }
}
