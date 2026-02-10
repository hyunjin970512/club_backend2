package kr.co.koreazinc.temp.repository.push;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.koreazinc.temp.model.entity.push.PushSubscriptionApp;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionAppRepository extends JpaRepository<PushSubscriptionApp, Long> {

    List<PushSubscriptionApp> findByEmpNoAndActiveYn(String empNo, String activeYn);

    Optional<PushSubscriptionApp> findByToken(String token);
}

