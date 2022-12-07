package com.cryptocurrency.service.user;

import com.cryptocurrency.entity.domain.Authority;
import com.cryptocurrency.entity.domain.Profile;
import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.entity.dto.ProfileDto;
import com.cryptocurrency.entity.enums.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    Set<Authority> getAuthoritiesByUsername(String username);

    User changeAuthData(User user, String password);
    Role getMaxRole(User user);
    Set<Authority> setAllRoles(Role role);
    Role getMaxRole(Set<Authority> roles);
    User addUserRole(User user, Role role);
    User removeUserRole(User user, Role role);
    boolean registerUser(User user, String pass, String name, Role role, String email);

    Profile findProfileByUser(User user);
    Profile editProfileData(User user, ProfileDto profileDto);
    String editProfileImage(String imageId, User user);

    Optional<User> find(String username);
    List<User> findByRole(Role role);

    boolean inactivate(User user);
    boolean activate(User user);

    User save(User user);
    User save(User user, Role role);
    boolean remove(User user);
}
