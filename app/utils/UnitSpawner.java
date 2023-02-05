package utils;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import akka.actor.ActorRef;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Tile;
import commands.BasicCommands;
import structures.basic.Unit;
import structures.tags.ExecuteTags;
import structures.tags.Tag;
import controllers.EventHandler;

/**
 * Unit spawner class creates units and populates their stats 
 * @author Preslav Aleksandrov
 */
public class UnitSpawner{

    static int cnt = 1;
    static final int SPAWN_WAIT_TIME = 200;

    EventHandler ev;
    Board board;

    public UnitSpawner(Board board, EventHandler ev)
    {
        this.ev = ev;
        this.board = board;
    }
    /**
     * Creates an avatar
     * @param out - actor ref required by object builder
     * @param A - unit name
     * @return returns populated Unit object
     */
    public Unit spawnAvatar(ActorRef out, String A){
        Unit unit;
        Tile t;
		if(A.equals("player")){
			unit = (Unit) BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
            unit.setOwner("player");
            unit.setName("avatar");
            t = board.getTileReference(1,2);

		}
		else{
			unit = (Unit) BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);
            unit.setOwner("ai");
            unit.setName("enemy_avatar");
            t = board.getTileReference(7,2);

		}
        unit.setActorRef(out);
        unit.setPositionByTile(t);
        BasicCommands.drawUnit(out, unit, t);
        try {Thread.sleep(SPAWN_WAIT_TIME);} catch (InterruptedException e) {e.printStackTrace();}
        unit.setMaxHealth(20);
        unit.setHealth(20,false);
        try {Thread.sleep(SPAWN_WAIT_TIME);} catch (InterruptedException e) {e.printStackTrace();}
        unit.setMaxDamage(2);
        unit.setDamage(2,false);
        try {Thread.sleep(SPAWN_WAIT_TIME);} catch (InterruptedException e) {e.printStackTrace();}
        t.setUnit(unit);
        // unit.addTag(new Tag("new"));

        board.addUnit(unit);
        ev.avatarCreated(unit);
        return unit;
	}
    /**
     * Creates a Unit object
     * @param out - actor ref required by object builder
     * @param A - unit name
     * @param t - the tile on which to create the unit
     * @return returns populated Unit object
     */
    public Unit spawnUnit(ActorRef out, Card A, Tile t){
        Unit unit;
        //check who the player who made the unit is
        // 1 - player
        // 0 - ai
        if(board.getGameState().getCurrentPlayer().getIndex() == 1){
            unit = (Unit) BasicObjectBuilders.loadUnit(StaticConfFiles.humanUnits.get(A.getCardname()),++cnt, Unit.class);
            unit.setOwner("player");
        }
        else{
            unit = (Unit) BasicObjectBuilders.loadUnit(StaticConfFiles.aiUnits.get(A.getCardname()),++cnt, Unit.class);
            unit.setOwner("ai");
        }
        unit.setName(A.getCardname());
        unit.setActorRef(out);
        unit.setPositionByTile(t);
        BasicCommands.drawUnit(out, unit, t);

        try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

        unit.setMaxHealth(A.getBigCard().getHealth());
        unit.setHealth(A.getBigCard().getHealth(),false);
        try {Thread.sleep(SPAWN_WAIT_TIME);} catch (InterruptedException e) {e.printStackTrace();}
        unit.setMaxDamage(A.getBigCard().getAttack());
        unit.setDamage(A.getBigCard().getAttack(),false);
        try {Thread.sleep(SPAWN_WAIT_TIME);} catch (InterruptedException e) {e.printStackTrace();}
        t.setUnit(unit);
        unit.addTag(new Tag("new"));

        board.addUnit(unit);
        ev.unitCreated(unit);
        return unit;
	}
}
