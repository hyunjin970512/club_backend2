package kr.co.koreazinc.app.configuration.push;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.PushService;

@Configuration
public class WebPushConfig {
	
	@PostConstruct
	public void init() {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	@Bean
	public PushService pushService(
									@Value("${webpush.vapid.public-key}") String publicKey,
									@Value("${webpush.vapid.private-key}") String privateKey,
									@Value("${webpush.subject}") String subject
									) throws Exception 
	{
		PushService service = new PushService();
		service.setPublicKey(publicKey);
		service.setPrivateKey(privateKey);
		service.setSubject(subject);
		return service;
	}
	
}
