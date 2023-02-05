package structures.basic;

import java.util.ArrayList;
import java.util.List;

/**
* A list of Card objects, representing a Player's Deck.
* Last in first out - like stacking cards on a table.
* @author Mike Parr-Burman
*/

public class Deck {

    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<Card>();
    }

    /**
    * adds a card to the 'top' of the deck
    * LIFO - the last card added is first to be drawn
    */
    public void addCard(Card c) {
        cards.add(c);
    }

    /**
    * returns a reference to the top card in the deck, but leaves it there
    * returns null if deck is empty
    * LIFO - the last card added is first to be drawn
    */
    public Card peekCard() {
        int size = cards.size();

        if (size == 0)
            return null;

        return cards.get(size - 1);

    }

    /**
    * returns the top card in the deck, and removes it from the deck
    * retuns null if deck is empty
    * LIFO - the last card added is first to be drawn
    */
    public Card drawCard() {
        // get reference to the top card
        Card topCard = this.peekCard();

        // remove it from the deck
        if (topCard != null)
            cards.remove(cards.size() - 1);

        return topCard;

    }

}
