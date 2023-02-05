package structures.basic;

import java.util.ArrayList;

import controllers.PlayController;
import structures.GameState;
import structures.basic.ai.*;
import akka.actor.ActorRef;
import commands.BasicCommands;

/**
 * An AI Player class to hold AI functionality for challenging opposition.
 * @author Anna Smeaton
 * @author Mike Parr-Burman
 */

public class AI extends Player {

     GameState gameState;
     Brain brain;

    // constructor including "Brain" for calculated decisions
    public AI(int health, int mana, int index, GameState gameState) {
        super(health, mana, index, gameState);
        this.index = index;
        this.gameState = gameState;
        brain = new Brain(gameState);
    }

    /**
    * takeTurn controls the sequence of an AI turn
    */
    public void takeTurn() {

        System.out.println("AI taking a turn");
        // pauses for a second so the action isn't instantaneous
        brain.think();
        // trigger all the card related things
        playCards();
        brain.think();
        // play unit actions
        playUnits();
        // end the current turn
        gameState.nextTurn();
    }

    /**
    * AI sequencing and logic relating to the playing of cards.
    */
    private void playCards() {
        // get all the cards that can legally be played now
        ArrayList<PlayableCard> playableCards = brain.getPlayableCards(this.hand, this.mana, this.health);

        // System.out.println(playableCards);

        // if we have at least 1 playable card
       while(playableCards.size() > 0) {
            // get the best card to play
            PlayableCard best = brain.decideBestCardPlay(playableCards);
            // System.out.println("AI best card is:" + best);
            // System.out.println(String.format("I will play spell on %d,%d",best.getTargetTile().getTilex(), best.getTargetTile().getTiley() ));

            // call PlayController to play the card
            PlayController.handlePlay(best.getHandPosition()); // select card
            
            // if card is a spell give the player a heads up to look for the effect
            if (hand.getCard(best.getHandPosition()).checkTag("spell")) {
            	BasicCommands.addPlayer1Notification(gameState.getActorRef(), "AI casts a spell", 2);
            }
            PlayController.handlePlay(best.getTargetTile().getTilex(), best.getTargetTile().getTiley()); //select target tile

            //sleep for a bit to allow the animation to take place
            try {Thread.sleep(brain.ACTION_WAIT_TIME);} catch (InterruptedException e) {e.printStackTrace();}

            // refresh playable cards before the next while iteration
            playableCards = brain.getPlayableCards(this.hand, this.mana, this.health);

        }
   }

   /**
   * all AI turn sequencing relating to the selecting and playing of units
   */
    private void playUnits() {

        // get friendly Units
        ArrayList<Unit> friendlyUnits = filterFriendlyUnits(gameState.getBoard().getUnitsOnBoard());

  		// for each friendly unit (leaving avatar last)
        for (int i = friendlyUnits.size() - 1; i >= 0; i--) {
        	// as long as this unit can still do a thing
        	Unit unit = friendlyUnits.get(i);
            while (gameState.getBoard().highlightOptions(unit) != null) {
                // execute the best thing
  			    brain.executeBestAction(unit);
            }
        }
    }

    private ArrayList<Unit> filterFriendlyUnits(ArrayList<Unit> inputUnits) {
        // make a bucket
        ArrayList<Unit> friendlies = new ArrayList<Unit>();

        for (Unit unit : inputUnits) {
            // if they belong to ai throw them in the bucket
            if (unit.getOwner().equals("ai")) {
                friendlies.add(unit);
            }
        }
        System.out.println("AI got the friendly units: " + friendlies);
        return friendlies;
    }

}
