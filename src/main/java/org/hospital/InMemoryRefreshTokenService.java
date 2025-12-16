//package org.hospital;
//
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class InMemoryRefreshTokenService implements RefreshTokenService {
//
//    private final Map<String, RefreshTokenRecord> byJti = new HashMap<>();
//    private final Map<String, String> latestJtiByUser = new HashMap<>();
//
//    @Override
//    public synchronized void saveNew(RefreshTokenRecord record) {
//        if (record == null || record.jti == null) return;
//        // 사용자당 1개 정책: 기존 최신값이 있으면 우선 폐기
//        String prevJti = latestJtiByUser.get(record.username);
//        if (prevJti != null) {
//            RefreshTokenRecord prev = byJti.get(prevJti);
//            if (prev != null) {
//                byJti.put(prevJti, new RefreshTokenRecord(prev.username, prev.jti, prev.issuedAt, prev.expiresAt, true, prev.userAgent, prev.ip));
//            }
//        }
//        byJti.put(record.jti, record);
//        latestJtiByUser.put(record.username, record.jti);
//    }
//
//    @Override
//    public synchronized void revoke(String jti) {
//        if (jti == null) return;
//        RefreshTokenRecord rec = byJti.get(jti);
//        if (rec != null) {
//            byJti.put(jti, new RefreshTokenRecord(rec.username, rec.jti, rec.issuedAt, rec.expiresAt, true, rec.userAgent, rec.ip));
//            // 최신 포인터가 이 jti를 가리키면 제거
//            String latest = latestJtiByUser.get(rec.username);
//            if (jti.equals(latest)) {
//                latestJtiByUser.remove(rec.username);
//            }
//        }
//    }
//
//    @Override
//    public synchronized boolean isValid(String username, String jti, Date expiresAt) {
//        if (username == null || jti == null || expiresAt == null) return false;
//        RefreshTokenRecord rec = byJti.get(jti);
//        if (rec == null) return false;
//        if (rec.revoked) return false;
//        if (!username.equals(rec.username)) return false;
//        if (expiresAt.before(new Date())) return false;
//        // 사용자당 최신 1개 정책: jti가 최신인지 확인 (재사용 방지)
//        String latest = latestJtiByUser.get(username);
//        return jti.equals(latest);
//    }
//}
