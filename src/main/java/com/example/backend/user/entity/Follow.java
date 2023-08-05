package com.example.backend.user.entity;

import com.example.backend.Timestamped;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Follow extends Timestamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long followId;
	@ManyToOne
	@JoinColumn(name = "from_User_id")
	private User fromUser;
	@ManyToOne
	@JoinColumn(name = "to_User_id")
	private User toUser;

	public Follow(User fromUser, User toUser){
		this.fromUser = fromUser;
		this.toUser = toUser;
	}
}
