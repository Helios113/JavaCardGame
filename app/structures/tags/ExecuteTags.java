package structures.tags;

import structures.basic.Unit;
import structures.basic.Player;
import structures.tags.Who;
import controllers.EventHandler;
import java.util.ArrayList;
/**
 * Execute Tags is the class which handles tags and their
 * applications on game entities.
 * It provides methods to handle the different tags supported
 * by the game
 * @author Preslav Aleksandrov
 */
public class ExecuteTags {

    public static int turn;
    public static EventHandler eventHandler;
    public static void setEvenHandler(EventHandler ev) {
        eventHandler = ev;
    }
    /////////////////////
    // Modifiers
    /////////////////////
    /**
    * This add method takes care of add modifiers applied to a Unit.
    * It takes a target, attribute and value
    * For the meanings of the different attributes please refer to 
    * README.md
    */
    public static void add(Unit target, char attr, int val) {
        switch (attr) {
            case 'h': // health
                if (val < 0) {
                    eventHandler.damageTaken(target);
                }
                target.addHealth(val, true);
                // if updating the health of either avatar
                // - also update their respective Player object
                if (target.checkTag("avatar")) {
                    eventHandler.getPlayer("player").setHealth(target.getHealth());
                } else if (target.checkTag("enemy_avatar")) {
                    eventHandler.getPlayer("ai").setHealth(target.getHealth());
                }
                break;
            case 'j': // health capped at a max
                if (val < 0) {
                    eventHandler.damageTaken(target);
                }
                target.addHealth(val, false);
                break;
            case 'a': // damage
                target.addDamage(val, true);
                break;
        }
    }

    /*
     * Method for setting attributes.
     * Sister method to add
     */
    public static void set(Unit target, char attr, int val) {
        switch (attr) {
            case 'h': // health
                target.setHealth(val, true);
                // if updating the health of either avatar
                // - also update their respective Player object
                if (target.checkTag("avatar")) {
                    eventHandler.getPlayer("player").setHealth(target.getHealth());
                } else if (target.checkTag("enemy_avatar")) {
                    eventHandler.getPlayer("ai").setHealth(target.getHealth());
                }
                break;
            case 'j': // health max
                target.setHealth(val, false);
                break;
            case 'm': // mana
                // target.addMana(val);// check that target is player or AI
                break;
            case 'a': // damage
                target.setDamage(val, true);
                break;
        }
    }
    /**
    * This add method takes care of add modifiers applied to a Player.
    * It takes a target, attribute and value
    * For the meanings of the different attributes please refer to 
    * README.md
    */
    public static void add(Player target, char attr, int val) {
        switch (attr) {
            case 'm': // health
                target.addMana(val);
                break;
            case 'c': // damage
                target.drawCards(val);
                break;
        }
    }
    /*
     * Method for setting attributes.
     * Sister method to add
     */
    public static void set(Player target, char attr, int val) {
        switch (attr) {
            case 'm': // health
                target.setHealth(val);
                break;
        }
    }

    /////////////////////
    // onEvents
    /////////////////////

    // onCast{<who>,<list of actions>}
    /**
    * executeList is a method which takes a list of modifier tags, 
    * interprets them and executes them
    * actionOwner - the entity who's tags are being executed
    */
    public static void executeList(Unit actionOwner, ArrayList<Tag> actions) {
        for (Tag A : actions) {
            Modifier mod = ((Modifier) A);
            //Comment System.out.println(mod);
            // checks if target will be a player
            if (mod.who.contains("player")) {
                Player target = Who.whoPlayer(actionOwner, mod.who);
                if (mod.name.equals("add"))
                    add(target, mod.attr, mod.val);
                else
                    set(target, mod.attr, mod.val);
            } else { // if target is a unit
                ArrayList<Unit> target = Who.who(actionOwner, mod.who);
                for (Unit tar : target) {
                    if (mod.name.equals("add"))
                        add(tar, mod.attr, mod.val);
                    else
                        set(tar, mod.attr, mod.val);
                }
            }

        }

    }

    /**
    * onCast handles onCast tags
    * actor is the player who cast the spell
    * affectedUnits - list of units with onCast tags
    */
    public static void onCast(Player actor, ArrayList<Unit> affectedUnits) {
        for (Unit au : affectedUnits) {
            if (Who.who(actor, au)) {
                System.out.println("Execute onCast of " + au.getOwner());
                executeList(au, ((OnEvent) au.getTagByName("onCast")).modifiers);
            }
        }
    }
    /**
    * onSummoned handles onSummoned tags
    * actor is the unit who was summoned
    * affectedUnits - list of units with onSummoned tags
    */
    public static void onSummoned(Unit u, ArrayList<Unit> affectedUnits) {
        for (Unit au : affectedUnits) {
            if (Who.who(u, au, "onSummoned")) {
                System.out.println("Execute onSummoned of " + au);
                executeList(au, ((OnEvent) au.getTagByName("onSummoned")).modifiers);
            }
        }
    }
    /**
    * onDamageTaken handles onDamageTaken tags
    * actor is the unit that was damaged
    * affectedUnits - list of units with onDamageTaken tags
    */
    public static void onDamageTaken(Unit u, ArrayList<Unit> affectedUnits) {
        for (Unit au : affectedUnits) {
            if (Who.who(u, au, "onDamageTaken")) {
                System.out.println("Execute onDamageTaken of " + au);
                executeList(au, ((OnEvent) au.getTagByName("onDamageTaken")).modifiers);
            }
        }
    }
    /**
    * onDeath handles onDeath tags
    * actor is the unit that died
    * affectedUnits - list of units with onDeath tags
    */
    public static void onDeath(Unit u, ArrayList<Unit> affectedUnits) {
        for (Unit au : affectedUnits) {
            if (Who.who(u, au, "onDeath")) {
                System.out.println("Execute the tags of " + au.getOwner());
                executeList(au, ((OnEvent) au.getTagByName("onDeath")).modifiers);
            }
        }
    }

    /**
     * castSpell casts a spell card
     * target is the target of the spell
     * ts - tag list of the spell
     */
    public static boolean castSpell(Unit target, ArrayList<Tag> ts)
    {
        for (Tag t : ts)
        {
            switch (t.getName())
            {
                case "add":
                    if(((Modifier)t).who.equals("target_n") && target.checkTag("avatar"))
                    {
                        return false;
                    }
                    add(target, ((Modifier)t).attr, ((Modifier)t).val);
                    break;
                case "set":
                    if(((Modifier)t).who.equals("target_n") && target.checkTag("avatar"))
                    {
                        return false;
                    }
                    set(target, ((Modifier)t).attr, ((Modifier)t).val);
                    break;
            }
        }
        return true;
    }

}
