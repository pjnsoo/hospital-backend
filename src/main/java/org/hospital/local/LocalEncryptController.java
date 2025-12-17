package org.hospital.local;

import org.hospital.model.mapper.UserSessionMapper;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LocalEncryptController {

    private final StringEncryptor jasyptStringEncryptor;

    @Autowired
    UserSessionMapper userSessionMapper;

    public LocalEncryptController(StringEncryptor jasyptStringEncryptor) {
        this.jasyptStringEncryptor = jasyptStringEncryptor;
    }

    @PostMapping("/local/encrypt")
    public Map<String, String> encrypt(@RequestBody Map<String, String> request) {
        userSessionMapper.sel(null);
        String plainText = request.get("plainText");
        String encryptedText = jasyptStringEncryptor.encrypt(plainText);

        Map<String, String> response = new HashMap<>();
        response.put("plainText", plainText);
        response.put("encryptedText", "ENC(" + encryptedText + ")");
        return response;
    }
}