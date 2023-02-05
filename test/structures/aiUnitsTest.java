import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import structures.GameState;
import structures.basic.Board;
import structures.basic.Unit;
import structures.basic.AI;
import structures.basic.ai.*;
import structures.basic.Player; 


public class aiUnitsTest {

 
	@Test
	public void checkAiBestAttack() {
		
		 GameState gameState = new GameState();
		 gameState.loadGame(null);
		 
		 // check AI Unitar is on board
		 ArrayList<Unit> units = gameState.getBoard().getUnitsOnBoard();
		 
		 Boolean avatarFound = false;
		 
		 // checks through units on initialised board to make sure avatar is there
		 for (int i = 0; i < units.size(); i++) {
			 
			 if (units.get(i).checkTag("enemy_avatar")) {
				 avatarFound = true;
				 Unit avatar = units.get(i);
			 }
		 }
		 assertTrue(avatarFound);
	}
	
	 @Test
	public void checkUnitSpawn() {
		
		 GameState gameState = new GameState();
		 gameState.loadGame(null);
		 
		 // check AI Unitar is on board
		 ArrayList<Unit> units = gameState.getBoard().getUnitsOnBoard();
		 
		 Boolean avatarFound = false;
		 Unit avatar = null; 
		 
		 // checks through units on initialised board to make sure avatar is there
		 for (int i = 0; i < units.size(); i++) {
			 
			 if (units.get(i).checkTag("enemy_avatar")) {
				 avatarFound = true;
				avatar = units.get(i);
			 }
		 }
		 
		 // place weak comodo charger unit in front and check if avatar attacks
		 gameState.spawnUnit(1, 6, 2);
		 
		 assertFalse(gameState.getBoard().getTileReference(6, 2).getUnit() == null); 
		 
		
		 
		 

	}
	 @Test
	 public void checkAvatarAttack() {
	 GameState gameState = new GameState();
	 gameState.loadGame(null);
	 
	 // check AI Unitar is on board
	 ArrayList<Unit> units = gameState.getBoard().getUnitsOnBoard();
	 
	 Boolean avatarFound = false;
	 Unit avatar = null; 
	 
	 // checks through units on initialised board to make sure avatar is there
	 for (int i = 0; i < units.size(); i++) {
		 
		 if (units.get(i).checkTag("enemy_avatar")) {
			 avatarFound = true;
			avatar = units.get(i);
		 }
	 }
	 
	 // place weak comodo charger unit in front and check if avatar attacks
	 gameState.spawnUnit(1, 6, 2);
	 
	 AI ai = gameState.getAI();
	 ai.takeTurn();
	 
	 // comodo charger should have taken damage
	 assertTrue(gameState.getBoard().getTileReference(6, 2).getUnit().getHealth() < 6);
	 
	 

}

}