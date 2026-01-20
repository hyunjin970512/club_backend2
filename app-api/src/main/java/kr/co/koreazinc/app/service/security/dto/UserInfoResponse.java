package kr.co.koreazinc.app.service.security.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserInfoResponse {
    private String userId;
    private List<Job> job;

    @Getter @Setter
    public static class Job {
        private String empNo;
    }
}
