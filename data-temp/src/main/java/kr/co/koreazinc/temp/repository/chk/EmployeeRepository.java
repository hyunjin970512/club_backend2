package kr.co.koreazinc.temp.repository.chk;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.koreazinc.temp.model.chk.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

  Optional<Employee> findByEmpNoAndUseAtAndDeleteAt(String empNo, String useAt, String deleteAt);

}
