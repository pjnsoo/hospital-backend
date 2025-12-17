package org.hospital.local;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.web.bind.annotation.*;

@RestController
public class LocalEncryptController {

    private final StringEncryptor jasyptStringEncryptor;

    public LocalEncryptController(StringEncryptor jasyptStringEncryptor) {
        this.jasyptStringEncryptor = jasyptStringEncryptor;
    }

    @GetMapping("/local/encrypt/{plainText}")
    public String encrypt(@PathVariable String plainText) {

        /* Jasypt 라이브러리를 사용한 평문 암호화 수행 */
        String encryptedText = jasyptStringEncryptor.encrypt(plainText);

        /* 결과 반환을 위한 Map 객체 생성 및 데이터 삽입 */
//        Map<String, String> response = new HashMap<>();
        return "ENC(%s)".formatted(encryptedText);
    }
}