package kr.co.koreazinc.app.service.form;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.koreazinc.app.model.form.ClubJoinRequestDto;
import kr.co.koreazinc.temp.model.entity.main.ClubJoinRequest;
import kr.co.koreazinc.temp.repository.form.ClubJoinRequestRepository;
import kr.co.koreazinc.temp.repository.form.ClubRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubJoinService {

    private static final String REQUEST_STATUS = "10"; // 신청

    private final ClubRepository clubRepository;
    private final ClubJoinRequestRepository clubJoinRequestRepository;
    
    
    @Transactional(readOnly = true)
    public ClubJoinRequestDto.JoinCheckResponse checkJoinState(String empNo, Long clubId) {
        ClubJoinRequestDto.JoinCheckResponse res = new ClubJoinRequestDto.JoinCheckResponse();

        // 1) 이미 가입(20) 체크 (club_user_info)
        boolean joined = clubRepository.existsJoined(empNo, clubId); // 아래 레포 메서드 추가
        if (joined) {
            res.setJoined(true);
            res.setMessage("이미 가입된 동호회입니다.");
            return res;
        }

        // 2) 신청중 체크 (club_join_request)
        Long requestId = clubJoinRequestRepository.findRequestIdMineByClubId(empNo, clubId);
        if (requestId != null) {
            res.setRequested(true);
            res.setRequestId(requestId);
            res.setMessage("이미 가입 신청 중인 동호회입니다.");
            return res;
        }

        res.setMessage("OK");
        return res;
    }

    /** ✅ 동호회 단건 (동호회명 표기용) */
    @Transactional(readOnly = true)
    public ClubJoinRequestDto.ClubSimpleResponse getClubSimple(Long clubId) {
        ClubJoinRequestDto.ClubSimpleResponse res =
            clubRepository.selectClubSimple(ClubJoinRequestDto.ClubSimpleResponse.class, clubId);

        if (res == null) throw new IllegalArgumentException("동호회 없음 clubId=" + clubId);
        return res;
    }

    /** ✅ 가입신청 단건 (EDIT/VIEW 조회용) */
    @Transactional(readOnly = true)
    public ClubJoinRequestDto.Response getRequest(String empNo, Long requestId) {
        ClubJoinRequest entity = clubJoinRequestRepository.findOneMine(requestId, empNo);
        if (entity == null) throw new IllegalArgumentException("가입신청 없음(또는 본인 아님) requestId=" + requestId);

        ClubJoinRequestDto.Response res = new ClubJoinRequestDto.Response();
        res.setRequestId(entity.getRequestId());
        res.setClubId(entity.getClubId());
        res.setApplyReason(entity.getApplyReason());
        res.setStatus(entity.getStatus());
        res.setRequestUser(entity.getRequestUser());
        res.setRequestDate(entity.getRequestDate() == null ? null : entity.getRequestDate().toString());
        return res;
    }

    /** ✅ 가입 신청 생성 */
    @Transactional
    public Long createJoinRequest(String empNo, ClubJoinRequestDto.Create req) {
        Long clubId = req.getClubId();
        String reason = req.getApplyReason();

        if (clubId == null) throw new IllegalArgumentException("clubId required");
        if (reason == null || reason.trim().isEmpty()) throw new IllegalArgumentException("applyReason required");

        // 선검증(유니크 제약이 최종 방어)
        long cnt = clubJoinRequestRepository.countByClubIdAndRequestUser(clubId, empNo);
        if (cnt > 0) throw new IllegalStateException("이미 가입 신청한 동호회입니다.");

        ClubJoinRequest entity = new ClubJoinRequest();
        entity.setClubId(clubId);
        entity.setRequestUser(empNo);
        entity.setApplyReason(reason.trim());
        entity.setStatus(REQUEST_STATUS);
        entity.setRequestDate(LocalDateTime.now());
        entity.setCreateUser(empNo);
        entity.setCreateDate(LocalDateTime.now());

        try {
            clubJoinRequestRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("이미 가입 신청한 동호회입니다.");
        }

        return entity.getRequestId();
    }

    /** ✅ 가입 신청 수정 */
    @Transactional
    public void updateJoinRequest(String empNo, Long requestId, ClubJoinRequestDto.Update req) {
        String reason = req.getApplyReason();

        if (requestId == null) throw new IllegalArgumentException("requestId required");
        if (reason == null || reason.trim().isEmpty()) throw new IllegalArgumentException("applyReason required");

        ClubJoinRequest entity = clubJoinRequestRepository.findOneMine(requestId, empNo);
        if (entity == null) throw new IllegalArgumentException("가입신청 없음(또는 본인 아님) requestId=" + requestId);

        // clubId가 payload에 같이 오니, 이상한 수정 방지용으로 체크(선택)
        if (req.getClubId() != null && entity.getClubId() != null && !req.getClubId().equals(entity.getClubId())) {
            throw new IllegalArgumentException("clubId mismatch");
        }

        // 상태 제한 걸고 싶으면 여기서
        // if (!REQUEST_STATUS.equals(entity.getStatus())) throw new IllegalStateException("신청 상태만 수정 가능");

        entity.setApplyReason(reason.trim());
        entity.setUpdateUser(empNo);
        entity.setUpdateDate(LocalDateTime.now());
    }
}
