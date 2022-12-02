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
package de.futuresqr.server.persistence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import de.futuresqr.server.model.backend.PersistenceUser;

/**
 * This verifies the data initializes as expected
 */
@SpringBootTest
@DirtiesContext()
public class UserRepositoryTest {

	private static final String LOGIN_NAME = "rbreunung";
	@Autowired
	private UserRepository userRepository;
	private PersistenceUser originalUser;
	private Set<String> originaAuthorities;

	@BeforeEach
	@Transactional
	void setup() {

		// Currently these users are part of the demo setup. They are required to pass
		// the tests.
//		userRepository.deleteAll();
//		userRepository.flush();
//		originaAuthorities = new HashSet<>();
//		originaAuthorities.add("ROLE_TEST");
//		originalUser = PersistenceUser.builder().uuid(UUID.randomUUID()).loginName("admin")
//				.displayName("Robert Breunung").email("rbreunung@gmail.com").grantedAuthorities(originaAuthorities)
//				.build();
//		userRepository.save(originalUser);
//
//		originaAuthorities = new HashSet<>();
//		originaAuthorities.add("ROLE_TEST");
//		originalUser = PersistenceUser.builder().uuid(UUID.randomUUID()).loginName("user")
//				.displayName("Robert Breunung").email("rbreunung@gmail.com").grantedAuthorities(originaAuthorities)
//				.build();
//		userRepository.save(originalUser);

		originaAuthorities = new HashSet<>();
		originaAuthorities.add("ROLE_TEST");
		originalUser = PersistenceUser.builder().uuid(UUID.randomUUID()).loginName(LOGIN_NAME)
				.displayName("Robert Breunung").email("rbreunung@gmail.com").grantedAuthorities(originaAuthorities)
				.build();
		userRepository.save(originalUser);
	}

	@ParameterizedTest
	@Transactional
	@ValueSource(strings = { LOGIN_NAME, "admin", "user" })
	void findByLoginName_knownUser_getUser(String username) {
		PersistenceUser user = userRepository.findByLoginName(username);

		assertNotNull(user, "User shall exist.");
	}

	@ParameterizedTest
	@Transactional
	@ValueSource(strings = { LOGIN_NAME, "admin", "user" })
	void findByLoginName_knownUser_getAuthorities(String username) {
		PersistenceUser user = userRepository.findByLoginName(username);

		assertFalse(user.getGrantedAuthorities().isEmpty(), "At least one authority is expected.");
	}
}
