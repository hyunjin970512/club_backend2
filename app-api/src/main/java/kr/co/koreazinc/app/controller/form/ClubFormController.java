package kr.co.koreazinc.app.controller.form;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import kr.co.koreazinc.app.model.form.ClubDto;
import kr.co.koreazinc.app.service.account.CurrentUserService;
import kr.co.koreazinc.app.service.form.ClubFormService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/club")
public class ClubFormController {

    private final ClubFormService clubFormService;
    
    private final CurrentUserService currentUser;

    @GetMapping("/form")
    public ClubDto.FormResponse form(
    		@RequestParam(name = "status") String status,
    		@RequestParam(name = "clubId", required = false) Long clubId,
            Authentication authentication
    ) {
        return clubFormService.getForm(status, clubId, currentUser.empNoOrThrow());
    }

    // 동호회 신설
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long create(
			@RequestParam(name = "clubNm") String clubNm,
			@RequestParam(name = "clubDesc") String clubDesc,
			@RequestParam(name = "purpose") String purpose,
			@RequestParam(name = "clubType") String clubType,
			@RequestParam(name = "clubMasterId", required = false) String clubMasterId,
			@RequestPart(name = "ruleFile", required = false) MultipartFile ruleFile,
			Authentication authentication
		) throws IOException {
    	
		ClubDto.SaveRequest req = new ClubDto.SaveRequest();
		req.setClubNm(clubNm);
		req.setClubDesc(clubDesc);
		req.setPurpose(purpose);
		req.setClubType(clubType);
		req.setClubMasterId(clubMasterId); // 없어도 됨 (서비스에서 empNo로 박아도 됨)
    	
        return clubFormService.createClub(req, ruleFile, currentUser.empNoOrThrow());
    }

    // 동호회 수정
    @PutMapping(value = "/{clubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void update(
			@PathVariable(name = "clubId") Long clubId,
			@RequestParam(name = "clubNm") String clubNm,
			@RequestParam(name = "clubDesc") String clubDesc,
			@RequestParam(name = "purpose") String purpose,
			@RequestParam(name = "clubType") String clubType,
			@RequestParam(name = "clubMasterId", required = false) String clubMasterId,
			@RequestPart(name = "ruleFile", required = false) MultipartFile ruleFile,
			Authentication authentication
    ) throws IOException {
    	
		ClubDto.SaveRequest req = new ClubDto.SaveRequest();
		req.setClubNm(clubNm);
		req.setClubDesc(clubDesc);
		req.setPurpose(purpose);
		req.setClubType(clubType);
		req.setClubMasterId(clubMasterId);
		
		clubFormService.updateClubWithFile(clubId, req, ruleFile, currentUser.empNoOrThrow());
    }

}
