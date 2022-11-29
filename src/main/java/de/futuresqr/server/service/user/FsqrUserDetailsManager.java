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
package de.futuresqr.server.service.user;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import de.futuresqr.server.model.backend.PersistenceUser;
import de.futuresqr.server.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Adapter access the user repository from Spring Security authentication
 * mechanism.
 */
@Service
@Slf4j
public class FsqrUserDetailsManager implements UserDetailsManager {

	public static final String PREFIX_ROLE = "ROLE_";
	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_USER = "USER";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private void setPasswordEncoder(PasswordEncoder encoder) {
		if (userRepository.count() == 0) {
			log.info("Empty user repository. Set default users.");
			PersistenceUser user = PersistenceUser.builder().uuid(randomUUID()).loginName("user")
					.password(encoder.encode("password"))
					.grantedAuthorities(Arrays.stream(new String[] { PREFIX_ROLE + ROLE_USER }).collect(toSet()))
					.displayName("Otto Normal").avatarLocation(randomUUID().toString()).email("user@mindscan.local")
					.build();
			userRepository.save(user);
			user = PersistenceUser.builder().uuid(randomUUID()).loginName("admin").password(encoder.encode("admin"))
					.grantedAuthorities(
							Arrays.stream(new String[] { PREFIX_ROLE + ROLE_USER, PREFIX_ROLE + ROLE_ADMIN })
									.collect(toSet()))
					.displayName("Super Power").avatarLocation(randomUUID().toString()).email("admin@mindscan.local")
					.build();
			userRepository.save(user);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		PersistenceUser user = userRepository.findByLoginName(username);
		if (user != null) {
			return User.builder().username(user.getLoginName()).password(user.getPassword())
					.accountLocked(user.isBanned()).authorities(user.getGrantedAuthorities().toArray(String[]::new))
					.build();
		}
		return null;
	}

	@Override
	public void createUser(UserDetails user) {
		throw new UnsupportedOperationException("Not implemented.");

	}

	@Override
	public void updateUser(UserDetails user) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void deleteUser(String username) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean userExists(String username) {

		return userRepository.findByLoginName(username) != null;
	}

}
