package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Constants;
import com.example.entity.Game;
import com.example.entity.Turn;
import com.example.repository.GameRepository;

@Service
public class GameService {

	@Autowired
	private GameRepository repo;
	
	@Autowired
	private TurnService turnService;
	
	public List<Game> listAll() {
		return repo.findAll();
	}
	
	public Game startNewGame(int i) {
		String firstPlayer;
		if (i==0) 
			firstPlayer=Constants.COMPUTER;
		else 
			firstPlayer=Constants.HUMAN;
		Game game = createNewGame(firstPlayer);
		if (i==0)
			computerTurn(0);
		return game;
	}
	
	public Game createNewGame(String firstPlayer) {
		turnService.cleanTurns();
		repo.deleteAll();
		Game game = new Game();
		game.setFirstPlayer(firstPlayer);
		game.setStatus(Constants.STARTED);
		return repo.save(game);
	}

	public String humanTurn(Turn turn) {
		turn = turnService.save(turn);
		String status = checkGameStatus(turn);
		if (status == Constants.FINISHED)
			updateGameStatusFinished(turn.getPlayer());
		else {
			computerTurn(turn.getValue());
		}
		return Constants.OK;
	}

	public Turn computerTurn(int lastValue) {
		Turn turn = turnService.doTurn(lastValue);
		String status = checkGameStatus(turn);
		if (status == Constants.FINISHED)
			updateGameStatusFinished(turn.getPlayer());
		return turn;
	}
	
	Game updateGameStatusFinished(String winner) {
		Game game = repo.findTopByOrderByIdDesc();
		game.setStatus(Constants.FINISHED);
		game.setWinner(winner);
		return repo.save(game);
	}

	String checkGameStatus(Turn turn) {
		if (turn.getValue() == 1)
			return Constants.FINISHED;
		return Constants.CONTINUE;
	}

	public Game getLastGame() {
		return repo.findTopByOrderByIdDesc();
	}

}
