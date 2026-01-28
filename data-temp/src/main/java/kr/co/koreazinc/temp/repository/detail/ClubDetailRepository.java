package kr.co.koreazinc.temp.repository.detail;
import static kr.co.koreazinc.temp.model.entity.main.QClubUserInfo.clubUserInfo;
import static kr.co.koreazinc.temp.model.entity.main.QClubJoinRequest.clubJoinRequest;
import static kr.co.koreazinc.temp.model.entity.detail.view.QVClubDetail.vClubDetail;
import static kr.co.koreazinc.temp.model.entity.detail.QClubFeeInfo.clubFeeInfo;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.detail.ClubDetail;
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
                        vClubDetail.docFileNm,
                        vClubDetail.downloadUrl
                ))
                .from(vClubDetail));
    }
   
   
   
   /**
    * 동호회 회비 조회하기
    */
   public <T> List<T> selectClubFeeInfoList(Class<T> type, Integer clubId) {
	   return queryFactory
	            .select(Projections.bean(type,
	            		clubFeeInfo.feeId.intValue().as("feeId"),
	            		clubFeeInfo.clubId.intValue().as("clubId"),
	                    clubFeeInfo.positionCd,
	                    Expressions.stringTemplate("fn_get_common_code({0}, {1})", 
	                                             "POSITION_CD", clubFeeInfo.positionCd).as("positionNm"),
	                    clubFeeInfo.amount.as("positionAmt")
	            ))
	            .from(clubFeeInfo)
	            .where(clubFeeInfo.clubId.eq((int) clubId.longValue()))
	            .orderBy(clubFeeInfo.positionCd.desc())
	            .fetch();
   }
   
   /**
    * 동호회 회비 수정하기
    */
  public long updateClubFeeInfo(Integer clubId, String positionCd, Integer amount, String empNo) { 
	   return queryFactory
               .update(clubFeeInfo)
               .set(clubFeeInfo.amount, amount)
               .set(clubFeeInfo.updateUser, empNo)
               .set(clubFeeInfo.updateDate, java.time.LocalDateTime.now())
               .where(clubFeeInfo.clubId.eq((int) clubId.longValue())
                       .and(clubFeeInfo.positionCd.eq(positionCd)))
               .execute();
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
		            .fetchOne()
		    );
  }
}