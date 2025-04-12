package codeverse.com.web_be.service.UserService;

import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.service.IGenericService;

import java.util.Optional;

public interface IUserService extends IGenericService<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}