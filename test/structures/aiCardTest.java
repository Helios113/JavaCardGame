import static org.junit.Assert.*;
import org.junit.Test;

import utils.StaticConfFiles;
import utils.BasicObjectBuilders;
import structures.GameState;
import akka.actor.ActorRef;
import structures.basic.ai.*;
import structures.basic.Hand;
import structures.basic.Card;
import java.util.ArrayList;

public class aiCardTest {

    /**
    * correctly filter cards outside mana budget
    */
    @Test
    public void checkAiCardSelection1() {
        int avatarHealth = 10;
        int manaBudget = 4;
        //test hand config to load
        String[] testDeck = {
            StaticConfFiles.c_entropic_decay, // mana cost 5 - NOT IN BUDGET
            StaticConfFiles.c_pyromancer, // mana cost 2
            StaticConfFiles.c_serpenti, // mana cost 6 - NOT IN BUDGET
            StaticConfFiles.c_bloodshard_golem, // mana cost 3
        };
        // these cards are within the mana budget
        ArrayList<String> resultDeck = new ArrayList<String>();
        resultDeck.add("Pyromancer");
        resultDeck.add("Bloodshard Golem");
        // by our algorithm Pyromancer is the best of these as it has ranged + b-gol has no special abilities
        String best = "Pyromancer";

        checkAiCardSelection(avatarHealth, manaBudget, testDeck, resultDeck, best);
    }

    /**
    * behaviour when no cards are affordable
    */
    @Test
    public void checkAiCardSelection2() {
        int avatarHealth = 10;
        int manaBudget = 0;
        //test hand config to load
        String[] testDeck = {
            StaticConfFiles.c_entropic_decay, // mana cost 5 - NOT IN BUDGET
            StaticConfFiles.c_pyromancer, // mana cost 2 - NOT IN BUDGET
            StaticConfFiles.c_serpenti, // mana cost 6 - NOT IN BUDGET
            StaticConfFiles.c_bloodshard_golem, // mana cost 3 - NOT IN BUDGET
        };
        // no cards are within the mana budget
        ArrayList<String> resultDeck = new ArrayList<String>();
        // no cards to pick a best one from
        String best = null;

        checkAiCardSelection(avatarHealth, manaBudget, testDeck, resultDeck, best);
    }

    /**
    * compare between units with no special abilities
    */
    @Test
    public void checkAiCardSelection3() {
        int avatarHealth = 10;
        int manaBudget = 10;
        //test hand config to load
        String[] testDeck = {
            StaticConfFiles.c_comodo_charger, // mana cost 2; attack 1
            StaticConfFiles.c_hailstone_golem, // mana cost 6; attack 4
        };
        // all cards are within the mana budget
        ArrayList<String> resultDeck = new ArrayList<String>();
        resultDeck.add("Comodo Charger");
        resultDeck.add("Hailstone Golem");
        // absent special abilities, best one is the one with highest attack
        String best = "Hailstone Golem";

        checkAiCardSelection(avatarHealth, manaBudget, testDeck, resultDeck, best);
    }

    /**
    * pick the best special ability
    */
    @Test
    public void checkAiCardSelection4() {
        int avatarHealth = 9;
        int manaBudget = 10;
        //test hand config to load
        String[] testDeck = {
            StaticConfFiles.c_sundrop_elixir, // mana cost 1; can boost avatar Health
            StaticConfFiles.c_silverguard_knight, // mana cost 3; provoke
        };
        // all cards are within the mana budget
        ArrayList<String> resultDeck = new ArrayList<String>();
        resultDeck.add("Sundrop Elixir");
        resultDeck.add("Silverguard Knight");
        // seeing as our avatar health is low enough to take advantage of the full sundrop health boost, it scores highest
        String best = "Sundrop Elixir";

        checkAiCardSelection(avatarHealth, manaBudget, testDeck, resultDeck, best);
    }

    /**
    * similar to before, but avatar has full health
    * should now not play sundrop elixir
    */
    @Test
    public void checkAiCardSelection5() {
        int avatarHealth = 20;
        int manaBudget = 10;
        //test hand config to load
        String[] testDeck = {
            StaticConfFiles.c_sundrop_elixir, // mana cost 1; can boost avatar Health
            StaticConfFiles.c_comodo_charger, // mana cost 2; attack 1; no special
        };
        // we already have full health and can't get the benefit of Sundrop
        // so it's not considered 'playable' - better saving it for later
        ArrayList<String> resultDeck = new ArrayList<String>();
        resultDeck.add("Comodo Charger");

        String best = "Comodo Charger";

        checkAiCardSelection(avatarHealth, manaBudget, testDeck, resultDeck, best);
    }

    // *************************************************************************

    /**
    * does the repeated testing work
    */
    private void checkAiCardSelection(int avatarHealth, int manaBudget, String[] testDeck, ArrayList<String> resultDeck, String best) {

        GameState gameState = new GameState();
        gameState.loadGame(null);
        Brain brain = new Brain(gameState);

        Hand testHand = testHandLoader(testDeck);
        ArrayList<PlayableCard> playableCards = brain.getPlayableCards(testHand, manaBudget, avatarHealth);

        // same number of cards as sample result?
        boolean cardsMatch = (playableCards.size() == resultDeck.size());

        // same names?
        for (PlayableCard pc : playableCards) {
            if (!resultDeck.contains(pc.getCard().getCardname())) {
                cardsMatch = false;
                System.out.println("card in wrong place: " + pc.getCard().getCardname());
            };
        }
        assertTrue(cardsMatch);

        // is the card selected 'best' correct?
        boolean bestCardMatch = false;
        PlayableCard bestPC = brain.decideBestCardPlay(playableCards);
        if (bestPC == null) {
            bestCardMatch = (best == null);
        } else {
            bestCardMatch = bestPC.getCard().getCardname().equals(best);
        }
        assertTrue(bestCardMatch);
    }

    private Hand testHandLoader(String[] TestDeckConf) {
        Hand hand = new Hand();
        for (String card : TestDeckConf) {
            hand.addCard(BasicObjectBuilders.loadCard(card, 0, Card.class));
        }
        return hand;
    }

}
