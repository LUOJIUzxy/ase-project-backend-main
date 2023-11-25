package com.tum.service;

import com.tum.constant.UserRole;
import com.tum.jwt.JweUtil;
import com.tum.model.AppUser;
import com.tum.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private JweUtil jweUtil;

    public List<AppUser> listUserCredentials() throws Exception {
        return appUserRepository.findAll(Sort.by(Sort.Direction.ASC, "_id"));
    }

    public AppUser getUserFromUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public AppUser saveUserCredential(AppUser user) throws Exception {
        String username = checkUsernameUnique(user.getUsername());
        String email = checkEmailUnique(user.getEmail());
        String role = checkRoleLegal(user.getRole());
        String password = checkPasswordEmpty(user.getPassword());
        AppUser created = new AppUser(username, email, password, role);
        return appUserRepository.save(created);
    }

    public AppUser updateUserCredential(String userId, AppUser user) throws Exception {
        AppUser found = appUserRepository.findAppUsersById(userId);
        if (found == null) {
            throw new RuntimeException("User not found");
        }
        // Not allow to update username
//        if (user.getUsername() != null && !user.getUsername().equals(found.getUsername())) {
//            String username = checkUsernameUnique(user.getUsername());
//            found.setUsername(username);
//        }
        // Not allow to update username
//        if (user.getEmail() != null && !user.getEmail().equals(found.getEmail())) {
//            String email = checkEmailUnique(user.getEmail());
//            found.setEmail(email);
//        }
        if (!found.getRole().equals(UserRole.BOX) && !user.getRole().equals(UserRole.BOX) && user.getRole() != null && !user.getRole().equals(found.getRole())) {
            // Box's role cannot be changed, user's role cannot be changed to box either
            String role = checkRoleLegal(user.getRole());
            found.setRole(role);
        }
        // only update password automatically for box
        if (user.getRole().equals(UserRole.BOX) && user.getPassword() != null && !user.getPassword().equals(found.getPassword())) {
            String password = checkPasswordEmpty(user.getPassword());
            found.setPassword(password);
        }

        appUserRepository.save(found);
        return found;
    }

    public AppUser resetPassword(String userId, AppUser user) throws Exception {
        AppUser found = appUserRepository.findAppUsersById(userId);
        if (found == null) {
            throw new RuntimeException("User not found");
        }

        if (user.getPassword() != null && !user.getPassword().equals(found.getPassword())) {
            String password = checkPasswordEmpty(user.getPassword());
            found.setPassword(password);
        }
        appUserRepository.save(found);
        return found;
    }

    public void deleteUserCredentials(List<String> ids) throws Exception{
        try {
            appUserRepository.deleteAllById(ids);
        } catch (Exception e) {
            throw e;
        }
    }

    private String checkUsernameUnique(String username) throws Exception {
        // username must be unique
        AppUser found = appUserRepository.findByUsername(username);
        if (found == null) {
            return username;
        } else {
            throw new Exception();
        }
    }

    private String checkEmailUnique(String email) throws Exception {
        // email must be unique
        AppUser found = appUserRepository.findByEmail(email);
        if (found == null) {
            return email;
        } else {
            throw new Exception();
        }
    }

    private String checkRoleLegal(String role) throws Exception {
        switch (role) {
            case UserRole.DISPATCHER:
            case UserRole.DELIVERER:
            case UserRole.CUSTOMER:
            case UserRole.BOX:
                return role;
            default:
                throw new Exception();
        }
    }

    private String checkPasswordEmpty(String password) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (password == null || password.isEmpty() || password.isBlank()) {
            throw new Exception();
        }
        return encoder.encode(password);
    }
}
