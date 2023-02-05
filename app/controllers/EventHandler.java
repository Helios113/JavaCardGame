package controllers;

import java.util.ArrayList;
import structures.basic.Board;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.tags.ExecuteTags;
import structures.basic.Player;
import commands.BasicCommands;
/**
 * The Event Handler class takes care of all
 * Event tags. It uses the ExecuteTag class to then run the correct tag
 * @author Preslav Aleksandrov 
 */
public class EventHandler {
        ArrayList<Unit> onCast;
        ArrayList<Unit> onSummoned;
        ArrayList<Unit> onDamageTaken;
        ArrayList<Unit> onDeath;
        ArrayList<Unit> player;
        ArrayList<Unit> ai;
        ArrayList<Unit> avatar;
        ArrayList<Unit> enemy_avatar;
        Player h_player;
        Player ai_player;
        Board board;

        public EventHandler(Board board) {
                onCast = new ArrayList<Unit>();
                onSummoned = new ArrayList<Unit>();
                onDamageTaken = new ArrayList<Unit>();
                onDeath = new ArrayList<Unit>();
                player = new ArrayList<Unit>();
                ai = new ArrayList<Unit>();
                avatar = new ArrayList<Unit>();
                enemy_avatar = new ArrayList<Unit>();
                this.board = board;
        }
        public void setPlayers(Player human, Player ai) {
                this.h_player = human;
                this.ai_player = ai;
        }
        public Player getPlayer(String A) {
                if (A.equals("player"))
                        return h_player;
                return ai_player;
        }

        public void avatarCreated(Unit u) {
                if (u.getOwner().equals("ai")) {
                        enemy_avatar.add(u);
                        return;
                }
                avatar.add(u);
                unitCreated(u);

        }

        public void unitCreated(Unit u) {
                System.out.println("New unit created with tags:");
                System.out.println(u.getTags());
                if (u.checkTag("onCast")) {
                        System.out.println("Unit was added to onCast");
                        onCast.add(u);
                }
                if (u.checkTag("onSummoned")) {
                        System.out.println("Unit was added to onSummoned");
                        onSummoned.add(u);
                }
                if (u.checkTag("onDamageTaken")) {
                        System.out.println("Unit was added to onDamageTaken");
                        onDamageTaken.add(u);
                }
                if (u.checkTag("onDeath")) {
                        System.out.println("Unit was added to onDeath");
                        onDeath.add(u);
                }
                if (u.getOwner().equals("player")) {
                        System.out.println("Unit was added to player units");
                        player.add(u);
                } else {
                        System.out.println("Unit was added to ai units");
                        ai.add(u);
                }
                unitSummoned(u);
        }

        public ArrayList<Unit> getUnits(String set) {
                if (set.equals("ai")) {
                        return ai;
                } else if (set.equals("player")) {
                        return player;
                } else if (set.equals("avatar")) {
                        return avatar;
                }
                return enemy_avatar;
                // This needs to be able to return avatars as well based on their absolute names

        }

        public void spellCast(Player p) {
                System.out.println("____________");
                System.out.println("A spell was cast");
                System.out.println("The effected units are: "+onCast.toString());
                ExecuteTags.onCast(p, onCast);
                System.out.println("____________");
                // tagExecution("onCast", p, onCast);
        }

        public void unitSummoned(Unit u) {
                System.out.println("____________");
                System.out.println("A unit was summoned");
                System.out.println("The effected units are: "+onSummoned.toString());
                System.out.println("____________");
                ExecuteTags.onSummoned(u, onSummoned);
        }

        public void damageTaken(Unit u) {
                System.out.println("____________");
                System.out.println("A unit has taken damage");
                System.out.println("The effected units are: "+onDamageTaken.toString());
                System.out.println("____________");
                ExecuteTags.onDamageTaken(u, onDamageTaken);
                // tagExecution("onDamageTaken", u, onDamageTaken);
        }

        public void unitDied(Unit u) {
                System.out.println("____________");
                System.out.println("A unit has died");
                System.out.println("The effected units are: "+onDamageTaken.toString());
                System.out.println("____________");
                ExecuteTags.onDeath(u, onDeath);
                onCast.remove(u);
                onSummoned.remove(u);
                onDamageTaken.remove(u);
                onDeath.remove(u);
                board.getTileReference(u.getPosition().getTilex(), u.getPosition().getTiley()).setUnit(null);
                board.removeUnit(u);
                if(u.checkTag("avatar")||u.checkTag("enemy_avatar")) {
                	board.getGameState().endGame(); 	
                }
        }

}
