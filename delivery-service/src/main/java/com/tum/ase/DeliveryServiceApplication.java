package com.tum.ase;

import com.tum.ase.constant.UserRole;
import com.tum.ase.controller.UserController;
import com.tum.ase.model.*;
import com.tum.ase.repo.BoxRepository;
import com.tum.ase.repo.OrderRepository;
import com.tum.ase.repo.UserRepository;
import com.tum.ase.security.inteceptor.RestTemplateInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@Slf4j
@EnableEurekaClient
public class DeliveryServiceApplication implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;

	@Autowired
	BoxRepository boxRepository;

	@Autowired
	OrderRepository orderRepository;

	public static void main(String[] args) {
		SpringApplication.run(DeliveryServiceApplication.class, args);
	}

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyManagementException {
//		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//		FileInputStream fis = null;
//		try {
//			Resource keystoreFile = new ClassPathResource("keystore.jks");
//			InputStream is = keystoreFile.getInputStream();
//
//			keyStore.load(is, "password".toCharArray());
//		} catch (Exception e) {
//			System.err.println("Error when loading KeyStore");
//			e.printStackTrace();
//		} finally {
//			if (fis != null) {
//				fis.close();
//			}
//		}
//
//		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
//				new SSLContextBuilder()
//						.loadTrustMaterial(null, new TrustSelfSignedStrategy())
//						.loadKeyMaterial(keyStore, "password".toCharArray())
//						.build(),
//				NoopHostnameVerifier.INSTANCE);
//
//		HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(
//				socketFactory).build();

//		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
//				httpClient);

//		RestTemplate restTemplate = new RestTemplate(requestFactory);
//		CloseableHttpClient httpClient = HttpClients.custom()
//				.setSSLHostnameVerifier(new NoopHostnameVerifier())
//				.build();
//		HttpComponentsClientHttpRequestFactory requestFactory =
//				new HttpComponentsClientHttpRequestFactory();
//		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate();

		List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
		if (CollectionUtils.isEmpty(interceptors)) {
			interceptors = new ArrayList<>();
		}
		interceptors.add(new RestTemplateInterceptor());
		restTemplate.setInterceptors(interceptors);
		return restTemplate;
	}

	@Override
	public void run(String... args) throws Exception {
		// Mock data for testing locally
		userRepository.deleteAll();
		orderRepository.deleteAll();
		boxRepository.deleteAll();
		if (userRepository.findAll().size() == 0) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			AppUser testDispatcher = new AppUser("dispatcher", "test1@gmail.com", null, UserRole.DISPATCHER);
			testDispatcher.setId("63d76a8b982f9143e56af4bb");
			AppUser testDeliverer = new AppUser("deliverer", "test2@gmail.com", "w9zap1br", UserRole.DELIVERER);
			testDeliverer.setId("63d76a8b982f9143e56af4bc");
			AppUser testCustomer = new AppUser("customer", "siyun.liang@tum.de", "f784xjmc", UserRole.CUSTOMER);
			testCustomer.setId("63d76a8b982f9143e56af4bd");
			Box testBox = new Box("Box13", "Boltzmannstr. 3, 85748 Muenchen", "group13");
			testBox.setId("63d76a8b982f9143e56af4be");

			userRepository.save(testDispatcher);
			userRepository.save(testDeliverer);
			userRepository.save(testCustomer);
			boxRepository.save(testBox);
		}
	}
}
