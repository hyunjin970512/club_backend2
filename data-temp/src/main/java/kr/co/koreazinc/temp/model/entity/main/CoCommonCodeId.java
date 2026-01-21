package kr.co.koreazinc.temp.model.entity.main;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CoCommonCodeId implements Serializable {

    @Column(name = "main_code", length = 25, nullable = false)
    private String mainCode;

    @Column(name = "sub_code", length = 15, nullable = false)
    private String subCode;

    public CoCommonCodeId(String mainCode, String subCode) {
        this.mainCode = mainCode;
        this.subCode = subCode;
    }
}

