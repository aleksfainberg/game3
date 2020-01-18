package com.example.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.example.Constants;
import com.example.entity.Game;
import com.example.entity.Turn;
import com.example.service.GameService;
import com.example.service.TurnService;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class AppControllerTest {

	@Autowired
	@InjectMocks
	AppController controller;
	
	@Mock
	private GameService gameService;

	@Mock
	private TurnService turnService;
	
	@BeforeAll
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	void testViewHomePage() {
		Model model = new ExtendedModelMap();
		String result = controller.viewHomePage(model);
		assertThat(result)
	      .isEqualTo("index");
	}

	@Test
	void testStartGame() {
		Game game1 = new Game();
		when(gameService.startNewGame(0)).thenReturn(game1);
		String result = controller.startGame(0);
		assertThat(result)
	      .isEqualTo("redirect:/turns");
		verify(gameService, times(1)).startNewGame(0);
	}

	@Test
	void testViewTurns() {
		Turn turn1 = new Turn();
		List<Turn> listTurns1 = new ArrayList<Turn>();
		Game game1 = new Game();
		Model model1 = new ExtendedModelMap();
		when(gameService.getLastGame()).thenReturn(game1);
		when(turnService.listAll()).thenReturn(listTurns1);
		when(turnService.createNewHumanTurn()).thenReturn(turn1);		
		String result = controller.viewTurns(model1);
		assertThat(result)
	      .isEqualTo("turns");
		verify(gameService, times(1)).getLastGame();
		verify(turnService, times(1)).listAll();
		verify(turnService, times(1)).createNewHumanTurn();		
	}

	
	@Test
	void testSaveHumanTurn() {
		Turn turn1 = new Turn();
		when(turnService.validateTurn(turn1)).thenReturn(Constants.FAILED);
		String result = controller.saveHumanTurn(turn1);
		assertThat(result)
	      .isEqualTo("turnFailed");		
		
		when(turnService.validateTurn(turn1)).thenReturn(Constants.OK);
		when(gameService.humanTurn(turn1)).thenReturn(Constants.OK);
		result = controller.saveHumanTurn(turn1);
		assertThat(result)
	      .isEqualTo("redirect:/turns");		
		verify(gameService, times(1)).humanTurn(turn1);
		verify(turnService, times(2)).validateTurn(turn1);
	}

}
