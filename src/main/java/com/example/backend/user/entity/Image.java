package com.example.backend.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image {
	@Id
	private String imageKey;

	@Column(nullable = false)
	private String imageUrl;

	public Image(String imageKey, String imageUrl) {
		this.imageKey = imageKey;
		this.imageUrl = imageUrl;
	}
}
