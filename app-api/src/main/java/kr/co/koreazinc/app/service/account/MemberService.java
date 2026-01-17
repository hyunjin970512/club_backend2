package kr.co.koreazinc.app.service.account;

import org.springframework.stereotype.Service;


import kr.co.koreazinc.temp.model.entity.account.Employee;
import kr.co.koreazinc.temp.model.entity.account.Meresponse;
import kr.co.koreazinc.temp.repository.account.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final EmployeeRepository employeeRepository;

  public Meresponse me(String empNo) {
	  
	  System.err.println("데이터 확인 : " + empNo);
	  
    Employee e = employeeRepository.findByEmpNoAndUseAtAndDeleteAt(empNo, "Y", "N")
        .orElseThrow(() -> new RuntimeException("Employee not found: " + empNo));

    return new Meresponse(
        e.getEmpNo(),
        e.getNameKo(),
        e.getCoCd(),
        e.getDeptCd(),
        e.getPositionCd(),
        e.getCmpEmail(),
        e.getConEmail(),
        e.getProxyEmail()
    );
  }
}
