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
package de.futuresqr.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import de.futuresqr.server.rest.user.LoginSuccessHandler;

/**
 * Main security configuration of the server.
 */
@Configuration
public class SecurityConfiguration {

	// path for all server REST end points
	private static final String PATH_REST = "/rest/**";
	// path used for login
	private static final String PATH_REST_AUTHENTICATE = "/rest/user/authenticate";
	// path used for custom re-authentication / remember-me mechanism
	private static final String PATH_REST_REAUTHENTICATE = "/rest/user/reauthenticate";
	// path used for receiving CSRF token
	public static final String PATH_REST_CSRF = "/rest/user/csrf";

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// authorization requirements section
		http.authorizeHttpRequests()
				// this rest path can be accessed by everyone
				.antMatchers(PATH_REST_AUTHENTICATE, PATH_REST_CSRF, PATH_REST_REAUTHENTICATE).permitAll()
				// this path can be accessed only by authenticated users
				.antMatchers(PATH_REST).authenticated()
				// allow all other requests
				.anyRequest().permitAll();
		http.formLogin().loginProcessingUrl(PATH_REST_AUTHENTICATE).successHandler(new LoginSuccessHandler());
		http.userDetailsService(getUserDetails());
		// CSRF configuration for Angular
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		// enable remember-me
		http.rememberMe().alwaysRemember(true);
		return http.build();
	}

	private InMemoryUserDetailsManager getUserDetails() {
		InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
		UserDetails user = User.builder().username("user").password(passwordEncoder().encode("password")).roles("USER")
				.build();
		userDetailsManager.createUser(user);
		return userDetailsManager;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
