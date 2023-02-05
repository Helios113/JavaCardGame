package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.ValidMoveAttackController;
import utils.BasicObjectBuilders;
import java.lang.Thread.*;
import java.util.ArrayList;
import structures.basic.Taggable;

import javax.swing.text.html.HTML.Tag;

import utils.UnitSpawner;
/**   
 *
 * 	This class provides methods for controlling the visuals of the Board on the screen. It will update game screen with valid move and
 *  attack options.
 *	@author Stuart Miller
 */

public class Board {

	private Tile[][] tileReferences;
	private ActorRef out;
	private GameState gameState;
	private ArrayList<Unit> unitsOnBoard;
	public static final int boardWidth = 9;
	public static final int boardHeight = 5;

	/**
	 * board constructor. Will create Tile objects for the game to be played on and draw to front end
	 * @param out 
	 * @param gameState 
	 */

	public Board(ActorRef out, GameState gameState) {
		this.gameState = gameState;
		tileReferences = new Tile[boardWidth][boardHeight];
		this.out = out;
		unitsOnBoard = new ArrayList<>();

		//draw all initial board and store references
		for(int i = 0; i < tileReferences.length; i++) {
			for(int j = 0; j< tileReferences[i].length; j++) {
				tileReferences[i][j] = BasicObjectBuilders.loadTile(i,j);
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
				BasicCommands.drawTile(out, tileReferences[i][j], 0);
			}
		}
	}


	public ArrayList<Unit> getUnitsOnBoard(){
		return this.unitsOnBoard;
	}
	public void addUnit(Unit u) {
		this.unitsOnBoard.add(u);
	}
	public void removeUnit(Unit u) {
		this.unitsOnBoard.remove(u);
	}
	public Tile getTileReference(int x, int y) {
		return tileReferences[x][y]; 
		
	}
	public GameState getGameState() {
		return this.gameState;
	}


	/**
	 * Calls ValidMoveAttackController for valid options. VMAC will update Tile.mode with these options. This will call
	 * update board to change the front end.
	 * @param Taggable Card that you want options displayed for
	 * @return boardAsInt move and attack options displayed as ints for ai to consider moves
	 */
	public int[][] highlightOptions(Taggable t) {
		resetTiles(); 
		boolean options = false;
		if(t.checkTag("spell")) {
			options = ValidMoveAttackController.highlightSpellTargets(this, t);
		}else if(t.checkTag("airdrop")) {
			options = ValidMoveAttackController.highlightSpawn(this, "airdrop");
		}else {
			options = ValidMoveAttackController.highlightSpawn(this, "");
		}
		System.out.println("In Board class options:" + options);
		if(options = false) return null;
		else {
			updateBoard();
			return boardAsInt(-1,-1);
		}
	}


	/**
	 * This method can be called to show move options of a Unit at x, y
	 * @param t object of Unit to move
	 * @param x x-position of unit
	 * @param y y-position of unit
	 * @return boardAsInt move and attack options displayed as ints for ai to consider moves
	 */

	public int[][] highlightOptions(Unit t, int x, int y) {

		resetTiles(); 
		System.out.println("highlightOptions: "+ x + ":" +y);
		boolean options = ValidMoveAttackController.validMoves(this, x, y, t);
		updateBoard();
		System.out.println("options:" + options);
		if(!options ) {
			System.out.println("highlight options returning null");
			return null;
		}
		else {
			System.out.println("highlight options returning Object");
			return boardAsInt(x,y);
		}
	}

	/**
	 * This will return an int[][] depicting options that a unit can play. Starting position is depicted by -1.
	 * Used by the AI
	 * @param x starting x position
	 * @param y starting y position
	 * @return intBoard int depiction of valid move and attack options from starting point given
	 */
	private int[][] boardAsInt(int x, int y){

		int[][] intBoard = new int[9][5];

		for(int i = 0; i< intBoard.length ; i++) {
			for(int j = 0; j < intBoard[i].length ; j++) {
				intBoard[i][j] = getTileReference(i,j).getMode();
			}
		}
		if(x>=0 && y>=0) intBoard[x][y] = -1;
		return intBoard;
	}

	/**
	 * This method can be called to show options for a Unit with location not given (used by AI)
	 * @param u the unit you want to show moves for
	 * @return options int depiction of valid move and attack options from starting point given
	 */

	public int[][] highlightOptions(Unit u) {

		Tile t = findUnitOnBoard(u);
		if(t == null) return null;

		int[][] options = highlightOptions(u, t.getTilex(), t.getTiley());
		return options;
	}

	/**
	 * This method will check for updates to Tile.mode, and update front end if it is the human's
	 */

	private void updateBoard() {
		if(gameState.getListenToFrontEnd() == true) {
			for(int i = 0; i < tileReferences.length; i++) {
				for(int j = 0; j < tileReferences[i].length; j++) {
					BasicCommands.drawTile(out, getTileReference(i,j), getTileReference(i,j).getMode());
					try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}
	}

	/**
	 * This method will reset all the tile modes back to 0 (standard) and call the front end to visualise this.
	 */

	public void resetTiles() {
		for(int i = 0; i<tileReferences.length; i++) {
			for(int j = 0; j<tileReferences[i].length; j ++) {
				tileReferences[i][j].setMode(0);
			}
		}
		updateBoard();
	}

	/**
	 * Helper method to find position of Unit on board
	 * @param unit to find
	 * @return tile reference where the unit is
	 */
	public Tile findUnitOnBoard(Unit u) {

		for(int i = 0 ; i<tileReferences.length; i++) {
			for(int j = 0; j<tileReferences[i].length; j++) {
				try {
					if(tileReferences[i][j].getUnit().equals(u)) {
						return tileReferences[i][j];
					}
				}catch(NullPointerException e) {}
			}
		}
		return null;
	}

	/**
	 * method will turn the board red
	 */
	public void endGame() {
		for(int i = 0; i<tileReferences.length; i++) {
			for(int j = 0; j<tileReferences[i].length; j ++) {
				tileReferences[i][j].setMode(2);
			}
		}
		updateBoard();
	}
}
