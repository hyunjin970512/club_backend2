package kr.co.koreazinc.temp.repository.comm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import static kr.co.koreazinc.temp.model.entity.comm.QCommonMappingDoc.commonMappingDoc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQuery;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.data.support.Query;
import kr.co.koreazinc.temp.model.entity.comm.CommonMappingDoc;

@Repository
@Transactional(readOnly = true)
public class CommonMappingDocRepository extends AbstractJpaRepository<CommonMappingDoc, Long> {
	
	public CommonMappingDocRepository(@Autowired List<EntityManager> entityManagers) {
		super(CommonMappingDoc.class, entityManagers);
	}
	
	public class SelectQuery<DTO> extends Query.Select<DTO> {
        public SelectQuery(JPAQuery<DTO> query) {
            super(query);
            this.addField("boardId", commonMappingDoc.refId);
            this.addField("docNo", commonMappingDoc.docNo);
        }

        public SelectQuery<DTO> eqBoardId(Long refId) {
            query.where(commonMappingDoc.refId.eq(refId));
            return this;
        }

        public SelectQuery<DTO> eqDocNo(Long docNo) {
            query.where(commonMappingDoc.docNo.eq(docNo));
            return this;
        }
    }
	
	public SelectQuery<CommonMappingDoc> selectQuery() {
        return new SelectQuery<>(queryFactory.selectFrom(commonMappingDoc));
    }
	
    @Transactional
    public CommonMappingDoc insert(CommonMappingDoc entity) {
        return super.insert(entity);
    }

    @Transactional
    public void deleteByRefId(Long boardId) {
        queryFactory.delete(commonMappingDoc)
                    .where(commonMappingDoc.refId.eq(boardId))
                    .execute();
    }
    
    public void deleteMapFile(Long refId, Long docNo, String empNo) {
    	queryFactory.update(commonMappingDoc)
        .set(commonMappingDoc.deleteYn, "Y")
        .set(commonMappingDoc.updateUser, empNo)
        .set(commonMappingDoc.updateDate, LocalDateTime.now())
        .where(commonMappingDoc.docNo.eq(docNo), commonMappingDoc.refId.eq(refId))
        .execute();
    }
    
    
    /**
     * 특정 게시글에 연결된 파일 매핑 목록 조회
     */
    public List<CommonMappingDoc> findByRefId(Long refId) {
    	return queryFactory
    			.selectFrom(commonMappingDoc)
    			.where(
    				commonMappingDoc.refId.eq(refId),
    				commonMappingDoc.deleteYn.eq("N")
    			)
    			.fetch();
    }
}
