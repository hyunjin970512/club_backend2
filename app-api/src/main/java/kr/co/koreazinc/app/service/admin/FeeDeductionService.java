package kr.co.koreazinc.app.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.koreazinc.app.model.admin.FeeDeductionDto;
import kr.co.koreazinc.temp.repository.admin.FeeDeductionManageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeeDeductionService {

  private final FeeDeductionManageRepository repo;

  @Transactional(readOnly = true)
  public List<FeeDeductionDto.Row> getRows(String yyyymm) {
    return repo.selectRows(FeeDeductionDto.Row.class, yyyymm).fetch();
  }
}
