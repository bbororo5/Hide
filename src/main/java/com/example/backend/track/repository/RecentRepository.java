package com.example.backend.track.repository;

import com.example.backend.track.entity.Recent;
import com.example.backend.user.entity.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecentRepository extends JpaRepository<Recent, Long> {

    List<Recent> findAllByUserOrderByCreationDateDesc(User user);

	List<Recent> findByUserOrderByCreationDateAsc(User user);

	List<Recent> findTop7ByOrderByCreationDateDesc(Pageable pageable);
}


