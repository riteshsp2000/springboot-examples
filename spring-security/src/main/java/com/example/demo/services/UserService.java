package com.example.demo.services;

import com.example.demo.dao.UserDetailsDao;
import com.example.demo.entities.ConfirmationTokenEntity;
import com.example.demo.entities.UserDetailsEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";

    private final UserDetailsDao userDetailsDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userDetailsDao.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(UserDetailsEntity userDetailsEntity) {
        boolean userExists = userDetailsDao
                .findByEmail(userDetailsEntity.getEmail())
                .isPresent();

        if (userExists) {
            // TODO check of attributes are the same and
            // TODO if email not confirmed send confirmation email.

            throw new IllegalStateException("email already taken");
        }

        String encodedPassword = bCryptPasswordEncoder
                .encode(userDetailsEntity.getPassword());

        userDetailsEntity.setPassword(encodedPassword);

        userDetailsDao.save(userDetailsEntity);

        String token = UUID.randomUUID().toString();

        ConfirmationTokenEntity confirmationTokenEntity = new ConfirmationTokenEntity(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                userDetailsEntity
        );

        confirmationTokenService.saveConfirmationToken(confirmationTokenEntity);

//        TODO: SEND EMAIL

        return token;
    }

    public int enableAppUser(String email) {
        return userDetailsDao.enableAppUser(email);
    }
}
