package codeverse.controller

import groovy.util.logging.Slf4j
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

@Slf4j
@SpringBootTest
@Unroll
class AuthenticationControllerSpec extends Specification {
    def "hello should log message"() {
        expect:
        log.info("hello world")
        true // dummy test
    }
}
