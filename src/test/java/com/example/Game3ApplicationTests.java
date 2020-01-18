package com.example;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.controller.AppController;
import com.example.entity.Turn;

@SpringBootTest
@AutoConfigureMockMvc
class Game3ApplicationTests {

    @Autowired
    AppController appController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHomePage() throws Exception{
    	this.mockMvc.perform(get("/"))
    		.andExpect(status().isOk())
    		.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML_VALUE))
    		.andExpect(view().name("index"));
    }
    
    @Test
    public void testStartGame() throws Exception{
    	this.mockMvc.perform(get("/newGame/0"))
    		.andExpect(status().is3xxRedirection())
    		.andExpect(view().name("redirect:/turns"));
    }

    @Test
    public void testViewTurns() throws Exception{
    	this.mockMvc.perform(get("/turns"))
    		.andExpect(status().isOk())
    		.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML_VALUE))
    		.andExpect(view().name("turns"))
    		.andExpect(model().attributeExists("game"))
    		.andExpect(model().attributeExists("listTurns"))
    		.andExpect(model().attributeExists("turn"));
    }
    
    @Test
    public void testSaveHumanTurn() throws Exception{
    	Turn turn1 = new Turn();
    	turn1.setPlayer(Constants.HUMAN);
    	turn1.setValue(2);
    	this.mockMvc.perform(post("/saveHumanTurn").flashAttr("turn", turn1))
    		.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML_VALUE))
    		.andExpect(status().isOk())
    		.andExpect(view().name("turnFailed"));
    }
}

