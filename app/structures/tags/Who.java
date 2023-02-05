package structures.tags;

import java.util.ArrayList;
import java.util.Arrays;

import structures.GameState;
import structures.basic.Player;
import structures.basic.Unit;
import controllers.EventHandler;

/**
 * The who class converts local tag names to global ones
 * for example if a tag contains avatar (the unit's owner's avatar)
 * if the owner is the ai, Who will give back "enemy_avatar"
 * if the same unit is owned by the human player, Who will give back
 * "avatar"
 */
public class Who {
    private static GameState gameState;
    private static EventHandler eventHandler;
    private static Player AI;
    // equivalency matrix
    public static final String[][] equivalency = {
            { "avatar", "player" },
            { "enemy_avatar", "ai" }
    };
    // basic functionality
    // who("owner", "relative_target") = "absolute_target"
    // ex.
    // who("player", "enemy") = "ai"
    // who("ai", "enemy_avatar") = "avatar"
    //

    public static void setEvenHandler(EventHandler eh) {
        eventHandler = eh;
    }
    /**
     * who method general method which converts from local to global naming
     * @param actor - unit which does the action
     * @param test - the unit to test against
     * @param mode - type of test
     * @return whether the test unit should execute it's tags
     */
    public static boolean who(Unit actor, Unit test, String mode) {
        // System.out.println("Mode: " + mode);
        // System.out.println("Trigger unit " + actor);
        // System.out.println("Affected unit " + test);
        // System.out.println("Affected unit's target (local) " + ((OnEvent) test.getTagByName(mode)).who);
        // System.out.println("Affected unit's target (global) " + who(actor, ((OnEvent) test.getTagByName(mode)).who));
        return who(test, ((OnEvent) test.getTagByName(mode)).who).contains(actor);
    }
    /**
     * who method player method which converts from local to global naming
     * @param casted - player who casted
     * @param test - unit to test against
     * @return whether the test unit should execute it's tags
     */
    public static boolean who(Player casted, Unit test) {
        System.out.print("Mode: " + "On Cast");
        System.out.println(" done by " + casted.getName());
        System.out.println(" done by " + casted.getIndex());
        System.out.println(test.getTagByName("onCast"));
        // == who(casted.getIndex(),((OnEvent)test.getTagByName("OnCast")).who)
        System.out.println(who(test.getOwner(), ((OnEvent) test.getTagByName("onCast")).who));
        return casted.getName().equals(who(test.getOwner(), ((OnEvent) test.getTagByName("onCast")).who));
    }

    public static ArrayList<Unit> who(Unit actionOwner, String target) {
        int own = actionOwner.getOwner().equals("ai") ? 0 : 1;
        switch (target) {
            case "me":
                return new ArrayList<>(Arrays.asList(actionOwner));
            case "avatar":// returns only avatar
                return eventHandler.getUnits(equivalency[1 - own][0]);
            case "enemy_avatar":// returns only avatar
                return eventHandler.getUnits(equivalency[own][0]);
            case "enemy":// returns avatar included
                return eventHandler.getUnits(equivalency[own][1]);
            case "friendly":// returns avatar included
                return eventHandler.getUnits(equivalency[1 - own][1]);
        }
        return null;
    }

    public static String who(String owner, String target) {
        int own = owner.equals("ai") ? 0 : 1;
        switch (target) {
            case "enemy":// returns avatar included
                return equivalency[own][1];
            case "friendly":// returns avatar included
                return equivalency[1 - own][1];
        }
        return "";

    }

    public static Player whoPlayer(Unit actionOwner, String target) {
        int own = actionOwner.getOwner().equals("ai") ? 0 : 1;
        switch (target) {
            case "player":
                System.out.println(equivalency[own][1]);
                return eventHandler.getPlayer(equivalency[1 - own][1]);
            case "enemy_player":
                System.out.println(equivalency[own][0]);
                return eventHandler.getPlayer(equivalency[own][1]);
        }
        return null;
    }

}
