package kr.co.koreazinc.app.service.form;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubFormService {

    private final ClubInfoRepository clubInfoRepository;
    private final ClubCreateRequestRepository clubCreateRequestRepository;
    
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
            commonDocService.saveMapping(req.getRequestId(), docNo, empNo); // ✅ refId = requestId
            req.setRuleFileId(docNo);
            req.setUpdateUser(empNo);
            req.setUpdateDate(LocalDateTime.now());
            clubCreateRequestRepository.save(req);
        } else {
            // 파일 없이도 dto로 ruleFileId 들어온 경우가 있을 수 있으니 반영(옵션)
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
    
    /**
     * ✅ 수정: club_info 업데이트 + 최신 request 업데이트
     * ruleFile 있으면 업로드 + mapping(refId=requestId) + ruleFileId 교체
     */
    @Transactional
    public void updateClubWithFile(Long clubId, ClubDto.SaveRequest dto, MultipartFile ruleFile, String empNo) throws IOException {
        LocalDateTime now = LocalDateTime.now();

        ClubInfo clubInfo = clubInfoRepository.findById(clubId)
            .orElseThrow(() -> new IllegalArgumentException("club_info not found: " + clubId));

        // 1) club_info 업데이트
        clubInfo.setClubNm(dto.getClubNm());
        clubInfo.setClubType(dto.getClubType());
        clubInfo.setUpdateUser(empNo);
        clubInfo.setUpdateDate(now);
        clubInfoRepository.save(clubInfo);

        // 2) 최신 request 가져오거나 없으면 생성
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

        // 3) 텍스트 업데이트
        req.setClubNm(dto.getClubNm());
        req.setClubDesc(dto.getClubDesc());
        req.setPurpose(dto.getPurpose());
        req.setMemberFileId(dto.getMemberFileId());
        req.setUpdateUser(empNo);
        req.setUpdateDate(now);

        // 4) request가 신규인 경우 먼저 저장해서 requestId 확보
        if (req.getRequestId() == null) {
            req = clubCreateRequestRepository.save(req);
        }

        // 5) 파일 있으면 업로드 + mapping(refId=requestId) + ruleFileId 교체
        if (ruleFile != null && !ruleFile.isEmpty()) {
            Long docNo = commonDocService.saveFile(ruleFile, "CB", empNo);
            commonDocService.saveMapping(req.getRequestId(), docNo, empNo);
            req.setRuleFileId(docNo);
        } else {
            // 파일 안 왔으면 기존 유지 (dto.ruleFileId로 덮어쓰지 않음)
            // 단, dto.ruleFileId를 일부러 보내는 구조라면 여기서 반영하도록 변경 가능
        }

        clubCreateRequestRepository.save(req);

        // 6) club_info.create_request_id 동기화(없으면 채움)
        if (clubInfo.getCreateRequestId() == null || clubInfo.getCreateRequestId().isBlank()) {
            clubInfo.setCreateRequestId(String.valueOf(req.getRequestId()));
            clubInfo.setUpdateUser(empNo);
            clubInfo.setUpdateDate(now);
            clubInfoRepository.save(clubInfo);
        }
    }
    
}
