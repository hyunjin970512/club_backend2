package kr.co.koreazinc.app.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.koreazinc.app.model.admin.FeeDeductionDto;
import kr.co.koreazinc.app.service.admin.FeeDeductionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/fee-deductions")
public class FeeDeductionController {

  private final FeeDeductionService service;

  @GetMapping
  public List<FeeDeductionDto.Row> list(@RequestParam("yyyymm") String yyyymm) {
    return service.getRows(yyyymm);
  }
}
