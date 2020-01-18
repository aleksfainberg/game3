package com.example.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.Constants;
import com.example.entity.Game;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
public class GameRepositoryTest {

	@Autowired
	private GameRepository gameRepository;
	
	@Test
	public void saveGame_thenFind() {
	    // given
	    Game game1 = new Game();
	    game1.setStatus(Constants.STARTED);
	    game1.setFirstPlayer(Constants.HUMAN);	   
	    gameRepository.save(game1);
	 
	    // when
	    Game found = gameRepository.findTopByOrderByIdDesc();
	 
	    // then
	    assertThat(found.getStatus())
	      .isEqualTo(game1.getStatus());
	    assertThat(found.getFirstPlayer())
	      .isEqualTo(game1.getFirstPlayer());
	}
	
	@AfterAll
	public void cleanDB() {
		gameRepository.deleteAll();
	}
}
