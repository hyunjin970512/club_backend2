package kr.co.koreazinc.temp.repository.form;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.koreazinc.temp.model.entity.main.ClubCreateRequest;

public interface ClubCreateRequestRepository extends JpaRepository<ClubCreateRequest, Long> {
    Optional<ClubCreateRequest> findTopByClubIdOrderByRequestIdDesc(Long clubId);
}
