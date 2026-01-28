package kr.co.koreazinc.app.service.form;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.koreazinc.app.model.form.ClubDto;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.comm.CommonDocService;
import kr.co.koreazinc.temp.model.entity.main.ClubCreateRequest;
import kr.co.koreazinc.temp.model.entity.main.ClubInfo;
import kr.co.koreazinc.temp.repository.form.ClubCreateRequestRepository;
import kr.co.koreazinc.temp.repository.form.ClubInfoRepository;
import kr.co.koreazinc.temp.repository.form.ClubFeeInfoRepository;
import kr.co.koreazinc.temp.repository.form.ClubUserInfoRepository;   // ✅ 추가
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubFormService {

    private final ClubInfoRepository clubInfoRepository;
    private final ClubCreateRequestRepository clubCreateRequestRepository;

    private final ClubFeeInfoRepository clubFeeInfoRepository; // ✅ 회비
    private final ClubUserInfoRepository clubUserInfoRepository; // ✅ 생성자 자동 가입

    private final CurrentUserService currentUser;
    private final CommonDocService commonDocService;

    @Transactional(readOnly = true)
    public ClubDto.FormResponse getForm(String status, Long clubId, String empNo) {
        ClubDto.FormResponse res = new ClubDto.FormResponse();
        res.setStatus(status);

        if ("CREATE".equalsIgnoreCase(status)) {
            res.setClubMasterId(empNo);
            return res;
        }

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

    // 동호회 신설
    @Transactional
    public Long createClub(ClubDto.SaveRequest dto, MultipartFile ruleFile, String empNo) throws IOException {

        // 1) club_info 생성
        ClubInfo clubInfo = new ClubInfo();
        clubInfo.setClubNm(dto.getClubNm());
        clubInfo.setClubMasterId(dto.getClubMasterId() != null ? dto.getClubMasterId() : empNo);
        clubInfo.setClubType(dto.getClubType());
        clubInfo.setStatus("10");
        clubInfo.setCreateUser(empNo);
        clubInfo.setCreateDate(LocalDateTime.now());

        clubInfo = clubInfoRepository.save(clubInfo);

        // ✅ (추가) 기본 회비 3건 자동 생성
        // club_fee_info 쪽 clubId 타입이 Integer라서 변환 (현재 구조 유지)
        Integer clubIdInt = clubInfo.getClubId().intValue();
        clubFeeInfoRepository.insertDefaultFees(clubIdInt, "SYSTEM");

        // ✅ (추가) 동호회 생성자 자동 가입(클럽유저 테이블 insert)
        // user_role_cd='00', status='20', emp_no=empNo, create_user=empNo
        clubUserInfoRepository.insertCreatorAsMember(clubInfo.getClubId(), empNo);

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

        // 3) 파일 있으면 업로드 + mapping(refId=requestId) + request.ruleFileId 반영
        if (ruleFile != null && !ruleFile.isEmpty()) {
            Long docNo = commonDocService.saveFile(ruleFile, "FR", empNo);
            commonDocService.saveMapping(req.getRequestId(), docNo, empNo);
            req.setRuleFileId(docNo);
            req.setUpdateUser(empNo);
            req.setUpdateDate(LocalDateTime.now());
            clubCreateRequestRepository.save(req);
        } else {
            if (dto.getRuleFileId() != null) {
                req.setRuleFileId(dto.getRuleFileId());
                req.setUpdateUser(empNo);
                req.setUpdateDate(LocalDateTime.now());
                clubCreateRequestRepository.save(req);
            }
        }

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

        clubInfo.setClubNm(dto.getClubNm());
        clubInfo.setClubType(dto.getClubType());
        clubInfo.setUpdateUser(empNo);
        clubInfo.setUpdateDate(LocalDateTime.now());

        clubInfoRepository.save(clubInfo);

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

        if (clubInfo.getCreateRequestId() == null || clubInfo.getCreateRequestId().isBlank()) {
            clubInfo.setCreateRequestId(String.valueOf(req.getRequestId()));
            clubInfoRepository.save(clubInfo);
        }
    }

    @Transactional
    public void updateClubWithFile(Long clubId, ClubDto.SaveRequest dto, MultipartFile ruleFile, String empNo) throws IOException {
        LocalDateTime now = LocalDateTime.now();

        ClubInfo clubInfo = clubInfoRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("club_info not found: " + clubId));

        clubInfo.setClubNm(dto.getClubNm());
        clubInfo.setClubType(dto.getClubType());
        clubInfo.setUpdateUser(empNo);
        clubInfo.setUpdateDate(now);
        clubInfoRepository.save(clubInfo);

        ClubCreateRequest req = clubCreateRequestRepository.findTopByClubIdOrderByRequestIdDesc(clubId)
            .orElseGet(() -> {
                ClubCreateRequest r = new ClubCreateRequest();
                r.setClubId(clubId);
                r.setClubMasterKey(clubInfo.getClubMasterId());
                r.setStatus("SAVED");
                r.setCreateUser(empNo);
                r.setCreateDate(now);
                return r;
            });

        req.setClubNm(dto.getClubNm());
        req.setClubDesc(dto.getClubDesc());
        req.setPurpose(dto.getPurpose());
        req.setMemberFileId(dto.getMemberFileId());
        req.setUpdateUser(empNo);
        req.setUpdateDate(now);

        if (req.getRequestId() == null) {
            req = clubCreateRequestRepository.save(req);
        }

        if (ruleFile != null && !ruleFile.isEmpty()) {
            Long docNo = commonDocService.saveFile(ruleFile, "CB", empNo);
            commonDocService.saveMapping(req.getRequestId(), docNo, empNo);
            req.setRuleFileId(docNo);
        }

        clubCreateRequestRepository.save(req);

        if (clubInfo.getCreateRequestId() == null || clubInfo.getCreateRequestId().isBlank()) {
            clubInfo.setCreateRequestId(String.valueOf(req.getRequestId()));
            clubInfo.setUpdateUser(empNo);
            clubInfo.setUpdateDate(now);
            clubInfoRepository.save(clubInfo);
        }
    }
}
