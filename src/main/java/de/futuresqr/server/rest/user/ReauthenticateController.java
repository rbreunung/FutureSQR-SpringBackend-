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
package de.futuresqr.server.rest.user;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import de.futuresqr.server.SecurityConfiguration;
import de.futuresqr.server.model.backend.PersistenceUser;
import de.futuresqr.server.model.frontend.CurrentUser;
import de.futuresqr.server.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * The client can verify its authentication and receive the current user.
 */
@RestController
@Slf4j
public class ReauthenticateController {

	@Autowired
	private UserRepository userRepository;

	@PostMapping(SecurityConfiguration.PATH_REST_REAUTHENTICATE)
	CurrentUser getUserReauthenticate(HttpServletRequest request, Authentication authentication,
			@RequestPart(name = "assumedusername", required = false) String assumedusername) {

		String username = authentication == null ? null : authentication.getName();
		if (assumedusername == null || assumedusername.isBlank() || username == null || username.isBlank()
				|| (!username.equals(assumedusername))) {
			try {
				if (authentication != null)
					request.logout();
			} catch (ServletException e) {
				log.error("Error during session logout.", e);
			}

			return null;
		}
		PersistenceUser persistenceUser = userRepository.findByLoginName(username);
		return CurrentUser.from(persistenceUser);
	}
}
