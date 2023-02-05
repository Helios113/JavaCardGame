import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import utils.UnitSpawner;
import controllers.EventHandler;
import controllers.PlayController;
import structures.basic.Card;
import structures.basic.Player;
import structures.tags.ExecuteTags;

/**
 * Test functions of various spells and the underlying tag system
 * @author Preslav Aleksandrov
 *
 */
public class spellTest {

    /**
     * This test simply checks that a boolean vairable is set in GameState when we
     * call the
     * initalize method for illustration.
     */
    @Test
    public void initGame() {

        // First override the alt tell variable so we can issue commands without a
        // running front-end
        CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell(); // create an alternative tell
        BasicCommands.altTell = altTell; // specify that the alternative tell should be used

        // As we are not starting the front-end, we have no GameActor, so lets manually
        // create
        // the components we want to test
        GameState gameState = new GameState(); // create state storage
        Initalize initalizeProcessor = new Initalize(); // create an initalize event processor

        assertFalse(gameState.gameInitalised); // check we have not initalized

        // lets simulate recieveing an initalize message
        ObjectNode eventMessage = Json.newObject(); // create a dummy message
        initalizeProcessor.processEvent(null, gameState, eventMessage); // send it to the initalize event processor

        assertTrue(gameState.gameInitalised); // check that this updated the game state

        // create a player
        Player P1 = new Player(100, 100, 1, gameState);
        //test we can assign health to player
        assertTrue(P1.getHealth()==100);

        // create a dummy unit
        Unit U1 = (Unit) BasicObjectBuilders.loadUnit(StaticConfFiles.u_ironcliff_guardian, 0, Unit.class);
        //check if tags loaded fine
        U1.setMaxHealth(10);
        U1.setHealth(10,false);
        U1.setMaxDamage(2);
        U1.setDamage(2, false);
        assertTrue(U1.checkTag("provoke"));

        // load a deck
        P1.loadDeck(new String[] { "conf/gameconfs/cards/2_c_s_staff_of_ykir.json",
                "conf/gameconfs/cards/2_c_s_entropic_decay.json",
                "conf/gameconfs/cards/1_c_s_sundrop_elixir.json" });
        P1.dealStartingHand();
        Card c = P1.getHand().getCard(3);
        //check if card is correct
        assertTrue(c.getCardname().equals("Staff of Y'Kir'"));

        boolean res = ExecuteTags.castSpell(U1, c.getTags());
        //Staff of ykir
        assertTrue(res);

        //directly apply staff of ykir without checks
        assertTrue(U1.getDamage()==4);


        c = P1.getHand().getCard(1);
        res = ExecuteTags.castSpell(U1, c.getTags());
        // apply sundrop to this unit
        assertTrue(res);

        //ironcliff should not go over 10hp
        System.out.println("Health: "+U1.getHealth());
        assertTrue(U1.getHealth()==10);

        c = P1.getHand().getCard(2);
        res = ExecuteTags.castSpell(U1, c.getTags());
        // apply entropic decay to this unit
        assertTrue(res);

        //ironcliff should have 0 hp
        assertTrue(U1.getHealth()==0);


    }

}
