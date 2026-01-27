package kr.co.koreazinc.app.model.push;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebPushSubscribeRequest {

	private String endpoint;
	
	private Keys keys;
	
	private String userAgent;
	
	@Getter
	@Setter
	public static class Keys {
		private String p256dh;
		private String auth;
	}
}
