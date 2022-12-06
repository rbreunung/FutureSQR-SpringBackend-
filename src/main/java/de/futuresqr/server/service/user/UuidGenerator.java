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

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.factory.rfc4122.NameBasedMd5Factory;

/**
 * We want to generate custom UUIDs.
 */
@Service
public class UuidGenerator {

	private final NameBasedMd5Factory factory;

	private final UUID FUTURESQR_NAMESPACE_OID;
	private final UUID SYSCONFIG_NAMESPACE_OID;
	private final UUID USERNAMES_NAMESPACE_OID;

	public UuidGenerator() {
		factory = new NameBasedMd5Factory(UuidNamespace.NAMESPACE_OID);
		FUTURESQR_NAMESPACE_OID = factory.create("FutureSQR");
		SYSCONFIG_NAMESPACE_OID = factory.create(FUTURESQR_NAMESPACE_OID, "SystemInstance");
		USERNAMES_NAMESPACE_OID = factory.create(FUTURESQR_NAMESPACE_OID, "SystemUsers");
	}

	public UUID getSystemUuid(String key) {
		return factory.create(SYSCONFIG_NAMESPACE_OID, key);
	}

	public UUID getUserUuid(String loginName) {
		return factory.create(USERNAMES_NAMESPACE_OID, loginName);
	}
}
