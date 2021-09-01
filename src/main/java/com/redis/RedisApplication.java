package com.redis;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class RedisApplication {

	public static void main(String... args) {
		SpringApplication.run(RedisApplication.class, args);
	}
}

@Configuration
@EnableCaching
class ConfigCache {
}

@RestController
@RequestMapping("/")
class Controler {

	@Autowired
	private HttpServletRequest request;

	@GetMapping
	public RootHateos root() {
		RootHateos rootHateos = new RootHateos();

		rootHateos.add(linkTo(methodOn(Controler.class).findAll())
				.withRel("redis-cache"));

		rootHateos.add(linkTo(methodOn(Controler.class).evict(request))
				.withRel("redis-evict"));

		rootHateos.add(linkTo(methodOn(Controler.class).findMachine())
				.withRel("current-machine"));

		return rootHateos;
	}


	@GetMapping("current/machine")
	public Response findMachine() {
		final Response response_v1 = Response.builder()
				.machine(request.getLocalName())
				.uuid(UUID.randomUUID())
				.data(OffsetDateTime.now().toString())
				.build();

		return response_v1;
	}

	@GetMapping("/redis")
	@Cacheable("redis")
	public List<Response> findAll() {
		final Response response_v1 = Response.builder()
				.data("redis #1")
				.machine(request.getLocalName())
				.uuid(UUID.randomUUID())
				.build();

		final Response response_v2 = Response.builder()
				.data("redis #2")
				.uuid(UUID.randomUUID())
				.machine(request.getLocalName())
				.build();

		System.out.println("######### findAll #########");

		return List.of(response_v1, response_v2);
	}

	@GetMapping("redis/evict")
	@CacheEvict(cacheNames = "redis", allEntries = true)
	public Map<Object, Object> evict(HttpServletRequest request) {
		Map<Object, Object> toMap = new HashMap<>();
		toMap.put("evict", "redis");
		toMap.put("machine", request.getLocalName());

		System.out.println("######### evict #########");

		return toMap;
	}

}

@Data
@Builder
class Response implements Serializable {
	private UUID uuid;
	private String machine;
	private String data;
}

class RootHateos extends RepresentationModel<RootHateos> {
}