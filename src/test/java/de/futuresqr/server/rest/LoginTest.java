/**
 * MIT License
 *
 * Copyright (c) 2022 Robert Breunung
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.futuresqr.server.rest;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.COOKIE;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.OK;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import lombok.extern.slf4j.Slf4j;

/**
 * verify the default authentication workflow
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class LoginTest {

	private static final String REST_PATH_AUTHENTICATE = "/rest/user/authenticate";
	private static final String REST_PATH_CSRF = "/rest/user/csrf";
	private static final String SERVER_BASE_URI = "http://localhost:%d%s";

	@LocalServerPort
	int serverPort;

	@Autowired
	private TestRestTemplate webclient;

	@Test
	void getCsrfResponse_getRequest_validToken() {

		ResponseEntity<CsrfDto> entity = getCsrfResponse();

		log.info("Received CSRF token: {}", entity.getBody());
		assertNotNull(entity.getBody().getToken(), "CSRF token is expected at this URI.");
	}

	/**
	 * This test uses the CSRF token from a controller body.
	 */
	@Test
	void postAuthenticate_validRequestUsingBodyCsrf_userDto() {

		// get CSRF
		ResponseEntity<CsrfDto> entity = getCsrfResponse();
		List<HttpCookie> newCsrfCookies = getNewCookies(entity.getHeaders());
		log.info("Cookies received from CSRF controller: {}", newCsrfCookies.toString());

		// we use CSRF from controller body
		CsrfDto csrfDto = entity.getBody();

		// we use a multi part body for login
		RequestEntity<LinkedMultiValueMap<String, String>> request = createLoginRequest(csrfDto, newCsrfCookies,
				REST_PATH_AUTHENTICATE, getLoginFormBody());
		ResponseEntity<String> responseEntity = webclient.exchange(request, String.class);

		List<HttpCookie> newLoginCookies = getNewCookies(responseEntity.getHeaders());
		log.info("found {} new cookies: {}", newLoginCookies.size(), newLoginCookies);

		// custom login success handler sends status 200.
		assertEquals(OK, responseEntity.getStatusCode(), "Expect successful login.");
		assertTrue(newLoginCookies.size() > 0, "New session cookies must be set.");
	}

	/**
	 * This test uses the CSRF token from a controller header. This use case used to
	 * work in Spring Boot 2. THis use case is relevant, as it represents the
	 * Angular behavior.
	 */
	@Test
	void postAuthenticate_validRequestUsingHeaderCsrf_userDto() {

		// get CSRF
		ResponseEntity<CsrfDto> entity = getCsrfResponse();
		List<HttpCookie> newCsrfCookies = getNewCookies(entity.getHeaders());

		// we use the token from cookie instead of controller here
		CsrfDto csrfDto = entity.getBody();
		newCsrfCookies.stream().filter(c -> c.getName().equals("XSRF-TOKEN")).findAny()
				.ifPresent(c -> csrfDto.setToken(c.getValue()));

		// we use a multi part body for login
		RequestEntity<LinkedMultiValueMap<String, String>> request = createLoginRequest(csrfDto, newCsrfCookies,
				REST_PATH_AUTHENTICATE, getLoginFormBody());
		ResponseEntity<String> responseEntity = webclient.exchange(request, String.class);

		// we receive a 403 here because the provided token is not subject to the XOR
		// operation
		assertEquals(OK, responseEntity.getStatusCode(), "Expect successful login.");
	}

	private RequestEntity<LinkedMultiValueMap<String, String>> createLoginRequest(CsrfDto csrfDto,
			List<HttpCookie> cookieList, String path, LinkedMultiValueMap<String, String> formBody) {

		HttpHeaders headers = new HttpHeaders();
		if (cookieList != null) {
			headers.addAll(COOKIE, cookieList.stream().map(HttpCookie::toString).collect(toList()));
		}
		if (csrfDto != null) {
			headers.set(csrfDto.getHeaderName(), csrfDto.getToken());
		}

		String loginUri = String.format(SERVER_BASE_URI, serverPort, path);

		RequestEntity<LinkedMultiValueMap<String, String>> request = new RequestEntity<LinkedMultiValueMap<String, String>>(
				formBody, headers, HttpMethod.POST, URI.create(loginUri));
		return request;
	}

	private ResponseEntity<CsrfDto> getCsrfResponse() {
		String csrfUri = String.format(SERVER_BASE_URI, serverPort, REST_PATH_CSRF);
		ResponseEntity<CsrfDto> entity = webclient.getForEntity(csrfUri, CsrfDto.class);
		return entity;
	}

	private LinkedMultiValueMap<String, String> getLoginFormBody() {
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("username", "user");
		body.add("password", "password");
		return body;
	}

	private List<HttpCookie> getNewCookies(HttpHeaders headers) {
		List<HttpCookie> newCsrfCookies = headers.getOrEmpty(SET_COOKIE).stream()
				.flatMap(s -> HttpCookie.parse(s).stream()).collect(toList());
		return newCsrfCookies;
	}

}
