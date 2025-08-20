package codeverse.controller

import codeverse.com.web_be.controller.UserController
import codeverse.com.web_be.dto.request.UserRequest.LockUserRequest
import codeverse.com.web_be.dto.request.UserRequest.UserCreationByAdminRequest
import codeverse.com.web_be.dto.request.UserRequest.UserCreationRequest
import codeverse.com.web_be.dto.request.UserRequest.UserUpdateRequest
import codeverse.com.web_be.dto.response.UserResponse.UserDetailResponse
import codeverse.com.web_be.dto.response.UserResponse.UserResponse
import codeverse.com.web_be.entity.User
import codeverse.com.web_be.mapper.UserMapper
import codeverse.com.web_be.service.UserService.IUserService
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

class UserControllerSpec extends Specification {

    def userService = Mock(IUserService)
    def userMapper = Mock(UserMapper)
    def controller = new UserController(userService, userMapper)

    // ==============================
    // getAllUsers
    // ==============================
    def "getAllUsers returns list"() {
        given:
        def users = [new User(username: "a"), new User(username: "b")]
        def responses = [new UserResponse(username: "a"), new UserResponse(username: "b")]
        userService.findAll() >> users
        userMapper.userToUserResponse(_) >>> responses

        when:
        def result = controller.getAllUsers()

        then:
        result.statusCode == HttpStatus.OK
        result.body*.username == ["a", "b"]
    }

    def "getActiveUsers returns wrapped ApiResponse"() {
        given:
        def responses = [new UserResponse(username: "c")]
        userService.getActiveUsers() >> responses

        when:
        def result = controller.getActiveUsers()

        then:
        result.result == responses
        result.code == 200
    }

    // ==============================
    // getUserById
    // ==============================
    def "getUserById returns user when found"() {
        given:
        def user = new User(username: "a")
        def resp = new UserResponse(username: "a")
        userService.findById(1L) >> Optional.of(user)
        userMapper.userToUserResponse(user) >> resp

        when:
        def result = controller.getUserById(1L)

        then:
        result.statusCode == HttpStatus.OK
        result.body.username == "a"
    }

    def "getUserById returns 404 when not found"() {
        given:
        userService.findById(1L) >> Optional.empty()

        when:
        def result = controller.getUserById(1L)

        then:
        result.statusCode == HttpStatus.NOT_FOUND
    }

    // ==============================
    // myInfo + updateMyInfo
    // ==============================
    def "getMyInfo returns ApiResponse"() {
        given:
        def resp = new UserResponse(username: "me")
        userService.getMyInfo() >> resp

        when:
        def result = controller.getMyInfo()

        then:
        result.result.username == "me"
    }

    def "updateProfile delegates to service"() {
        given:
        def req = new UserUpdateRequest(name: "abc")
        def resp = new UserResponse(name: "abc")
        userService.updateMyInfo(req) >> resp

        when:
        def result = controller.updateProfile(req)

        then:
        result.result.name == "abc"
    }

    // ==============================
    // updateAvatar / updateQrCode
    // ==============================
    def "updateAvatar returns updated user"() {
        given:
        def file = Mock(MultipartFile)
        def resp = new UserResponse(username: "u1")
        userService.updateAvatar(file) >> resp

        when:
        def result = controller.updateAvatar(file)

        then:
        result.result.username == "u1"
    }

    def "updateQrCode returns updated user"() {
        given:
        def file = Mock(MultipartFile)
        def resp = new UserResponse(username: "u2")
        userService.updateQrCode(file) >> resp

        when:
        def result = controller.updateQrCode(file)

        then:
        result.result.username == "u2"
    }

    // ==============================
    // createUser
    // ==============================
    def "createUser persists and returns response"() {
        given:
        def req = new UserCreationRequest(username: "x")
        def user = new User(username: "x")
        def resp = new UserResponse(username: "x")
        userMapper.userRequestToUser(req) >> user
        userService.save(user) >> user
        userMapper.userToUserResponse(user) >> resp

        when:
        def result = controller.createUser(req)

        then:
        result.statusCode == HttpStatus.CREATED
        result.body.username == "x"
    }

    // ==============================
    // updateUser
    // ==============================
    def "updateUser updates when found"() {
        given:
        def req = new UserCreationRequest(username: "y")
        def user = new User(username: "y")
        def updated = new User(username: "yy")
        def resp = new UserResponse(username: "yy")
        userService.findById(1L) >> Optional.of(user)
        userService.update(user) >> updated
        userMapper.userToUserResponse(updated) >> resp

        when:
        def result = controller.updateUser(1L, req)

        then:
        result.statusCode == HttpStatus.OK
        result.body.username == "yy"
    }

    def "updateUser returns 404 when not found"() {
        given:
        def req = new UserCreationRequest(username: "y")
        userService.findById(1L) >> Optional.empty()

        when:
        def result = controller.updateUser(1L, req)

        then:
        result.statusCode == HttpStatus.NOT_FOUND
    }

    // ==============================
    // deleteUser
    // ==============================
    def "deleteUser calls service and returns noContent"() {
        when:
        def result = controller.deleteUser(1L)

        then:
        1 * userService.deleteById(1L)
        result.statusCode == HttpStatus.NO_CONTENT
    }

    // ==============================
    // lockOrUnlockUser
    // ==============================
    def "lockOrUnlockUser locks correctly"() {
        given:
        def req = new LockUserRequest(lock: true)

        when:
        def result = controller.lockOrUnlockUser(1L, req)

        then:
        1 * userService.toggleLockUser(1L, true)
        result.body.result == "User locked"
    }

    // ==============================
    // importUsers
    // ==============================
    def "importUsers creates multiple users"() {
        given:
        def reqs = [new UserCreationByAdminRequest(username: "a")]
        def user = new User(username: "a")
        def resp = new UserResponse(username: "a")
        userService.createUserByAdmin(_ as UserCreationByAdminRequest) >> user
        userMapper.userToUserResponse(user) >> resp

        when:
        def result = controller.importUsers(reqs)

        then:
        result.statusCode == HttpStatus.CREATED
        result.body*.username == ["a"]
    }

    // ==============================
    // admin detail + inactive instructors
    // ==============================
    def "getUserDetailForAdmin returns detail"() {
        given:
        def resp = new UserDetailResponse(username: "zz")
        userService.getUserDetailByAdmin(1L) >> resp

        when:
        def result = controller.getUserDetailForAdmin(1L)

        then:
        result.statusCode == HttpStatus.OK
        result.body.username == "zz"
    }

    def "getInactiveInstructors returns list"() {
        given:
        def list = [new UserDetailResponse(username: "instructor")]
        userService.getInactiveInstructors() >> list

        when:
        def result = controller.getInactiveInstructors()

        then:
        result*.username == ["instructor"]
    }

    // ==============================
    // activate/deactivate instructor
    // ==============================
    def "activateInstructor returns ApiResponse"() {
        when:
        def result = controller.activateInstructor(1L)

        then:
        1 * userService.activateInstructor(1L)
        result.code == 200
        result.message == "Instructor activated successfully"
    }

    def "deactivateInstructor returns ApiResponse"() {
        when:
        def result = controller.deactivateInstructor(1L)

        then:
        1 * userService.deactivateInstructor(1L)
        result.code == 200
        result.message == "Instructor deactivated successfully"
    }
}

