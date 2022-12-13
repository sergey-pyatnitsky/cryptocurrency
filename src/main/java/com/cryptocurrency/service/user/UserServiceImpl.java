package com.cryptocurrency.service.user;

import com.cryptocurrency.entity.domain.Authority;
import com.cryptocurrency.entity.domain.Profile;
import com.cryptocurrency.entity.domain.User;
import com.cryptocurrency.entity.dto.ProfileDto;
import com.cryptocurrency.entity.enums.Role;
import com.cryptocurrency.repository.AuthorityRepository;
import com.cryptocurrency.repository.ProfileRepository;
import com.cryptocurrency.repository.UserRepository;
import com.cryptocurrency.service.email.EmailService;
import com.cryptocurrency.util.ValidationUtil;
import com.cryptocurrency.validation.RegexpValidator;
import com.cryptocurrency.validation.UserValidator;
import com.cryptocurrency.validation.entity.RegexpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private RegexpValidator regexpValidator;

    @Autowired
    private EmailService emailService;

    @Override
    public Set<Authority> getAuthoritiesByUsername(String username) {
        User user = userRepository.findById(username).orElse(null);
        if (user != null && user.getRoles() != null)
            return user.getRoles();
        return null;
    }

    @Override
    public User changeAuthData(User user, String password) {
        user.setPassword(password);
        ValidationUtil.validate(user, userValidator);
        user.setPassword("{bcrypt}" + (new BCryptPasswordEncoder()).encode(password));
        return userRepository.save(user);
    }

    @Override
    public User changeUserRole(User user, Role role) {
        user.setRoles(this.setAllRoles(role));
        return userRepository.save(user);
    }

    @Override
    public Role getMaxRole(User user) {
        user = userRepository.findById(user.getUsername()).orElse(null);
        if (user == null) return null;

        for (int roleLoop = 0; roleLoop < Role.values().length; ++roleLoop) {
            if (user.getRoles().contains(authorityRepository.findByName(Role.values()[roleLoop].name()).orElse(null)))
                return Role.values()[roleLoop];
        }

        return null;
    }

    @Override
    public Set<Authority> setAllRoles(Role role) {
        Set<Authority> authorities = new HashSet<>();
        for (int roleLoop = Role.values().length - 1; roleLoop >= 0; --roleLoop) {
            int finalRoleLoop = roleLoop;
            authorities.add(authorityRepository.findByName(role.name())
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name(Role.values()[finalRoleLoop].name()).build())));
            if (Role.values()[roleLoop].equals(role)) break;
        }
        return authorities;
    }

    @Override
    public Role getMaxRole(Set<Authority> roles) {
        for (int roleLoop = 0; roleLoop < Role.values().length; ++roleLoop) {
            if (roles.contains(authorityRepository.findByName(Role.values()[roleLoop].name()).orElse(null)))
                return Role.values()[roleLoop];
        }
        return null;
    }

    @Override
    public User addUserRole(User user, Role role) {
        user = userRepository.findById(user.getUsername()).orElse(null);
        if (user != null) {
            user.getRoles().add(authorityRepository.findByName(role.name())
                    .orElse(authorityRepository.save(Authority.builder().name(role.name()).build())));
        }
        return null;
    }

    @Override
    public User removeUserRole(User user, Role role) {
        user = userRepository.findById(user.getUsername()).orElse(null);
        if (user != null)
            user.getRoles().removeIf(authority -> authority.getName().equals(role.name()));
        return null;
    }

    @Override
    public boolean registerUser(User user, String pass, String name, Role role, String email) {
        ValidationUtil.validate(user, userValidator);
        ValidationUtil.validate(RegexpEntity.RegexpValidationBuilder.anApiError().withEmail(email).build(), regexpValidator);
        user.setPassword("{bcrypt}" + (new BCryptPasswordEncoder()).encode(pass));
        if (!userRepository.existsById(user.getUsername())) {
            user = saveAllRoles(user, role);
            user.setActive(true);
            Profile profile = Profile.builder().name(name).user(user).email(email).build();
            profileRepository.save(profile);
            emailService.sendSimpleEmail(email,
                    "Welcome to the personnel accounting system",
                    "You have successfully registered in the cryptocurrency system.");
            return true;
        } else return false;
    }

    @Override
    public Profile findProfileByUser(User user) {
        return profileRepository.findProfileByUser(user);
    }

    @Override
    public Profile editProfileData(User user, ProfileDto profileDto) {
        Profile profile = profileRepository.findProfileByUser(user);
        profile.setAddress(profileDto.getAddress());
        profile.setName(profileDto.getName());
        profile.setCountry(profileDto.getCountry());
        profile.setEmail(profileDto.getEmail());
        profile.setPhone(profileDto.getPhone());
        return profileRepository.save(profile);
    }

    @Override
    public String editProfileImage(String imageId, User user) {
        Profile profile = profileRepository.findProfileByUser(user);
        profile.setImageId(imageId);
        return profileRepository.save(profile).getImageId();
    }

    @Override
    public Optional<User> find(String username) {
        return userRepository.findById(username);
    }

    @Override
    public List<Profile> findAllProfile() {
        return profileRepository.findAll();
    }

    @Override
    public List<Profile> findAllProfileByActiveStatus(boolean isActive) {
        return profileRepository.findProfilesByUser_isActive(isActive);
    }

    @Override
    public List<Profile> findByRole(Role role) {
        Authority authority = authorityRepository.findByName(role.name()).orElse(null);
        if (authority == null) return null;

        List<User> users = userRepository.findUsersByRolesContains(authority);
        if (users == null) return null;

        for (Role roleLoop : Role.values()) {
            if (roleLoop.equals(role)) break;
            users = users.stream()
                    .filter(user -> !user.getRoles().contains(authorityRepository.findByName(roleLoop.name()).orElse(null)))
                    .collect(Collectors.toList());
        }

        return users.stream().map(
                user -> profileRepository.findProfileByUser(user)
        ).collect(Collectors.toList());
    }

    @Override
    public boolean changeActiveStatus(User user, boolean isActive) {
        return userRepository.changeActiveStatus(user.getUsername(), isActive) == 1;
    }

    @Override
    public boolean inactivate(User user) {
        int isActive = userRepository.changeActiveStatus(user.getUsername(), false);
        return isActive == 1;
    }

    @Override
    public boolean activate(User user) {
        int isActive = userRepository.changeActiveStatus(user.getUsername(), true);
        return isActive == 1;
    }

    @Override
    public User save(User user) {
        ValidationUtil.validate(user, userValidator);
        return userRepository.save(user);
    }

    @Override
    public User save(User user, Role role) {
        ValidationUtil.validate(user, userValidator);
        user = userRepository.findById(user.getUsername()).orElse(user);

        Authority authority;
        if (authorityRepository.findByName(role.name()).isPresent())
            authority = authorityRepository.findByName(role.name()).orElse(null);
        else authority = Authority.builder().name(role.name()).build();

        if (user.getRoles() == null) {
            Set<Authority> authorities = new HashSet<>();
            authorities.add(authority);
            user.setRoles(authorities);
        } else
            user.getRoles().add(authority);
        user = userRepository.save(user);
        saveAllRoles(user, role);
        return user;
    }

    private User saveAllRoles(User user, Role role) {
        if (user.getRoles() == null) user.setRoles(new HashSet<>());
        for (int roleLoop = Role.values().length - 1; roleLoop >= 0; --roleLoop) {
            int finalRoleLoop = roleLoop;
            user.getRoles().add(authorityRepository.findByName(role.name())
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name(Role.values()[finalRoleLoop].name()).build())));
            if (Role.values()[roleLoop].equals(role)) break;
        }
        return userRepository.save(user);
    }

    @Override
    public boolean remove(User user) {
//        if (authorityRepository.removeByName(user.getUsername()) == 1) {
        if(profileRepository.removeByUser(user) == 1)
            return userRepository.removeByUsername(user.getUsername()) == 1;
//        }
        return false;
    }
}
