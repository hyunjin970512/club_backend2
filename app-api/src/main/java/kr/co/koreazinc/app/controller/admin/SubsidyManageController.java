package kr.co.koreazinc.app.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import kr.co.koreazinc.app.model.admin.SubsidyManageDto;
import kr.co.koreazinc.app.service.admin.SubsidyManageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subsidy/manage")
public class SubsidyManageController {

	private final SubsidyManageService subsidyManageService;
	
	@GetMapping
	public List<SubsidyManageDto.Row> list(@RequestParam("year") String year) {
		return subsidyManageService.list(year);
	}
	
	@PostMapping("/save")
	public void save(@RequestBody SubsidyManageDto.SaveRequest req) {
	  subsidyManageService.save(req.getYear(), req.getRows(), /*actorEmpNo*/ null);
	}
}
