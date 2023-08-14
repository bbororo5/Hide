//package com.example.playlist.playlist.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public class MusicRepositoryDto {
//    public interface MusicRepository extends JpaRepository<Music, Long> {
//
//        // 스포티파이 api 로직 대신 임시방편으로 넣음
//        Optional<Music> findById(Long id);
//        List<Music> findByTitle(String title);
//        List<Music> findByArtist(String artist);
//    }
//
//}
