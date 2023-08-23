package com.example.backend.util.spotify;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.backend.track.dto.Track;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SpotifyUtil {
	private long tokenExpirationTime;
	private String accessToken;
	private final Object lock = new Object();
	@Value("${spotify.clientId}")
	private final String SPOTIFY_CLIENT_ID;
	@Value("${spotify.clientSecret}")
	private final String SPOTIFY_CLIENT_SECRET;

	public void requestAccessToken() {
		synchronized (lock) {
			// 이미 다른 스레드가 토큰을 갱신한 경우, 재요청을 피하기 위한 추가 검사
			if (isValid(accessToken)) {
				return;
			}

			String clientId = SPOTIFY_CLIENT_ID;
			String clientSecret = SPOTIFY_CLIENT_SECRET;
			String credentials = Base64.getEncoder()
				.encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

			WebClient webClient = WebClient.builder()
				.baseUrl("https://accounts.spotify.com")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();

			Mono<String> responseMono = webClient.post()
				.uri("/api/token")
				.header("Authorization", "Basic " + credentials)
				.body(BodyInserters.fromFormData("grant_type", "client_credentials"))
				.retrieve()
				.bodyToMono(String.class);

			String response = responseMono.block(); // 기다리기
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(response);
				this.accessToken = jsonNode.get("access_token").asText();
				int expiresIn = jsonNode.get("expires_in").asInt(); // 초 단위로 오는 만료 시간
				this.tokenExpirationTime = System.currentTimeMillis() + (expiresIn * 1000L);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public List<Track> getTracksInfo(List<String> trackIdList) {
		return fetchDataFromSpotifyAPI(String.join(",", trackIdList), "tracks");
	}

	public Track getTracksInfo(String trackId) {
		return fetchSingleTrackFromSpotifyAPI(trackId, "tracks");
	}

	private Track fetchSingleTrackFromSpotifyAPI(String parameter, String endpoint) {
		List<Track> tracks = fetchDataFromSpotifyAPI(parameter, endpoint);
		if (tracks.isEmpty()) {
			throw new NoSuchElementException("트랙을 찾을 수 없습니다.");
		}
		return tracks.get(0);
	}

	public List<Track> getRecommendTracks(List<String> trackIdList) {
		return fetchDataFromSpotifyAPI(String.join(",", trackIdList), "recommendations");
	}

	public List<Track> getSearchResult(String keyword) {
		return fetchDataFromSpotifyAPI(keyword, "search");
	}

	private List<Track> fetchDataFromSpotifyAPI(String parameter, String endpoint) {
		return fetchDataFromSpotifyAPI(parameter, endpoint, 0);
	}

	private List<Track> fetchDataFromSpotifyAPI(String parameter, String endpoint, int attempt) {
		if (attempt > 2) {
			throw new IllegalStateException("재시도 2회 후에도 트랙 정보 가져오기 실패");
		}
		requestAccessToken();

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);

		HttpEntity<String> entity = new HttpEntity<>("", headers);

		String url = generateSpotifyUrl(parameter, endpoint);

		ArrayList<Track> tracklist = new ArrayList<>();

		try {
			ResponseEntity<JsonNode> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				entity,
				JsonNode.class
			);

			JsonNode responseBody = response.getBody();
			JsonNode tracksNode;
			if (endpoint.equals("search")) {
				tracksNode = responseBody.path("tracks").path("items");
			} else {
				tracksNode = responseBody.path("tracks");
			}

			// .path("items");

			if (tracksNode.isMissingNode()) {
				throw new NoSuchElementException("노드를 찾을 수 없습니다.");
			}

			for (JsonNode trackNode : tracksNode) {
				tracklist.add(parseTrackNode(trackNode));
			}
		} catch (RestClientException e) {
			if (isTokenExpired(e)) {
				requestAccessToken();
				return fetchDataFromSpotifyAPI(parameter, endpoint, attempt + 1); // 재시도
			} else {
				throw e;
			}
		}

		return tracklist;
	}

	private String generateSpotifyUrl(String parameter, String endpoint) {
		switch (endpoint) {
			case "tracks":
				return String.format("https://api.spotify.com/v1/%s?ids=%s", endpoint, parameter);
			case "recommendations":
				return String.format("https://api.spotify.com/v1/%s?limit=10&seed_tracks=%s", endpoint, parameter);
			case "search":
				return String.format("https://api.spotify.com/v1/%s?q=%s&type=track&limit=20", endpoint, parameter);
			// https://api.spotify.com/v1/search?q=jungkuk&type=track%2Cartist%2Cplaylist&limit=10'
			default:
				throw new IllegalArgumentException("지원하지 않는 엔드포인트입니다.");
		}
	}

	private Track parseTrackNode(JsonNode trackNode) {
		String trackId = trackNode
			.path("id")
			.asText();

		String trackTitle = trackNode
			.path("name")
			.asText();

		String albumName = trackNode
			.path("album")
			.path("name")
			.asText();

		JsonNode imageNodes = trackNode
			.path("album")
			.path("images");

		String image = imageNodes.size() > 0 ? imageNodes.get(0).path("url").asText() : "";

		List<Track.Artist> artists = new ArrayList<>();
		JsonNode artistNodes = trackNode.path("artists");
		for (JsonNode artistNode : artistNodes) {
			String artistName = artistNode.path("name").asText();
			Track.Artist artist = Track.Artist.builder()
				.artistName(artistName)
				.build();
			artists.add(artist);
		}

		List<Track.Genre> genreList = new ArrayList<>();
		JsonNode genreNodes = trackNode
			.path("album");
		for (JsonNode genreNode : genreNodes) {
			String genre = genreNode.path("genre").asText();
			Track.Genre genreElement = Track.Genre.builder()
				.genre(genre)
				.build();
			genreList.add(genreElement);
		}

		return Track.builder()
			.id(trackId)
			.title(trackTitle)
			.album(albumName)
			.image(image)
			.artists(artists)
			.genre(genreList)
			.build();
	}

	private boolean isTokenExpired(RestClientException e) {
		if (e instanceof HttpStatusCodeException) {
			HttpStatusCodeException httpException = (HttpStatusCodeException)e;

			//401 여부 체크
			if (httpException.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				//반환되는 메세지 확인하여 토큰 만료 여부 확인
				return httpException.getResponseBodyAsString().contains("The access token expired");
			}
		}

		return false;
	}

	private boolean isValid(String token) {
		// accessToken의 유효성 검사. 예를 들어, 만료 시간을 저장해둔다면 그것을 기반으로 판별 가능
		// 이 예제에서는 단순히 null 또는 빈 문자열인지만 검사
		return token != null && !token.isEmpty() && System.currentTimeMillis() < tokenExpirationTime;
	}

}

