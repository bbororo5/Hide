package com.example.backend.track.entity;

import com.example.backend.track.dto.StarDto;
import com.example.backend.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Star {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private Double star;
	@Column(name = "track_id")
	private String trackId;
	@ManyToOne
	User user;

	public Star(String trackId,Double star, User user){
		this.star=star;
		this.trackId=trackId;
		this.user=user;
	}
	public void updateStar(Double star){
		this.star= star;
	}

}
