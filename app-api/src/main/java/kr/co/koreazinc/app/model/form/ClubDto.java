package kr.co.koreazinc.app.model.form;

import lombok.Getter;
import lombok.Setter;

public class ClubDto {

    @Getter @Setter
    public static class FormResponse {
        private String status;   // CREATE / EDIT
        private Long clubId;

        private String clubNm;
        private String clubDesc;
        private String purpose;

        private String clubType;
        private String clubMasterId;

        private Long ruleFileId;
        private Long memberFileId;
    }

    @Getter @Setter
    public static class SaveRequest {
        private String clubNm;
        private String clubDesc;
        private String purpose;

        private String clubType;

        private String clubMasterId;   // 화면에서 안 받으면 서버에서 me로 박아도 됨
        private Long ruleFileId;
        private Long memberFileId;
    }
}
