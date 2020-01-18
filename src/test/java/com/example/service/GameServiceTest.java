package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.Constants;
import com.example.entity.Game;
import com.example.entity.Turn;
import com.example.repository.GameRepository;
import com.example.repository.TurnRepository;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
public class GameServiceTest {
	@Mock
	private GameRepository gameRepo;

	@Mock
	private TurnService turnService;
	
	@Autowired
	@InjectMocks
	private GameService gameService = new GameService() ;

	@BeforeAll
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void testListAll() {
		Game game1 = new Game();
		game1.setFirstPlayer(Constants.COMPUTER);
		game1.setStatus(Constants.FINISHED);		
		Game game2 = new Game();
		game2.setFirstPlayer(Constants.HUMAN);
		game2.setStatus(Constants.STARTED);		
		List<Game> listGame = new ArrayList<Game>();
		listGame.add(game1);
		listGame.add(game2);
		
		when(gameRepo.findAll()).thenReturn(listGame);
		List<Game> result = gameService.listAll();
		assertThat(result.size())
	      .isEqualTo(2);
	}
	
	@Test
	public void testStartNewGame_1() {
		Game game1 = new Game();
		game1.setFirstPlayer(Constants.HUMAN);
		game1.setStatus(Constants.STARTED);		
		GameService gameService1 = Mockito.spy(gameService);
		Mockito.doReturn(game1).when(gameService1).createNewGame(Constants.HUMAN);
		Game result = gameService1.startNewGame(1);
		assertThat(result.getFirstPlayer())
	      .isEqualTo(Constants.HUMAN);
	}
	
	@Test
	@DisplayName("testStartNewGame_0")
	public void testStartNewGame_0() {
		Turn turn1 = new Turn();
		turn1.setPlayer(Constants.COMPUTER);
		turn1.setValue(10);
		Game game1 = new Game();
		game1.setFirstPlayer(Constants.COMPUTER);
		game1.setStatus(Constants.STARTED);		
		GameService gameService1 = Mockito.spy(gameService);
		Mockito.doReturn(game1).when(gameService1).createNewGame(Constants.COMPUTER);
		Mockito.doReturn(turn1).when(gameService1).computerTurn(0);
		Game result = gameService1.startNewGame(0);
		assertThat(result.getFirstPlayer())
	      .isEqualTo(Constants.COMPUTER);
	}	

	
	@Test
	public void testCreateNewGame() {
		doNothing().when(turnService).cleanTurns();
		doNothing().when(gameRepo).deleteAll();
		when(gameRepo.save(ArgumentMatchers.<Game>any())).thenAnswer(i -> i.getArguments()[0]);
		Game result = gameService.createNewGame(Constants.COMPUTER);
		assertThat(result.getFirstPlayer())
	      .isEqualTo(Constants.COMPUTER);
		assertThat(result.getStatus())
	      .isEqualTo(Constants.STARTED);
	}	

	@Test
	public void testHumanTurn_Finished() {
		Turn turn1 = new Turn();
		turn1.setPlayer(Constants.HUMAN);
		turn1.setValue(10);
		
		Game game1 = createTestGame();
		when(turnService.save(turn1)).thenReturn(turn1);
		GameService gameService1 = Mockito.spy(gameService);
		Mockito.doReturn(Constants.FINISHED).when(gameService1).checkGameStatus(turn1);
		Mockito.doReturn(game1).when(gameService1).updateGameStatusFinished(turn1.getPlayer());
		String result = gameService1.humanTurn(turn1);
		
		assertThat(result)
	      .isEqualTo(Constants.OK);
	}
	
	@Test
	public void testHumanTurn_notFinished() {
		Turn turn1 = new Turn();
		turn1.setPlayer(Constants.HUMAN);
		turn1.setValue(10);
		
		when(turnService.save(turn1)).thenReturn(turn1);
		GameService gameService1 = Mockito.spy(gameService);
		Mockito.doReturn(Constants.CONTINUE).when(gameService1).checkGameStatus(turn1);
		Mockito.doReturn(turn1).when(gameService1).computerTurn(turn1.getValue());
		String result = gameService1.humanTurn(turn1);
		
		assertThat(result)
	      .isEqualTo(Constants.OK);
	}	

	@Test
	public void testComputerTurn_Finished() {
		Turn turn1 = createTestTurn();
		Game game1 = createTestGame();
		when(turnService.doTurn(turn1.getValue())).thenReturn(turn1);
		GameService gameService1 = Mockito.spy(gameService);
		Mockito.doReturn(Constants.CONTINUE).when(gameService1).checkGameStatus(turn1);
		Mockito.doReturn(game1).when(gameService1).updateGameStatusFinished(turn1.getPlayer());
		Turn result = gameService1.computerTurn(turn1.getValue());
		
		assertThat(result.getPlayer())
	      .isEqualTo(turn1.getPlayer());
		assertThat(result.getValue())
	      .isEqualTo(turn1.getValue());
		verify(turnService, times(1)).doTurn(turn1.getValue());
	}		

	@Test
	public void testUpdateGameStatusFinished() {
		Game game1 = createTestGame();
		when(gameRepo.findTopByOrderByIdDesc()).thenReturn(game1);		
		when(gameRepo.save(ArgumentMatchers.<Game>any())).thenAnswer(i -> i.getArguments()[0]);
		Game result = gameService.updateGameStatusFinished(Constants.COMPUTER);
		assertThat(result.getStatus())
	      .isEqualTo(Constants.FINISHED);
		assertThat(result.getWinner())
	      .isEqualTo(Constants.COMPUTER);
	}		

	@Test
	public void testCheckGameStatus_Continue() {
		Turn turn1 = createTestTurn();
		String result = gameService.checkGameStatus(turn1);
		assertThat(result)
	      .isEqualTo(Constants.CONTINUE);
	}		
	
	@Test
	public void testCheckGameStatus_Finished() {
		Turn turn1 = createTestTurn();
		turn1.setValue(1);
		String result = gameService.checkGameStatus(turn1);
		assertThat(result)
	      .isEqualTo(Constants.FINISHED);
	}			
	
	private Turn createTestTurn() {
		Turn turn1 = new Turn();
		turn1.setPlayer(Constants.HUMAN);
		turn1.setValue(10);		
		return turn1;
	}

	private Game createTestGame() {
		Game game1 = new Game();
		game1.setFirstPlayer(Constants.HUMAN);
		game1.setStatus(Constants.STARTED);		
		game1.setWinner(Constants.HUMAN);
		return game1;
	}	
	
	
//	@Test
//	public void testDoTurn_value0() {
//		Turn turn1 = new Turn();
//		turn1.setPlayer(Constants.COMPUTER);
//		turn1.setValue(10);
//		when(turnRepo.save(ArgumentMatchers.<Turn>any())).thenAnswer(i -> i.getArguments()[0]);
//		
//		Turn result = turnService.doTurn(0);
//		assertThat(result.getValue())
//	      .isBetween(1, Constants.MAX_START_VALUE);
//	}
//	
//	@Test
//	public void testCreateNewHumanTurn() {
//		Turn turn1 = new Turn();
//		turn1.setPlayer(Constants.HUMAN);
//		Turn result = turnService.createNewHumanTurn();
//		assertThat(result.getPlayer())
//	      .isEqualTo(turn1.getPlayer());
//	}
//	
//	@Test
//	public void testSave() {
//		Turn turn1 = new Turn();
//		turn1.setPlayer(Constants.COMPUTER);
//		turn1.setValue(10);
//		when(turnRepo.save(ArgumentMatchers.<Turn>any())).thenAnswer(i -> i.getArguments()[0]);
//		
//		Turn result = turnService.save(turn1);
//		assertThat(result.getValue())
//	      .isEqualTo(turn1.getValue());
//		assertThat(result.getPlayer())
//	      .isEqualTo(turn1.getPlayer());
//	}
//
//	@Test
//	public void testValidateTurn_OK() {
//		Turn turn1 = new Turn();
//		turn1.setPlayer(Constants.COMPUTER);
//		turn1.setValue(10);
//		
//		Turn turn2 = new Turn();
//		turn2.setPlayer(Constants.HUMAN);
//		turn2.setValue(3);
//		when(turnRepo.findTopByOrderByIdDesc()).thenReturn(turn1);
//		
//		String result = turnService.validateTurn(turn2);
//		assertThat(result)
//	      .isEqualTo(Constants.OK);
//	}
//
//	@Test
//	public void testValidateTurn_null() {
//		Turn turn1 = null;
//		
//		Turn turn2 = new Turn();
//		turn2.setPlayer(Constants.HUMAN);
//		turn2.setValue(3);
//		when(turnRepo.findTopByOrderByIdDesc()).thenReturn(turn1);
//		
//		String result = turnService.validateTurn(turn2);
//		assertThat(result)
//	      .isEqualTo(Constants.OK);
//		
//		turn2.setValue(1);
//		result = turnService.validateTurn(turn2);
//		assertThat(result)
//	      .isEqualTo(Constants.FAILED);
//	}
	
	
}
