package kr.co.koreazinc.app.controller.form;

import org.springframework.web.bind.annotation.*;

import kr.co.koreazinc.app.model.form.ClubJoinRequestDto;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.form.ClubJoinService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/club")
public class ClubJoinController {

    private final ClubJoinService clubJoinService;
    private final CurrentUserService currentUser;
    
    @GetMapping("/join/check/{clubId}")
    public ClubJoinRequestDto.JoinCheckResponse check(@PathVariable("clubId") Long clubId) {
        return clubJoinService.checkJoinState(currentUser.empNoOrThrow(), clubId);
    }

    /** ✅ 프론트: GET /api/clubs/{clubId} */
    @GetMapping("/getClubInfo/{clubId}")
    public ClubJoinRequestDto.ClubSimpleResponse club(@PathVariable("clubId") Long clubId) {
        return clubJoinService.getClubSimple(clubId);
    }

    /** ✅ 프론트: GET /api/clubs/requests/{requestId} */
    @GetMapping("/requests/{requestId}")
    public ClubJoinRequestDto.Response request(@PathVariable Long requestId) {
        return clubJoinService.getRequest(currentUser.empNoOrThrow(), requestId);
    }

    /** ✅ 프론트: POST /api/clubs/requests */
    @PostMapping("/requests")
    public Long create(@RequestBody ClubJoinRequestDto.Create req) {
        return clubJoinService.createJoinRequest(currentUser.empNoOrThrow(), req);
    }

    /** ✅ 프론트: PUT /api/clubs/requests/{requestId} */
    @PutMapping("/requests/{requestId}")
    public void update(@PathVariable Long requestId, @RequestBody ClubJoinRequestDto.Update req) {
        clubJoinService.updateJoinRequest(currentUser.empNoOrThrow(), requestId, req);
    }
}
