package com.biit.factmanager.security;

import com.biit.server.exceptions.BadRequestException;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.CreateUserRequest;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import com.biit.server.security.rest.exceptions.InvalidPasswordException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AuthenticatedUserProvider implements IAuthenticatedUserProvider {

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final Collection<? extends GrantedAuthority> AUTHORITIES = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
    );

    private static int idCounter = 1;

    private final Collection<IAuthenticatedUser> usersOnMemory = new ArrayList<>();


    @Override
    public Optional<IAuthenticatedUser> findByUsername(String username) {
        return usersOnMemory.stream().filter(user -> Objects.equals(username, user.getUsername())).findAny();
    }

    @Override
    public Optional<IAuthenticatedUser> findByUsername(String username, String applicationName) {
        return findByUsername(username);
    }

    @Override
    public Optional<IAuthenticatedUser> findByUID(String uid) {
        return usersOnMemory.stream().filter(user -> Objects.equals(uid, user.getUID())).findAny();
    }

    @Override
    public IAuthenticatedUser create(CreateUserRequest createUserRequest) {
        return createUser(createUserRequest.getUsername(), createUserRequest.getUniqueId(), createUserRequest.getName(),
                createUserRequest.getLastname(), createUserRequest.getPassword());
    }

    public void clear() {
        usersOnMemory.clear();
    }

    public IAuthenticatedUser createUser(String username, String uniqueId, String name, String lastName, String password) {
        if (findByUsername(username).isPresent()) {
            throw new BadRequestException(this.getClass(), "Username exists!");
        }

        final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUsername(username);
        authenticatedUser.setAuthorities(AUTHORITIES);
        authenticatedUser.setUID(uniqueId);
        authenticatedUser.setName(name);
        authenticatedUser.setLastname(lastName);
        if (password != null) {
            authenticatedUser.setPassword(encoder.encode(password));
        }

        usersOnMemory.add(authenticatedUser);

        return authenticatedUser;
    }

    @Override
    public IAuthenticatedUser updateUser(CreateUserRequest createUserRequest) {
        final AuthenticatedUser user = (AuthenticatedUser) usersOnMemory.stream().filter(iAuthenticatedUser ->
                iAuthenticatedUser.getUsername().equals(createUserRequest.getUsername())).findAny().orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "User with username '" + createUserRequest.getUsername() + "' does not exists"));
        user.setName(createUserRequest.getName());
        user.setLastname(createUserRequest.getLastname());
        return user;
    }

    @Override
    public void updatePassword(String username, String oldPassword, String newPassword) {
        final IAuthenticatedUser user = usersOnMemory.stream().filter(iAuthenticatedUser -> iAuthenticatedUser.getUsername().equals(username))
                .findAny().orElseThrow(() ->
                        new UserNotFoundException(this.getClass(), "User with username '" + username + "' does not exists"));

        //Check old password.
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException(this.getClass(), "Provided password is incorrect!");
        }

        //Update new password.
        user.setPassword(newPassword);
    }


    @Override
    public Collection<IAuthenticatedUser> findAll() {
        return usersOnMemory;
    }

    @Override
    public boolean deleteUser(String name, String username) {
        return delete(findByUsername(username).orElse(null));
    }

    @Override
    public boolean delete(IAuthenticatedUser authenticatedUser) {
        return usersOnMemory.remove(authenticatedUser);
    }

    @Override
    public Set<String> getRoles(String username, String organizationName, String application) {
        return null;
    }


    @Override
    public long count() {
        return usersOnMemory.size();
    }
}
