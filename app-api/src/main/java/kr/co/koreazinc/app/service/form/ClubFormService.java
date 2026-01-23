package kr.co.koreazinc.app.service.form;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.koreazinc.app.model.form.ClubDto;
import kr.co.koreazinc.temp.model.entity.main.ClubCreateRequest;
import kr.co.koreazinc.temp.model.entity.main.ClubInfo;
import kr.co.koreazinc.temp.repository.form.ClubCreateRequestRepository;
import kr.co.koreazinc.temp.repository.form.ClubInfoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubFormService {

    private final ClubInfoRepository clubInfoRepository;
    private final ClubCreateRequestRepository clubCreateRequestRepository;

    @Transactional(readOnly = true)
    public ClubDto.FormResponse getForm(String status, Long clubId, String empNo) {
        ClubDto.FormResponse res = new ClubDto.FormResponse();
        res.setStatus(status);

        if ("CREATE".equalsIgnoreCase(status)) {
            // 기본값만 세팅 (대표자=본인)
            res.setClubMasterId(empNo);
            return res;
        }

        // EDIT
        ClubInfo clubInfo = clubInfoRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("club_info not found: " + clubId));

        ClubCreateRequest req = clubCreateRequestRepository.findTopByClubIdOrderByRequestIdDesc(clubId)
            .orElse(null);

        res.setClubId(clubInfo.getClubId());
        res.setClubNm(req != null ? req.getClubNm() : clubInfo.getClubNm());
        res.setClubType(clubInfo.getClubType());
        res.setClubMasterId(clubInfo.getClubMasterId());

        if (req != null) {
            res.setClubDesc(req.getClubDesc());
            res.setPurpose(req.getPurpose());
            res.setRuleFileId(req.getRuleFileId());
            res.setMemberFileId(req.getMemberFileId());
        }

        return res;
    }

    @Transactional
    public Long createClub(ClubDto.SaveRequest dto, String empNo) {
        // 1) club_info 생성
        ClubInfo clubInfo = new ClubInfo();
        clubInfo.setClubNm(dto.getClubNm());
        clubInfo.setClubMasterId(dto.getClubMasterId() != null ? dto.getClubMasterId() : empNo);
        clubInfo.setClubType(dto.getClubType());
        clubInfo.setStatus("10");
        clubInfo.setCreateUser(empNo);
        clubInfo.setCreateDate(LocalDateTime.now());

        clubInfo = clubInfoRepository.save(clubInfo);

        // 2) club_create_request 생성
        ClubCreateRequest req = new ClubCreateRequest();
        req.setClubId(clubInfo.getClubId());
        req.setClubNm(dto.getClubNm());
        req.setClubDesc(dto.getClubDesc());
        req.setPurpose(dto.getPurpose());
        req.setClubMasterKey(clubInfo.getClubMasterId());
        req.setRuleFileId(dto.getRuleFileId());
        req.setMemberFileId(dto.getMemberFileId());
        req.setStatus("10");
        req.setCreateUser(empNo);
        req.setCreateDate(LocalDateTime.now());

        req = clubCreateRequestRepository.save(req);

        clubInfo.setCreateRequestId(String.valueOf(req.getRequestId()));
        clubInfo.setUpdateUser(empNo);
        clubInfo.setUpdateDate(LocalDateTime.now());
        clubInfoRepository.save(clubInfo);

        return clubInfo.getClubId();
    }

    @Transactional
    public void updateClub(Long clubId, ClubDto.SaveRequest dto, String empNo) {
        ClubInfo clubInfo = clubInfoRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("club_info not found: " + clubId));

        // club_info 업데이트 (필요한 것만)
        clubInfo.setClubNm(dto.getClubNm());
        clubInfo.setClubType(dto.getClubType());
        clubInfo.setUpdateUser(empNo);
        clubInfo.setUpdateDate(LocalDateTime.now());

        clubInfoRepository.save(clubInfo);

        // create_request는 "최신 1건" 업데이트 (없으면 새로 생성)
        ClubCreateRequest req = clubCreateRequestRepository.findTopByClubIdOrderByRequestIdDesc(clubId)
            .orElseGet(() -> {
                ClubCreateRequest r = new ClubCreateRequest();
                r.setClubId(clubId);
                r.setClubMasterKey(clubInfo.getClubMasterId());
                r.setStatus("SAVED");
                r.setCreateUser(empNo);
                r.setCreateDate(LocalDateTime.now());
                return r;
            });

        req.setClubNm(dto.getClubNm());
        req.setClubDesc(dto.getClubDesc());
        req.setPurpose(dto.getPurpose());
        req.setRuleFileId(dto.getRuleFileId());
        req.setMemberFileId(dto.getMemberFileId());
        req.setUpdateUser(empNo);
        req.setUpdateDate(LocalDateTime.now());

        clubCreateRequestRepository.save(req);

        // club_info.create_request_id 동기화 (옵션)
        if (clubInfo.getCreateRequestId() == null || clubInfo.getCreateRequestId().isBlank()) {
            clubInfo.setCreateRequestId(String.valueOf(req.getRequestId()));
            clubInfoRepository.save(clubInfo);
        }
    }
}
