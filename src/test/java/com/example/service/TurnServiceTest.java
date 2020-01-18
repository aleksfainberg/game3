package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.Constants;
import com.example.entity.Turn;
import com.example.repository.TurnRepository;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
public class TurnServiceTest {
	@Mock
	TurnRepository turnRepo;

	@Autowired
	@InjectMocks
	TurnService turnService = new TurnService() ;

	@BeforeAll
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void testListAll() {
		Turn turn1 = new Turn();
		turn1.setPlayer(Constants.COMPUTER);
		turn1.setValue(10);		
		Turn turn2 = new Turn();
		turn2.setPlayer(Constants.HUMAN);
		turn2.setValue(3);		
		List<Turn> listTurn = new ArrayList<Turn>();
		listTurn.add(turn1);
		listTurn.add(turn2);
		
		when(turnRepo.findAll()).thenReturn(listTurn);
		List<Turn> result = turnService.listAll();
		assertThat(result.size())
	      .isEqualTo(2);
	}
	
	@Test
	public void testDoTurn() {
		Turn turn1 = new Turn();
		turn1.setPlayer(Constants.COMPUTER);
		turn1.setValue(10);
		when(turnRepo.save(ArgumentMatchers.<Turn>any())).thenAnswer(i -> i.getArguments()[0]);
		
		Turn result = turnService.doTurn(9);
		assertThat(result.getValue())
	      .isEqualTo(3);
	}

	@Test
	public void testDoTurn_value0() {
		Turn turn1 = new Turn();
		turn1.setPlayer(Constants.COMPUTER);
		turn1.setValue(10);
		when(turnRepo.save(ArgumentMatchers.<Turn>any())).thenAnswer(i -> i.getArguments()[0]);
		
		Turn result = turnService.doTurn(0);
		assertThat(result.getValue())
	      .isBetween(1, Constants.MAX_START_VALUE);
	}
	
	@Test
	public void testCreateNewHumanTurn() {
		Turn turn1 = new Turn();
		turn1.setPlayer(Constants.HUMAN);
		Turn result = turnService.createNewHumanTurn();
		assertThat(result.getPlayer())
	      .isEqualTo(turn1.getPlayer());
	}
	
	@Test
	public void testSave() {
		Turn turn1 = new Turn();
		turn1.setPlayer(Constants.COMPUTER);
		turn1.setValue(10);
		when(turnRepo.save(ArgumentMatchers.<Turn>any())).thenAnswer(i -> i.getArguments()[0]);
		
		Turn result = turnService.save(turn1);
		assertThat(result.getValue())
	      .isEqualTo(turn1.getValue());
		assertThat(result.getPlayer())
	      .isEqualTo(turn1.getPlayer());
	}

	@Test
	public void testValidateTurn_OK() {
		Turn turn1 = new Turn();
		turn1.setPlayer(Constants.COMPUTER);
		turn1.setValue(10);
		
		Turn turn2 = new Turn();
		turn2.setPlayer(Constants.HUMAN);
		turn2.setValue(3);
		when(turnRepo.findTopByOrderByIdDesc()).thenReturn(turn1);
		
		String result = turnService.validateTurn(turn2);
		assertThat(result)
	      .isEqualTo(Constants.OK);
	}

	@Test
	public void testValidateTurn_null() {
		Turn turn1 = null;
		
		Turn turn2 = new Turn();
		turn2.setPlayer(Constants.HUMAN);
		turn2.setValue(3);
		when(turnRepo.findTopByOrderByIdDesc()).thenReturn(turn1);
		
		String result = turnService.validateTurn(turn2);
		assertThat(result)
	      .isEqualTo(Constants.OK);
		
		turn2.setValue(1);
		result = turnService.validateTurn(turn2);
		assertThat(result)
	      .isEqualTo(Constants.FAILED);
	}
	
	
}
