package controllers;

import structures.basic.Board;
import structures.basic.Card;

import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import commands.BasicCommands;
/**
 * The Play Controller class deals with all player inputs
 * whether they be human or AI.
 * Play Controller also stores a highlight attribute which shows
 * which game state are we currently in.
 * int highlight - used for determining what state the board is in
 * 0 - clear board
 * 1 - attack/move highlight
 * 2 - spawn unit highlight
 * 3 - cast spell highlight
 * @author Preslav Aleksandrov
 */
public class PlayController {
    public static int highlight = 0;
    private static Unit prevUnit = null;
    private static int prevCard = -1;
    public static boolean human = true;
    public static GameState gameState;
    public static void handlePlay(int handPosition) {
        if(handPosition == prevCard) {
            return;
        }
        Card c = gameState.getCurrentPlayer().getHand().getCard(handPosition);
        if (c.getManacost() > gameState.getCurrentPlayer().getMana()) {
            resetTiles();
            highlight = 0;
            //tells player they lack mana
            Message("Insufficient mana");
            return;
        }
        resetTiles();
        highlightOptions(c, handPosition);
        prevCard = handPosition;
        if (c.checkTag("spell"))
            highlight = 3;
        else
            highlight = 2;

    }

    public static void handlePlay(int x, int y) {
        Tile tile = gameState.getBoard().getTileReference(x, y);
        Unit u = tile.hasUnit() ? tile.getUnit() : null;
        switch (highlight) {
            case 0:
                if (u != null) {
                    if (!u.getOwner().equals(gameState.getCurrentPlayer().getName())) {
                        resetTiles();
                        //enemy unit clicked
                        prevUnit = null;
                    } else {
                        //friednly unit click 
                        highlightOptions(u, x, y);
                        highlight = 1;
                        prevUnit = u;
                    }
                } else {
                    //click empty
                    resetTiles();
                }
                break;
            case 1:
                if (u != null) {
                	gameState.getBoard().resetTiles();
                	gameState.setListenToFrontEnd(false);
                    gameState.getUnitMove().attackUnit(prevUnit.getPosition().getTilex(),
                            prevUnit.getPosition().getTiley(), x, y,true);
                    if(gameState.getTurn()%2==1)gameState.setListenToFrontEnd(true);
                } else {
                    gameState.getUnitMove().moveUnit(prevUnit.getPosition().getTilex(),
                            prevUnit.getPosition().getTiley(), x, y);
                }
                resetTiles();
                break;
            case 2:
                if (u == null) {
                    //creates unit
                    if(gameState.getUnitMove().spawnUnit(prevCard, x, y))
                        prevCard = -1;
                }
                resetTiles();
                break;
            case 3:
                if (u != null) {
                    //casts spell
                    if(gameState.getUnitMove().castSpell(prevCard, x, y))
                        prevCard = -1;
                }
                resetTiles();
                break;

        }

    }

    public static void resetTiles() {
        if(gameState.getCurrentPlayer().getIndex()==1){
            if(prevCard!=-1)
            {
                Card c1 = gameState.getCurrentPlayer().getHand().getCard(prevCard);
                BasicCommands.drawCard(gameState.getActorRef(),c1, prevCard, 0);
            }
        }
        gameState.getBoard().resetTiles();
        highlight = 0;
        prevCard = -1;
        prevUnit = null;
    }

    private static void highlightOptions(Card c, int handPos) {
        if(gameState.getCurrentPlayer().getIndex()==1)
            BasicCommands.drawCard(gameState.getActorRef(),c, handPos, 1);
        gameState.getBoard().highlightOptions(c);
    }

    private static void highlightOptions(Unit u, int x, int y) {
        gameState.getBoard().highlightOptions(u, x, y);
    }

    public static void unitDeath(Unit u)
    {
        gameState.getEventHandler().unitDied(u);
    }

    public static void Message(String m){
        if(gameState.getCurrentPlayer().getIndex()==1)
                BasicCommands.addPlayer1Notification(gameState.getActorRef(), m, 2);
    }
}
