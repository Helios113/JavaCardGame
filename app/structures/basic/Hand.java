package structures.basic;

import akka.actor.ActorRef;
import java.util.ArrayList;
import java.util.List;

import commands.BasicCommands;

/**
* a List of Card references in the Player's Hand.
* @author Mike Parr-Burman
*/
public class Hand {
    private List<Card> cards;
    public static final int MAX_CARDS = 6;

    public Hand() {
        cards = new ArrayList<Card>();
    }

    /**
    * add a card to the hand if there is space.
    * returns a boolean indicating if the card was successfully added
    */
    public Boolean addCard(Card card) {
        if (cards.size() >= MAX_CARDS)
            return false;
        cards.add(card);
        return true;
    }

    /**
    * remove the card at this position from the deck
    * takes the 'front end' position (1-indexed) as input
    * not the 0 indexed List position
    */
    public void removeCard(int position) {
    	// 
        cards.remove(position-1);
    }

    /**
    * draw this hand in Player1's visible slots in the GUI
    */
    public void drawHand(ActorRef out) {
        for (int i = 0; i < MAX_CARDS; i++) {
            if (i < cards.size())
                BasicCommands.drawCard(out, cards.get(i), i+1, 0);
            else
                // if the deck is smaller now than on last draw,
                // make sure any overhanging cards are cleared
                BasicCommands.deleteCard(out, i+1);
        }
    }

    /**
    * get the Card at this position
    * takes the 'front end' position (1-indexed) as input
    * not the 0 indexed List position
    */
    public Card getCard(int position) {
    	return cards.get(position - 1);
    }

    public int getSize() {
        return cards.size();
    }
}
