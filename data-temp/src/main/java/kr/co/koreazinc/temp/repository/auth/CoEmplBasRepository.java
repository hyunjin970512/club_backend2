package kr.co.koreazinc.temp.repository.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.koreazinc.temp.model.entity.account.CoEmplBas;

public interface CoEmplBasRepository extends JpaRepository<CoEmplBas, String> {
    Optional<CoEmplBas> findByEmpNo(String empNo);
}
