package kr.co.koreazinc.temp.repository.form;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.koreazinc.temp.model.entity.main.ClubInfo;

public interface ClubInfoRepository extends JpaRepository<ClubInfo, Long> {
}
