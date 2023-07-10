package com.example.demo.services;

import com.example.demo.dao.ConfirmationTokenDao;
import com.example.demo.entities.ConfirmationTokenEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenDao confirmationTokenDao;
    public void saveConfirmationToken(ConfirmationTokenEntity token) {
        confirmationTokenDao.save(token);
    }

    public Optional<ConfirmationTokenEntity> getToken(String token) {
        return confirmationTokenDao.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return confirmationTokenDao.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
