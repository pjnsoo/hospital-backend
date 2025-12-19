package org.hospital.local;

import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LocalEncryptController {

    private final StringEncryptor jasyptStringEncryptor;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/local/enc/config/{plainText}")
    public String encryptJasypt(@PathVariable String plainText) {

        /* Jasypt 라이브러리를 사용한 평문 암호화 수행 */
        String encryptedText = jasyptStringEncryptor.encrypt(plainText);

        /* 결과 반환을 위한 Map 객체 생성 및 데이터 삽입 */
//        Map<String, String> response = new HashMap<>();
        return "ENC(%s)".formatted(encryptedText);
    }

    @GetMapping("/local/enc/password/{plainText}")
    public String encryptUser(@PathVariable String plainText) {
        return passwordEncoder.encode(plainText);
    }
}