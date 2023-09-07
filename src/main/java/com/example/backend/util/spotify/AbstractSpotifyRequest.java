package com.example.backend.util.spotify;

import com.example.backend.track.dto.Track;
import com.example.backend.util.execption.CustomResponseErrorHandler;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@RequiredArgsConstructor
@Slf4j
public abstract class AbstractSpotifyRequest {
    protected final SpotifyTokenManager spotifyTokenManager;
    private final RestTemplate restTemplate;

    protected List<Track> fetchDataFromSpotifyAPI(String trackIds, int attempt) {
        log.info("스포티파이 API에 데이터 요청 시작");
        if (attempt > 2) {
            log.error("재시도 2회 이후에도 스포티파이 API로 부터 계속된 데이터 요청 실패");
            throw new IllegalStateException("트랙 정보 가져오기 실패");
        }

        log.info("스포티파이 API로부터 ACCESS TOKEN 받기");

        String accessToken = spotifyTokenManager.getAccessToken();

        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        log.info("url 설정");
        String url = generateSpotifyUrl(trackIds);

        ArrayList<Track> tracklist = new ArrayList<>();

        log.info("HTTP 요청");
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            JsonNode responseBody = response.getBody();
            log.debug("받은 전체 응답: {}", responseBody);

            log.info("받은 응답으로부터 노드 분류");
            JsonNode tracksNode = extractTracksNode(responseBody);

            if (tracksNode.isMissingNode()) {
                log.error("응답에서 예상된 노드를 찾지 못함. 전체 응답: {}", responseBody);
                throw new NoSuchElementException("노드를 찾을 수 없습니다.");
            } else {
                log.info("노드 추출 성공");
            }

            for (JsonNode trackNode : tracksNode) {
                tracklist.add(parseTrackNode(trackNode));
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED && isTokenExpired(e)) {
                log.warn("Spotify Access Token이 만료됨. 새 토큰을 요청하고 API를 다시 호출합니다.", e);
                spotifyTokenManager.getAccessToken();
                return fetchDataFromSpotifyAPI(trackIds, attempt + 1); // 재시도
            } else {
                log.error("HTTP 클라이언트 오류 발생: 상태 코드 {}", e.getStatusCode(), e);
            }
        } catch (HttpServerErrorException e) {
            log.error("HTTP 서버 오류 발생: 상태 코드 {}", e.getStatusCode(), e);
        } catch (RestClientException e) {
            log.error("API 호출 중 오류 발생", e);
        }

        return tracklist;
    }

    private boolean isTokenExpired(HttpClientErrorException e) {
        HttpStatusCode status = e.getStatusCode();
        String responseBody = e.getResponseBodyAsString();

        if (status == HttpStatus.UNAUTHORIZED) {
            log.debug("HTTP 응답 상태가 401 (UNAUTHORIZED). 응답 메시지 확인 중.");

            if (responseBody.contains("The access token expired")) {
                log.warn("Spotify Access Token이 만료됨을 확인. 토큰을 갱신해야 합니다.");
                return true;
            }
        } else {
            log.debug("HTTP 응답 상태: {}. 토큰 만료 문제가 아닌 것으로 판단.", status);
        }

        log.debug("RestClientResponseException의 타입: {}", e.getClass().getSimpleName());
        return false;
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

        // List<Track.Genre> genreList = new ArrayList<>();
        // JsonNode genreNodes = trackNode
        //         .path("album");
        // for (JsonNode genreNode : genreNodes) {
        //     String genre = genreNode.path("genre").asText();
        //     log.debug("장르: {}", genre);
        //     Track.Genre genreElement = Track.Genre.builder()
        //             .genre(genre)
        //             .build();
        //     genreList.add(genreElement);
        // }

        Track track = Track.builder()
                .id(trackId)
                .title(trackTitle)
                .album(albumName)
                .image(image)
                .artists(artists)
                // .genre(genreList)
                .build();

        log.info("트랙 노드 파싱 완료: {}", track);

        return track;
    }
    protected abstract String generateSpotifyUrl(String trackIds);

    protected abstract JsonNode extractTracksNode(JsonNode responseBody);
}
