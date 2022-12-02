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
package de.futuresqr.server.model.frontend;

import static de.futuresqr.server.service.user.FsqrUserDetailsManager.PREFIX_ROLE;

import java.util.UUID;

import de.futuresqr.server.model.backend.PersistenceUser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Client model of the current user.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CurrentUser {

	@NonNull
	private UUID uuid;
	@NonNull
	private String loginname;
	@NonNull
	private String displayname;
	private String avatarlocation;
	@NonNull
	private String email;
	@NonNull
	@Builder.Default
	private CurrentUserCapabilities capabilities = new CurrentUserCapabilities();

	public static CurrentUser from(PersistenceUser persistenceUser) {

		CurrentUserBuilder userBuilder = new CurrentUserBuilder();

		userBuilder.uuid(persistenceUser.getUuid()).loginname(persistenceUser.getLoginName())
				.displayname(persistenceUser.getDisplayName()).avatarlocation(persistenceUser.getAvatarLocation())
				.email(persistenceUser.getEmail());
		CurrentUser user = userBuilder.build();

		persistenceUser.getGrantedAuthorities().stream().filter(a -> a.startsWith(PREFIX_ROLE))
				.map(a -> a.substring(PREFIX_ROLE.length())).forEach(user.getCapabilities().getRoles()::add);

		return user;
	}
}
