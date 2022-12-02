package de.futuresqr.server.rest.user;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import de.futuresqr.server.model.backend.PersistenceUser;
import de.futuresqr.server.model.frontend.CurrentUser;
import de.futuresqr.server.model.frontend.UserProperties;
import de.futuresqr.server.persistence.UserRepository;

@RestController
public class BanController {

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/rest/user/ban")
	ResponseEntity<CurrentUser> postUserBan(@RequestPart(UserProperties.UUID) String uuid) {

		PersistenceUser persistenceUser = userRepository.getReferenceById(UUID.fromString(uuid));

		if (persistenceUser == null) {
			return ResponseEntity.notFound().build();
		}

		persistenceUser.setBanned(true);
		Instant now = Instant.now();
		persistenceUser.setBannedDate(now);
		persistenceUser.setLastChangeDate(now);

		persistenceUser = userRepository.save(persistenceUser);

		return ResponseEntity.ok(CurrentUser.from(persistenceUser));
	}

	@PostMapping("/rest/user/unban")
	ResponseEntity<CurrentUser> postUserUnban(@RequestPart(UserProperties.UUID) String uuid) {

		PersistenceUser persistenceUser = userRepository.getReferenceById(UUID.fromString(uuid));

		if (persistenceUser == null) {
			return ResponseEntity.notFound().build();
		}

		persistenceUser.setBanned(false);
		Instant now = Instant.now();
		persistenceUser.setBannedDate(null);
		persistenceUser.setLastChangeDate(now);

		persistenceUser = userRepository.save(persistenceUser);

		return ResponseEntity.ok(CurrentUser.from(persistenceUser));
	}
}
