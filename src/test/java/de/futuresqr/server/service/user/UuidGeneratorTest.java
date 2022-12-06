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

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.futuresqr.server.model.frontend.CurrentUser;
import lombok.extern.slf4j.Slf4j;

/**
 * Verify the output of the Generator produces expected results
 */
@Slf4j
public class UuidGeneratorTest {

	private UuidGenerator generator = new UuidGenerator();
	private static final ArrayList<CurrentUser> defaultUsers = new ArrayList<>();

	@BeforeAll
	static void readJsnon() throws IOException {
		File defaultsFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "defaults.json");
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode rootNode = mapper.readTree(defaultsFile);
		JsonNode users = rootNode.get("defaultUser");
		for (JsonNode user : users) {
			CurrentUser defaultUser = mapper.treeToValue(user, CurrentUser.class);
			log.info(defaultUser.toString());
			defaultUsers.add(defaultUser);
		}
	}

	@TestFactory
	Collection<DynamicTest> test_readUuidFromDefaultSetup_matchesCurrentGenerator() {
		return defaultUsers.stream()
				.map(user -> DynamicTest.dynamicTest("test " + user.getLoginname(),
						() -> assertEquals(user.getUuid(), generator.getUserUuid(user.getLoginname()))))
				.collect(toList());
	}

}
