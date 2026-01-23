package kr.co.koreazinc.temp.model.entity.comm;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Entity
@Table(name = "co_common_mapping_doc",
uniqueConstraints = {
		@UniqueConstraint(
				name = "uk_ref_id_doc_no",
	            columnNames = {"ref_id", "doc_no"} // 두 쌍의 중복 방지
		)
	}
)
public class CommonMappingDoc {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mapping_id") // 별도의 PK 생성
	private Long mappingId;
	
	@Column(name = "ref_id")
    private Long refId;
	
	@Column(name = "doc_no")
    private Long docNo;
	
	@Column(name = "delete_yn")
    private String deleteYn;
    
    @Column(name = "create_user")
    private String createUser;
    
    @Column(name = "create_date")
    private LocalDateTime createDate;
    
    @Column(name = "update_user")
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
