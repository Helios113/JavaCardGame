package structures;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Player;
import structures.basic.AI;
import utils.StaticConfFiles;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.UnitSpawner;
import java.lang.Math;
import java.util.ArrayList;

import structures.basic.UnitMove;
import structures.basic.Waiter;
import structures.tags.ExecuteTags;
import structures.tags.Who;
import controllers.EventHandler;
import controllers.PlayController;
import utils.UnitSpawner;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 *
 * @author Dr. Richard McCreadie
 * @author PRAMS
 *
 */
public class GameState {


	public boolean gameInitalised = false;

	//contains references to the board, players and event handlers 
	// and an ActorRef to communicate to front end
	private ActorRef out;
	private Player humanPlayer;
	private AI aiPlayer;
	private Player currentPlayer;
	private Player nextPlayer;
	private Board board;
	private int turn;	
	private UnitMove um;
	private EventHandler ev;
	private UnitSpawner us;
	// boolean to track whether front end events are blocked
	private boolean listenToFrontEnd;

	public GameState(){
		//odd turns are human
		this.turn = 1;
	}

	public void loadGame(ActorRef out) {

		this.out = out;
		listenToFrontEnd = true;
		loadBoard();
		loadPlayers();
		loadStartingDecks();
		BasicCommands.addPlayer1Notification(out,"Welcome Player 1!" , 3);
	}

	// a method to put all end of round action in one place
	public void nextTurn() {

		// update mana values
		PlayController.human = (turn%2==0);
		board.resetTiles();
		int newMana = Math.min(turn/2 + 2, Player.MAX_MANA);
		nextPlayer.setMana(newMana);
		currentPlayer.setMana(0);

		// deal the current player a card (it's the end of their turn)
		currentPlayer.dealCard();
		if (currentPlayer == humanPlayer) humanPlayer.drawHand();

		// increment turn + swap current/next Players
		this.turn++;
		Player newCurrentPlayer = nextPlayer;
		nextPlayer = currentPlayer;
		currentPlayer = newCurrentPlayer;
		removeTempTags();

		if(listenToFrontEnd == true) listenToFrontEnd = false;
		else {
			// wait for a bit in a separate thread before setting listenToFrontEnd=true
			// this lets any overhanging clicks from the browser during the ai turn get ignored
			Waiter w = new Waiter(this);
			Thread waitThread = new Thread(w);
			waitThread.start();

			if(turn%2==1) {
				BasicCommands.addPlayer1Notification(board.getGameState().getActorRef(), "Your Turn", 2);
			}
		}

	}

	public int getTurn(){
		return this.turn;
	}
	public Player getPlayer()
	{
		return humanPlayer;
	}
	public AI getAI()
	{
		return aiPlayer;
	}

	// helper methods for loading game elements
	protected void loadBoard() {
		this.board = new Board(this.out, this);
		um = new UnitMove(this.board);
		ev = new EventHandler(this.board);
		us = new UnitSpawner(this.board, ev);
		PlayController.gameState = this;
		Who.setEvenHandler(ev);
		ExecuteTags.setEvenHandler(ev);
	}

	public EventHandler getEventHandler() {
			return ev;
	}

	public Board getBoard() {
		return this.board;
	}

	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}

	public Player getNextPlayer() {
		return this.nextPlayer;
	}

	public UnitMove getUnitMove(){
		return this.um;
	}
	public boolean getListenToFrontEnd() {
		return listenToFrontEnd;
	}
	public void setListenToFrontEnd(boolean b) {
		this.listenToFrontEnd = b;
	}

	// helper method to create both players and spawn avatars
	protected void loadPlayers() {
		this.humanPlayer = new Player(20, 2,1, this);
		this.humanPlayer.setName("player");
		this.currentPlayer = this.humanPlayer;
		this.aiPlayer = new AI(20, 0, 0, this);
		this.aiPlayer.setName("ai");
		this.nextPlayer = this.aiPlayer;
		this.humanPlayer.setAvatar(us.spawnAvatar(out,"player"));
		this.aiPlayer.setAvatar(us.spawnAvatar(out, "AI"));
		ev.setPlayers(this.humanPlayer,this.aiPlayer);
		
		//gives avatars player references so that they update player health
		this.humanPlayer.getAvatar().setPlayer(this.humanPlayer);
		this.aiPlayer.getAvatar().setPlayer(this.aiPlayer);
	}

	// loads decks and hands
	protected void loadStartingDecks() {
		humanPlayer.loadDeck(StaticConfFiles.deck1);
		aiPlayer.loadDeck(StaticConfFiles.deck2);
		humanPlayer.dealStartingHand();
		humanPlayer.drawHand();
		aiPlayer.dealStartingHand();
	}
	
	// method for adding units into active play
	public void spawnUnit(int A, int x, int y)
	{
		Tile t = board.getTileReference(x, y);
		Card c = currentPlayer.getHand().getCard(A);
		getCurrentPlayer().addMana(-c.getManacost());
		this.us.spawnUnit(out,c,t);
		PlayController.Message(c.getCardname() + "!");
		currentPlayer.getHand().removeCard(A);
		if(currentPlayer.getIndex()==1){
			currentPlayer.drawHand();
		}
	}
	
	public ActorRef getActorRef() {
		return this.out;
	}

	// removes tags created over the course of the round so units can move etc. again
	public void removeTempTags() {
		ArrayList<Unit> units = this.getBoard().getUnitsOnBoard();

		for(Unit u: units) {
			u.removeTagByName("moved");
			u.removeTagByName("attacked");
			u.removeTagByName("spentAttack2");
			u.removeTagByName("new");
		}
	}
	
	public void endGame() {
		this.setListenToFrontEnd(true);
		board.endGame();
		this.setListenToFrontEnd(false);
		BasicCommands.addPlayer1Notification(out, "GAME OVER", 1000);

	}
}
