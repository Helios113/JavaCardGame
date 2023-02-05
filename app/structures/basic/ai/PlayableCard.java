package structures.basic.ai;

import java.util.ArrayList;
import java.util.List;

import play.core.formatters.Multipart.ByteStringFormatter;
import structures.basic.Hand;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Card;

/**
* utility class used by the AI to register that a card is playable this turn
* associates a card reference with its hand position and a score of how good a move it is.
* @author Anna Smeaton
*/

public class PlayableCard implements Comparable<PlayableCard>{

    private Card card;
    private int handPosition;
    private int score;
    private Tile targetTile;

    public PlayableCard(Card card, int handPosition) {
        this.card = card;
        this.handPosition = handPosition;
    }

    public Card getCard(){
        return card;
    }

    public int getHandPosition(){
        return handPosition;
    }

    public void setScore(int s){
        this.score = s;
    }

    public int getScore(){
        return score;
    }

    public void setTargetTile(Tile t) {
    	targetTile = t;
    }

    public Tile getTargetTile() {
    	return targetTile;
    }

    // implementing Comparable to help in finding the best 
    @Override
    public int compareTo(PlayableCard c) {
    	if (this.getScore() == c.getScore()) {
    		return 0;
    	}
    	else if (this.getScore() > c.getScore()) {
    		return 1;
    	}
    	else {
    		return -1;
    	}
    }

    // toString for testing purposes - AS
    public String toString() {
    	return String.format("Card: %s. Score: %d. Tile: %d, %d", card.getCardname(), score, targetTile.getTilex(), targetTile.getTiley() );
    }
}
