package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.Constants;
import com.example.entity.Game;
import com.example.entity.Turn;
import com.example.service.GameService;
import com.example.service.TurnService;

@Controller
public class AppController {
	@Autowired
	private GameService gameService;
	@Autowired
	private TurnService turnService;
	
	@RequestMapping("/")
	public String viewHomePage(Model model) {
		return "index";
	}

	@RequestMapping(value="/newGame/{firstPlayer}")
	public String startGame(@PathVariable(name = "firstPlayer") int id) {
		gameService.startNewGame(id);
		return "redirect:/turns";
	}

	@RequestMapping("/turns")
	public String viewTurns(Model model) {
		Game game = gameService.getLastGame();
		model.addAttribute("game", game);
		List<Turn> listTurns = turnService.listAll();
		model.addAttribute("listTurns", listTurns);
		Turn newTurn = turnService.createNewHumanTurn();
		model.addAttribute("turn", newTurn);
		return "turns";
	}
	
	@RequestMapping(value= "/saveHumanTurn", method = RequestMethod.POST)
	public String saveHumanTurn(@ModelAttribute("turn") Turn turn) {
		String turnStatus = turnService.validateTurn(turn);
		if (turnStatus == Constants.FAILED)
			return "turnFailed";
		gameService.humanTurn(turn);
		return "redirect:/turns";
	}	
}
