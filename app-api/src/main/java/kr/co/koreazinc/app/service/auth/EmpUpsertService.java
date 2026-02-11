package kr.co.koreazinc.app.service.auth;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.koreazinc.temp.model.entity.account.CoEmplBas;
import kr.co.koreazinc.temp.repository.auth.CoEmplBasRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpUpsertService {

  private final CoEmplBasRepository repo;

  @Transactional
  public void upsertFromSsoMap(Map<String, Object> me) {
    Job job = pickPrimaryJob(me);
    if (job.empNo == null) throw new IllegalStateException("SSO missing empNo");

    CoEmplBas emp = repo.findById(job.empNo)
    		  .orElseGet(() -> CoEmplBas.builder()
    		      .empNo(job.empNo)
    		      .pwd("1234")          // NOT NULL 컬럼 → 기본값
    		      .useAt("Y")
    		      .createDate(LocalDateTime.now())
    		      .createUser(job.empNo)
    		      .deleteAt("N")
    		      .emplRoleCd("10")     // 기본 권한
    		      .build()
    		  );


    emp.setUserId(str(me.get("userId")));
    emp.setCmpEmail(str(me.get("email")));
    emp.setNameKo(nvl(job.userKoNm, str(me.get("userKoNm")), str(me.get("userNm"))));

    emp.setCoCd(job.coCd);
    emp.setDeptCd(job.deptNm);
    emp.setPositionCd(job.posNm);
    emp.setUseAt("Y".equalsIgnoreCase(nvl(job.useYn, str(me.get("useYn")))) ? "Y" : "N");
    emp.setDeleteAt("N");

    repo.save(emp);
  }

  // =========================
  // SSO job 파싱 로직
  // =========================
  @SuppressWarnings("unchecked")
  private Job pickPrimaryJob(Map<String, Object> me) {
    Object jobObj = me.get("job");
    if (!(jobObj instanceof List<?> list) || list.isEmpty()) {
      return new Job(null);
    }

    Map<String, Object> picked = null;

    // 1) bassYn=Y 우선
    for (Object o : list) {
      if (o instanceof Map<?, ?>) {
        Map<String, Object> m = (Map<String, Object>) o;
        if ("Y".equalsIgnoreCase(str(m.get("bassYn")))) {
          picked = m;
          break;
        }
      }
    }

    // 2) 없으면 첫 번째
    if (picked == null && list.get(0) instanceof Map<?, ?>) {
      picked = (Map<String, Object>) list.get(0);
    }

    return new Job(picked);
  }

  // =========================
  // 내부 파싱용 클래스
  // =========================
  private static class Job {
    String empNo;
    String userKoNm;
    String coCd;
    String deptCd;
    String deptNm;
    String posCd;
    String posNm;
    String useYn;
    
    

    Job(Map<String, Object> m) {
      if (m == null) return;
      this.empNo = str(m.get("empNo"));
      this.userKoNm = str(m.get("userKoNm"));
      this.coCd = str(m.get("coCd"));
      this.deptCd = str(m.get("deptCd"));
      this.deptNm = str(m.get("deptNm"));
      this.posCd = str(m.get("posCd"));
      this.posNm = str(m.get("posNm"));
      this.useYn = str(m.get("useYn"));
    }
  }

  // =========================
  // util
  // =========================
  private static String str(Object v) {
    if (v == null) return null;
    String s = String.valueOf(v).trim();
    if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
    return s;
  }

  private static String nvl(String... xs) {
    for (String x : xs) {
      if (x != null && !x.isBlank()) return x;
    }
    return null;
  }
}
