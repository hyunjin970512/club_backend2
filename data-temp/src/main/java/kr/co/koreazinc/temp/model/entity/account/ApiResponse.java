package kr.co.koreazinc.temp.model.entity.account;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;

    private ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // ✅ 성공 응답
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // ✅ 실패 응답
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }

	public static ApiResponse<Meresponse> fail(String string, String string2) {
		// TODO Auto-generated method stub
		return new ApiResponse<>(false, null, string2);
	}
}
