package kr.co.koreazinc.temp.repository.detail;
import static kr.co.koreazinc.temp.model.entity.main.QClubUserInfo.clubUserInfo;
import static kr.co.koreazinc.temp.model.entity.main.QClubJoinRequest.clubJoinRequest;
import static kr.co.koreazinc.temp.model.entity.detail.view.QVClubDetail.vClubDetail;
import static kr.co.koreazinc.temp.model.entity.detail.QClubFeeInfo.clubFeeInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.data.support.Query.Procedure;
import kr.co.koreazinc.temp.model.entity.detail.ClubDetail;
import kr.co.koreazinc.temp.model.entity.detail.ClubFeeInfo;
import kr.co.koreazinc.temp.model.entity.main.ClubUserInfo;
import kr.co.koreazinc.temp.model.entity.main.QClubUserInfo;

@Repository
@Transactional(readOnly = true)
public class ClubDetailRepository extends AbstractJpaRepository<ClubDetail, Integer> {
    
    public ClubDetailRepository(@Autowired List<EntityManager> entityManagers) {
        super(ClubDetail.class, entityManagers);
    }
    
    // 뷰 전용 조회 클래스 정의
    public class SelectViewQuery<DTO> extends Query.Select<DTO> {
    	public SelectViewQuery(JPAQuery<DTO> query) {
    		super(query);
    		this.addField("clubId", vClubDetail.clubId);
            this.addField("clubName", vClubDetail.clubName);
    	}
    	
    	public SelectViewQuery<DTO> eqCludId(Integer clubId) {
    		query.where(vClubDetail.clubId.eq(clubId));
    		return this;
    	}
    }
    
    /**
     * View를 사용하여 상세 정보를 조회 (기존 DTO 재사용)
     */
    public <T> SelectViewQuery<T> selectClubDetailView(Class<T> type) {
        return new SelectViewQuery<>(queryFactory
                .select(Projections.bean(type,
                        vClubDetail.clubId,
                        vClubDetail.clubName,
                        vClubDetail.description,
                        vClubDetail.president,
                        vClubDetail.establishedDate,
                        vClubDetail.clubStatus,
                        vClubDetail.memberCnt,
                        vClubDetail.requestCnt,
                        vClubDetail.ruleFileId,
                        vClubDetail.requestId,
                        vClubDetail.positionCd,
                        vClubDetail.companyNm,
                        vClubDetail.purpose,
                        vClubDetail.deptCd,
                        vClubDetail.requestNm,
                        vClubDetail.requestEmpNo,
                        vClubDetail.docFileNm,
                        vClubDetail.downloadUrl
                ))
                .from(vClubDetail));
    }
   
   
   
   /**
    * 동호회 회비 조회하기
    */
   public <T> List<T> selectClubFeeInfoList(Class<T> type, Integer clubId) {
	   // 최근 등록 3건 조회
	   List<T> result = queryFactory
	            .select(Projections.bean(type,
	                    clubFeeInfo.feeId.intValue().as("feeId"),
	                    clubFeeInfo.clubId.intValue().as("clubId"),
	                    clubFeeInfo.positionCd,
	                    Expressions.stringTemplate("fn_get_common_code({0}, {1})", 
	                                             "POSITION_CD", clubFeeInfo.positionCd).as("positionNm"),
	                    clubFeeInfo.amount.as("positionAmt")
	            ))
	            .from(clubFeeInfo)
	            .where(clubFeeInfo.clubId.eq(clubId))
	            .orderBy(clubFeeInfo.feeId.desc())
	            .limit(3)
	            .fetch();
	   
	   // PositionCd 기준으로 정렬
	   result.sort((o1, o2) -> {
	        try {
	            String pos1 = (String) o1.getClass().getMethod("getPositionCd").invoke(o1);
	            String pos2 = (String) o2.getClass().getMethod("getPositionCd").invoke(o2);
	            return pos2.compareTo(pos1); // 내림차순
	        } catch (Exception e) {
	            return 0;
	        }
	    });
	   
	   return result;
   }
   
   /**
    * 동호회 회비 수정하기
    */
  public void saveClubFeeInfo(Integer clubId, String positionCd, Integer amount, String empNo) {
	  ClubFeeInfo fee = ClubFeeInfo.builder()
			  .clubId(clubId)
			  .positionCd(positionCd)
			  .amount(amount)
			  .createUser(empNo)
			  .createDate(LocalDateTime.now())
			  .updateUser(empNo)
			  .updateDate(LocalDateTime.now())
			  .build();
	  
	  entityManager.persist(fee);
  }
  
  /**
   * 동호회 권한 정보 조회하기
   */
  public Optional<ClubUserInfo> getClubAuthInfo(Long clubId, String empNo) {
	 return Optional.ofNullable(
			 queryFactory
			 	.selectFrom(clubUserInfo)
			 	.where(
			 		clubUserInfo.clubId.eq(clubId),
			 		clubUserInfo.empNo.eq(empNo)
			 	)
			 	.fetchOne()
		);
  }
  
  public Optional<String> getJoinRequestStatus(Long clubId, String empNo) {
	  return Optional.ofNullable(
		        queryFactory
		            .select(clubJoinRequest.status)
		            .from(clubJoinRequest)
		            .where(
		                clubJoinRequest.clubId.eq(clubId),
		                clubJoinRequest.requestUser.eq(empNo)
		            )
		            .orderBy(clubJoinRequest.requestId.desc())
		            .fetchFirst()
		    );
  }
  
  /**
   * 동호회 GW 상신 프로시저 호출
   */
  public class ClubGwAfterProcedure extends Procedure<Void> {
	  
	  public ClubGwAfterProcedure() {
		  // PostgreSQL 함수 호출
		  super(entityManager.createStoredProcedureQuery("public.sp_club_gw_after"));
		  
		  // 파라미터 등록
		  storedProcedure.registerParameter("p_club_id", Integer.class, ParameterMode.IN);
		  storedProcedure.registerStoredProcedureParameter("p_request_id", Integer.class, ParameterMode.IN);
          storedProcedure.registerStoredProcedureParameter("p_gwdocno", String.class, ParameterMode.IN);
          storedProcedure.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);
          storedProcedure.registerStoredProcedureParameter("p_user_emp_no", String.class, ParameterMode.IN);
	  }
	  
	  public ClubGwAfterProcedure setParams(int clubId, int requestId, String gwDocNo, String status, String userId) {
		  storedProcedure.setParameter("p_club_id", clubId);
          storedProcedure.setParameter("p_request_id", requestId);
          storedProcedure.setParameter("p_gwdocno", gwDocNo);
          storedProcedure.setParameter("p_status", status);
          storedProcedure.setParameter("p_user_emp_no", userId);
          return this;
	  }
  }
  
  /**
   * Service에서 호출할 실행 메서드
   */
  @Transactional
  public void executeClubGwAfter(Integer clubId, Integer requestId, String gwDocNo, String status, String userId) {
	  new ClubGwAfterProcedure()
	  	.setParams(clubId, requestId, gwDocNo, status, userId)
	  	.execute();
  }
}