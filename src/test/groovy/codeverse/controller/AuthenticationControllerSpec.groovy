package codeverse.controller

import codeverse.com.web_be.controller.AuthenticationController
import codeverse.com.web_be.dto.request.AuthenRequest.AuthenticationRequest
import codeverse.com.web_be.dto.request.AuthenRequest.LogoutRequest
import codeverse.com.web_be.dto.response.AuthenResponse.AuthenticationResponse
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse
import codeverse.com.web_be.exception.AppException
import codeverse.com.web_be.exception.ErrorCode
import codeverse.com.web_be.service.AuthenService.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AuthenticationControllerSpec extends Specification {

    def authenticationService = Mock(AuthenticationService)
    def controller = new AuthenticationController(authenticationService)

    // ========== verifyEmailHtml ==========
    def "No: #no → verifyEmailHtml returns correct HTML for success/failure"() {
        given:
        if (shouldThrow) {
            authenticationService.verifyEmail(_ as String) >> { throw new AppException(ErrorCode.INVALID_KEY) }
        } else {
            authenticationService.verifyEmail(_ as String) >> {}
        }

        when:
        ResponseEntity<String> response = controller.verifyEmailHtml("dummyToken")

        then:
        response.statusCode.value() == expectedStatus
        response.body.contains(expectedHtmlSnippet)

        where:
        no       | shouldThrow || expectedStatus | expectedHtmlSnippet
        "case01" | false       || 200            | "Email Verified!"
        "case02" | true        || 400            | "Verification Failed"
    }

    // ========== login ==========
    def "No: #no → login authenticate returns ApiResponse with expected username"() {
        given:
        def req = new AuthenticationRequest(username: "user@test.com", password: "123")
        def mockResponse = new AuthenticationResponse(token: "abc-token")
        authenticationService.authenticate(_ as AuthenticationRequest) >> mockResponse

        when:
        ApiResponse<AuthenticationResponse> resp = controller.authenticate(req)

        then:
        resp.result.token == "abc-token"

        where:
        no << ["case01"]
    }

    // ========== refresh success ==========
    def "No: case01 → refresh returns OK when header valid"() {
        given:
        authenticationService.refreshToken("valid-refresh") >> new AuthenticationResponse(token: "new-token")

        when:
        def result = controller.refresh("Bearer valid-refresh")

        then:
        result.statusCode == HttpStatus.OK
        result.body.result.token == "new-token"
    }

    // ========== refresh invalid headers ==========
    def "No: #no → refresh throws AppException when header invalid"() {
        when:
        controller.refresh(authHeader)

        then:
        thrown(AppException)

        where:
        no       | authHeader
        "case02" | null
        "case03" | "Invalid xxx"
    }

    // ========== logout ==========
    def "No: #no → logout delegates to service and returns ApiResponse"() {
        given:
        def request = new LogoutRequest(token: "logout-token")

        when:
        def response = controller.logout(request)

        then:
        1 * authenticationService.logout(_ as LogoutRequest)
        response instanceof ApiResponse

        where:
        no << ["case01"]
    }
}
