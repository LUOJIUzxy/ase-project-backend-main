package com.tum.ase.service;

import com.tum.ase.constant.ErrorMsg;
import com.tum.ase.constant.OrderStatus;
import com.tum.ase.constant.UserRole;
import com.tum.ase.exception.notfound.UserNotFoundException;
import com.tum.ase.exception.box.HasBeenAssignedException;
import com.tum.ase.exception.user.*;
import com.tum.ase.model.AppUser;
import com.tum.ase.model.AppUserCredential;
import com.tum.ase.model.AppUserRequest;
import com.tum.ase.model.Order;
import com.tum.ase.repo.OrderRepository;
import com.tum.ase.repo.UserRepository;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

@Service
public class UserService {

//    @Autowired
    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final AuthService authService;

    @Autowired
    public UserService(UserRepository userRepository, OrderRepository orderRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.authService = authService;
    }

    public List<AppUser> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "_id"));
    }

    public AppUser getUserById(String id) {
        AppUser user = userRepository.findAppUserById(id);
        if (user == null) {
            throw new UserNotFoundException(ErrorMsg.USER_NOT_FOUND);
        }
        return user;
    }

    public AppUser createUser(AppUserRequest user, String jwt) {
        String username = checkUsernameLegal(user.getUsername());
        String email = checkEmailLegal(user.getEmail());
        String role = checkRoleLegal(user.getRole());
        String token = null;
        if (!role.equals(UserRole.DISPATCHER)) {
            RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('0','z').filteredBy(LETTERS, DIGITS).build();

            token = generator.generate(8).toLowerCase();
        }
        String password = checkPasswordEmpty(user.getPassword());

        AppUser created = new AppUser(username, email, token, role);
        AppUserCredential credential = authService.saveUserCredential(new AppUserCredential(username, email, password, role), jwt);
        if (credential != null) {
            // make sure the same user in two db has the same id
            created.setId(credential.getId());
            return userRepository.save(created);
        } else {
            throw new FailUpdateCredentialException(ErrorMsg.FAIL_TO_UPDATE_CREDENTIAL);
        }
    }

    public AppUser updateUser(String userId, AppUser user, String jwt) {
        if (userId == null) {
            throw new UserNotFoundException(ErrorMsg.USER_NOT_FOUND);
        }
        AppUser found = userRepository.findAppUserById(userId);
        if (found == null) {
            throw new UserNotFoundException(ErrorMsg.USER_NOT_FOUND);
        } else {
            // if this user is assigned to an active order, does not allow to update
            checkIfAssignedToActiveOrder(found);

            // Not allow to update username
//            if (user.getUsername() != null && !user.getUsername().equals(found.getUsername())) {
//                String username = checkUsernameLegal(user.getUsername());
//                found.setUsername(username);
//            }
            // Not allow to update email
//            if (user.getEmail() != null && !user.getEmail().equals(found.getEmail())) {
//                String email = checkEmailLegal(user.getEmail());
//                found.setEmail(email);
//            }
            if (user.getRole() != null && !user.getRole().equals(found.getRole())) {
                String role = checkRoleLegal(user.getRole());
                found.setRole(role);
            }
            if (!found.getRole().equals(UserRole.DISPATCHER) && (user.getRole() != null && !user.getToken().equals(found.getToken()))) {
                String token = checkTokenLegal(user.getToken());
                found.setToken(token);
            } else if (found.getRole().equals(UserRole.DISPATCHER)) {
                found.setToken(null);
            }

            // update credential first
            AppUserCredential credential = authService.updateUserCredential(userId,
                    new AppUserCredential(found.getUsername(), found.getEmail(), null, found.getRole()), jwt);
            if (credential != null) {
                userRepository.save(found);
            } else {
                throw new FailUpdateCredentialException(ErrorMsg.FAIL_TO_UPDATE_CREDENTIAL);
            }
        }
        return found;
    }

    public void deleteUsers(List<String> ids, String jwt) {
        // if this user is assigned to an active order, does not allow to update
        List<AppUser> users = new ArrayList<>();
        userRepository.findAllById(ids).forEach(users::add);

        if (users.size() != ids.size()) {
            // some user is not found
            throw new UserNotFoundException(ErrorMsg.DELETE_USER_NOT_FOUND);
        }

        for (AppUser user : users) {
            checkIfAssignedToActiveOrder(user);
        }

        authService.deleteUserCredentials(ids, jwt);
        userRepository.deleteAllById(ids);
    }

    private String checkUsernameLegal(String username) {
        // username must not be empty
        if (username == null || username.isEmpty() || username.isBlank()) {
            throw new IllegalUsernameException(ErrorMsg.USERNAME_EMPTY);
        }
        // username must be unique
        AppUser found = userRepository.findAppUserByUsername(username);
        if (found == null) {
            return username;
        } else {
            throw new IllegalUsernameException(String.format(ErrorMsg.USERNAME_NOT_UNIQUE, username));
        }
    }

    private String checkEmailLegal(String email) {
        // email must not be empty
        if (email == null || email.isEmpty() || email.isBlank()) {
            throw new IllegalEmailException(ErrorMsg.EMAIL_EMPTY);
        }
        // email must be unique
        AppUser found = userRepository.findAppUserByEmail(email);
        if (found == null) {
            return email;
        } else {
            throw new IllegalEmailException(String.format(ErrorMsg.EMAIL_NOT_UNIQUE, email));
        }
    }

    private String checkTokenLegal(String token) {
        // token must not be empty
        if (token == null || token.isEmpty() || token.isBlank()) {
            throw new IllegalTokenException(ErrorMsg.TOKEN_EMPTY);
        }
        // token must be unique
        AppUser found = userRepository.findAppUserByToken(token);
        if (found == null) {
            // unify the token style
            if (token.length() != 8) {
                throw new IllegalTokenException(ErrorMsg.TOKEN_MALFORMED);
            }
            for (int i = 0; i < token.length(); i++) {
                if ((Character.isLetterOrDigit(token.charAt(i)) == false)) {
                    throw new IllegalTokenException(ErrorMsg.TOKEN_MALFORMED);
                }
            }
            return token.toLowerCase();
        } else {
            throw new IllegalTokenException(ErrorMsg.TOKEN_NOT_UNIQUE);
        }
    }

    private String checkRoleLegal(String role) {
        return switch (role) {
            case UserRole.DISPATCHER, UserRole.DELIVERER, UserRole.CUSTOMER -> role;
            default -> throw new IllegalRoleException(String.format(ErrorMsg.ROLE_NOT_LEGAL, role));
        };
    }

    private String checkPasswordEmpty(String password) {
        if (password == null || password.isEmpty() || password.isBlank()) {
            throw new IllegalPasswordException(ErrorMsg.PASSWORD_EMPTY);
        }
        return password;
    }

    private void checkIfAssignedToActiveOrder(AppUser user) {
        List<Order> orders = new ArrayList<>();
        if (user.getRole().equals(UserRole.DELIVERER)) {
            orders = orderRepository.findOrdersByDelivererId(user.getId());
        } else if (user.getRole().equals(UserRole.CUSTOMER)) {
            orders = orderRepository.findOrdersByCustomerId(user.getId());
        }
        // find active orders
        orders = orders.stream().filter(order -> !order.getStatus().equals(OrderStatus.Finished)).collect(Collectors.toList());
        if (orders.size() > 0) {
            throw new HasBeenAssignedException(String.format(ErrorMsg.USER_HAS_BEEN_ASSIGNED, user.getUsername()));
        }
    }
}
