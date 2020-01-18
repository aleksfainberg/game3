package com.example.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Constants;
import com.example.entity.Turn;
import com.example.repository.TurnRepository;

@Service
public class TurnService {
	private static final Random random = new Random();

	@Autowired
	private TurnRepository repo;
	
	public List<Turn> listAll(){
		return repo.findAll();
	}

	public Turn doTurn(int i) {
		Turn newTurn = new Turn();
		newTurn.setPlayer(Constants.COMPUTER);
		if (i==0)
			newTurn.setValue(random.nextInt(Constants.MAX_START_VALUE));
		else {
			newTurn.setValue((int)Math.round(((double) i) / 3));
		}
		//newTurn = repo.save(newTurn);
		return repo.save(newTurn);
	}

	public void cleanTurns() {
		repo.deleteAll();
	}

	public Turn createNewHumanTurn() {
		Turn turn = new Turn();
		turn.setPlayer(Constants.HUMAN);
		return turn;
	}

	public Turn save(Turn turn) {
		return repo.save(turn);
	}

	public String validateTurn(Turn turn) {
		Turn lastTurn = repo.findTopByOrderByIdDesc();
		if (lastTurn == null)
			if (turn.getValue()<2)
				return Constants.FAILED;
			else
				return Constants.OK;
		if (turn.getValue() * 3 == lastTurn.getValue() + 1 ||
			turn.getValue() * 3 == lastTurn.getValue() ||
			turn.getValue() * 3 == lastTurn.getValue() - 1) 
			return Constants.OK;
		return Constants.FAILED;
	}
	
}
