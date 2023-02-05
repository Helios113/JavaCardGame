package structures.basic;

import akka.actor.ActorRef;
import structures.GameState;
import commands.BasicCommands;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * A basic representation of of the Player. A player
 * has health and mana.
 *
 * This class also holds internal information
 * about the player and handles updating the front end
 * when a change occurs.
 *
 * @author Dr. Richard McCreadie
 * @author Preslav Aleksandrov
 */
public class Player {

	int health = 0;
	int mana = 0;
	Deck deck;
	protected Hand hand;
	protected Unit avatar;
	protected String name = "";
	protected int index;
	protected ActorRef out;
	public final static int STARTING_HAND_SIZE = 3;
	public final static int MAX_MANA = 9;
	private GameState gameState;


	// constructor containing an index of which player this is. 
	// Currently only 0 or 1 but in future games could be expanded
	public Player(int health, int mana, int index, GameState gameState) {
		super();
		this.out = gameState.getActorRef();
		this.health = health;
		this.mana = mana;
		this.index = index;
		this.setHealth(health);
		this.setMana(mana);
		this.deck = new Deck();
		this.hand = new Hand();
		this.gameState = gameState;
	}
	@JsonIgnore
	public void setAvatar(Unit avatar)
	{
		this.avatar = avatar;
	}
	@JsonIgnore
	public Unit getAvatar()
	{
		return avatar;
	}

	public int getHealth() {
		return health;
	}
	@JsonIgnore
	public Hand getHand()
	{
		return hand;
	}
	public void setHealth(int health) {
		//clip lower limit to 0
		if (health < 0) health = 0;
		this.health = health;

		// update the player health display in front end
		if (this.index == 1)
			BasicCommands.setPlayer1Health(out, this);
		else
			BasicCommands.setPlayer2Health(out, this);

		// give the front end time to do
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		if (this.index == 1)
			BasicCommands.setPlayer1Mana(out, this);
		else
			BasicCommands.setPlayer2Mana(out, this);
	}
	public void addMana(int mana) {
		this.mana += mana;
		System.out.println("Mana"+this.mana);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		if (this.index == 1)
			BasicCommands.setPlayer1Mana(out, this);
		else
			BasicCommands.setPlayer2Mana(out, this);

	}

	public void loadDeck(String[] deckNameList) {
		for (int i = 0; i < deckNameList.length; i++) {
			Card thisCard = BasicObjectBuilders.loadCard(deckNameList[i], 0, Card.class);
			this.deck.addCard(thisCard);
		}

	}

	public void dealCard() {
		// draw the next card from the deck
		Card newCard = deck.drawCard();

		if (newCard == null) {

			gameState.endGame();
			return;
		}

		// add it to the hand if there is room
		boolean successfullyAdded = hand.addCard(newCard);
		if(!successfullyAdded) {
			// message the player to say their card was lost
			BasicCommands.addPlayer1Notification(out, "Your hand is full!", 1);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.addPlayer1Notification(out, "You burned a card!", 1);
		}
	}
	public void drawCards(int n)
	{
		for (int i = 0; i < n; i++) {
			this.dealCard();
		}
		if(this.index == 1)
		{
			drawHand();
		}
	}
	public void dealStartingHand() {
		for (int i = 0; i < STARTING_HAND_SIZE; i++) {
			this.dealCard();
		}
	}

	public void drawHand() {
		hand.drawHand(out);
	}
	@JsonIgnore
	public void setName(String name)
	{
		this.name = name;
	}
	@JsonIgnore
	public String getName()
	{
		return name;
	}
	@JsonIgnore
	public void setIndex(int i)
	{
		this.index = i;
	}
	@JsonIgnore
	public int getIndex()
	{
		return index;
	}



}
