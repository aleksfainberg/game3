package com.example.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.Constants;
import com.example.entity.Turn;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
public class TurnRepositoryTest {

	@Autowired
	private TurnRepository turnRepository;
	
	@Test
	public void saveGame_thenFind() {
		System.out.println("test3");
	    // given
	    Turn turn1 = new Turn();
	    turn1.setPlayer(Constants.HUMAN);
	    turn1.setValue(11);
	    turnRepository.save(turn1);
	 
	    Turn turn2 = new Turn();
	    turn2.setPlayer(Constants.COMPUTER);
	    turn2.setValue(4);
	    turnRepository.save(turn2);	    
	    
	    // when
	    Turn found = turnRepository.findTopByOrderByIdDesc();
	 
	    // then
	    assertThat(found.getPlayer())
	      .isEqualTo(turn2.getPlayer());
	    assertThat(found.getValue())
	      .isEqualTo(turn2.getValue());
	}
	
	@AfterAll
	public void cleanDB() {
		turnRepository.deleteAll();
	}
}
