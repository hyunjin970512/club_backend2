package kr.co.koreazinc.app.controller.form;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import kr.co.koreazinc.app.model.form.ClubDto;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.form.ClubFormService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clubs")
public class ClubFormController {

    private final ClubFormService clubFormService;
    
    private final CurrentUserService currentUser;

    @GetMapping("/form")
    public ClubDto.FormResponse form(
            @RequestParam String status,
            @RequestParam(required = false) Long clubId,
            Authentication authentication
    ) {
        return clubFormService.getForm(status, clubId, currentUser.empNoOrThrow());
    }

    @PostMapping
    public Long create(@RequestBody ClubDto.SaveRequest req, Authentication authentication) {
        return clubFormService.createClub(req, currentUser.empNoOrThrow());
    }

    @PutMapping("/{clubId}")
    public void update(
            @PathVariable Long clubId,
            @RequestBody ClubDto.SaveRequest req,
            Authentication authentication
    ) {
        clubFormService.updateClub(clubId, req, currentUser.empNoOrThrow());
    }

}
