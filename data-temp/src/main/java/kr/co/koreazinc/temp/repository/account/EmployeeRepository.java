package kr.co.koreazinc.temp.repository.account;

import kr.co.koreazinc.temp.model.entity.account.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmpNoAndUseAtAndDeleteAt(String empNo, String useAt, String deleteAt);

    Optional<Employee> findByCmpEmailAndUseAtAndDeleteAt(String cmpEmail, String useAt, String deleteAt);
    
    Optional<Employee> findByEmpNo(String empNo);

}

