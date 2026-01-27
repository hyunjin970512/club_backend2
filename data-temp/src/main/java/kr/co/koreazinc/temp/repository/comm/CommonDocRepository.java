package kr.co.koreazinc.temp.repository.comm;

import java.util.List;
import static kr.co.koreazinc.temp.model.entity.comm.QCommonDoc.commonDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import kr.co.koreazinc.data.support.Query;
import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.entity.comm.CommonDoc;

@Repository
@Transactional(readOnly = true)
public class CommonDocRepository extends AbstractJpaRepository<CommonDoc, Long> {
	
	public CommonDocRepository(@Autowired List<EntityManager> entityManagers) {
		super(CommonDoc.class, entityManagers);
	}
	
	public class SelectQuery<DTO> extends Query.Select<DTO> {
		public SelectQuery(JPAQuery<DTO> query) {
			super(query);
			this.addField("docNo", commonDoc.docNo);
			this.addField("jobSeCode", commonDoc.jobSeCode);
            this.addField("docFileNm", commonDoc.docFileNm);
            this.addField("createUser", commonDoc.createUser);
            this.addField("createDate", commonDoc.createDate);
            this.addField("filePath", commonDoc.filePath);
            this.addField("saveFileNm", commonDoc.saveFileNm); 
            this.addField("createUser", commonDoc.createUser);
            this.addField("createDate", commonDoc.createDate);
		}
		
		public SelectQuery<DTO> eqDocNo(Long docNo) {
			query.where(commonDoc.docNo.eq(docNo));
	        return this;
	    }
		
		 public SelectQuery<DTO> eqJobSeCode(String jobSeCode) {
            query.where(commonDoc.jobSeCode.eq(jobSeCode));
            return this;
        }
	}
	
	// ID로 단건 조회 (다운로드용)
	public CommonDoc selectById(Long docNo) {
	    return queryFactory.selectFrom(commonDoc)
	            .where(commonDoc.docNo.eq(docNo)
	              .and(commonDoc.deleteYn.eq("N"))) // 삭제되지 않은 파일만
	            .fetchOne();
	}
	
	// 전체 엔티티 조회
	public SelectQuery<CommonDoc> selectQuery() {
        return new SelectQuery<>(queryFactory.selectFrom(commonDoc));
    }
	
	public <T> SelectQuery<T> selectQuery(Class<T> type) {
		return new SelectQuery<>(queryFactory
				.select(Projections.bean(type, 
						commonDoc.docNo,
						commonDoc.jobSeCode,
						commonDoc.docFileNm,
						commonDoc.createUser,
						commonDoc.createDate))
				.from(commonDoc));
	}
	
	@Transactional
    public CommonDoc insert(CommonDoc entity) {
        return super.insert(entity);
    }
	
	@Transactional
    public CommonDoc save(CommonDoc entity) {
        return super.save(entity);
    }
}
