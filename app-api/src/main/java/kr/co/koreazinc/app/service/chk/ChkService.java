package kr.co.koreazinc.app.service.chk;

import org.springframework.stereotype.Service;

import kr.co.koreazinc.app.service.security.dto.Meresponse;
import kr.co.koreazinc.temp.model.chk.Employee;
import kr.co.koreazinc.temp.repository.chk.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChkService {
	
	private final EmployeeRepository employeeRepository;

	public Meresponse me(String empNo) {
	  
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
			e.getProxyEmail(),
			null, null
		);
	  }
	
}

