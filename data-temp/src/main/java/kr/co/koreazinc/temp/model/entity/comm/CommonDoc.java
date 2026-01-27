package kr.co.koreazinc.temp.model.entity.comm;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "co_common_doc")
@Entity
public class CommonDoc {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "doc_no")
    private Long docNo;
	
	@Column(name = "job_se_code")
	private String jobSeCode;
	
	@Column(name = "delete_yn")
    private String deleteYn;
	
	@Column(name = "doc_file_nm")
    private String docFileNm;
	
	@Column(name = "file_path")
    private String filePath;
	
	@Column(name = "save_file_nm")
    private String saveFileNm;
    
    @Column(name = "create_user")
    private String createUser;
    
    @Column(name = "create_date")
    private LocalDateTime createDate;
    
    @Column(name = "update_user")
    private String updateUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
