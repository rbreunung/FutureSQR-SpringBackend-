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
package de.futuresqr.server.model.backend;

import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data model for the user of the FutureSQR application and database.
 */
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "fsqrUser", uniqueConstraints = { @UniqueConstraint(columnNames = "loginName") })
public class PersistenceUser {

	@Id
	private UUID uuid;
	private String loginName;
	private String password;
	@Builder.Default
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<String> grantedAuthorities = new HashSet<>();

	private String avatarLocation;
	private String displayName;
	private String email;

	@Builder.Default
	private Instant createdDate = Instant.now();
	@Builder.Default
	private Instant lastChangeDate = Instant.now();
	private boolean banned;
	private Instant bannedDate;
}
