package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.PlayController;
import structures.basic.UnitMove;


/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile.
 * 
 * This class holds the unit's internal state and
 * communicates this local state to the front end.
 *
 * @author Dr. Richard McCreadie
 * @author Preslav Aleksandrov
 */
public class Unit extends Taggable {

    @JsonIgnore
    protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java
                                                               // objects from a file

    int id;
    UnitAnimationType animation;
    Position position;
    UnitAnimationSet animations;
    ImageCorrection correction;
    ActorRef out;
    private String owner = "";
    private String name = "";
    int curHealth = 0;
    int curDamage = 0;
    int maxHealth = 0;
    int maxDamage = 0;
    
    // for avatars to link to player
    protected Player player;
    

    // add health and attack
    // TODO
    public Unit() {
    }

    public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
        super();
        this.id = id;
        this.animation = UnitAnimationType.idle;

        position = new Position(0, 0, 0, 0);
        this.correction = correction;
        this.animations = animations;
    }

    public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
        super();
        this.id = id;
        this.animation = UnitAnimationType.idle;

        position = new Position(currentTile.getXpos(), currentTile.getYpos(), currentTile.getTilex(),
                currentTile.getTiley());
        this.correction = correction;
        this.animations = animations;
    }

    public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
            ImageCorrection correction) {
        super();
        this.id = id;
        this.animation = animation;
        this.position = position;
        this.animations = animations;
        this.correction = correction;
    }

    // for units like avatars that need to link to the player health
    public void setPlayer(Player p) {
    	this.player = p;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UnitAnimationType getAnimation() {
        return animation;
    }

    public void setAnimation(UnitAnimationType animation) {
        this.animation = animation;
    }

    public ImageCorrection getCorrection() {
        return correction;
    }

    public void setCorrection(ImageCorrection correction) {
        this.correction = correction;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public UnitAnimationSet getAnimations() {
        return animations;
    }

    public void setAnimations(UnitAnimationSet animations) {
        this.animations = animations;
    }

    /**
     * This command sets the position of the Unit to a specified
     * tile.
     *
     * @param tile
     */
    @JsonIgnore
    public void setPositionByTile(Tile tile) {
        position = new Position(tile.getXpos(), tile.getYpos(), tile.getTilex(), tile.getTiley());
    }

    @JsonIgnore
    public String getOwner() {
        return owner;
    }

    @JsonIgnore
    public void setOwner(String owner) {
        this.owner = owner;
    }
    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    ///////// Health and Damage //////////
    // int curHealth = 0;
    // int curDamage = 0;
    // int maxHealth = 0;
    // int maxDamage = 0;

    public void setActorRef(ActorRef out)
    {
        this.out = out;
    }
    
    @JsonIgnore
    public void setMaxHealth(int h) {
        this.maxHealth = h;
    }

    @JsonIgnore
    public int getHealth() {
        return this.curHealth;
    }

    public void addHealth(int h, boolean overMax) {
        this.curHealth += h;
        if (!overMax && curHealth > maxHealth) {
            System.out.println("Added too much hp");
            curHealth = maxHealth;
        }
        
        // if this is an avatar update player health
        if (this.checkTag("avatar")) {
        	this.player.setHealth(this.curHealth);
        }else if (this.checkTag("enemy_avatar")) {
        	this.player.setHealth(curHealth);
        }
        
        if(h < 0){
            BasicCommands.playUnitAnimation(out, this, UnitAnimationType.hit);
		    try {Thread.sleep(this.animations.hit.getLength());} catch (InterruptedException e) {e.printStackTrace();}
        }
        updateHealth(this.curHealth);
    }

    public void setHealth(int h, boolean overMax) {
        this.curHealth = h;
        if (!overMax && curHealth > maxHealth) {
            curHealth = maxHealth;
        }
        updateHealth(this.curHealth);
    }

    @JsonIgnore
    public void setMaxDamage(int d) {
        this.maxDamage = d;
    }

    @JsonIgnore
    public int getDamage() {
        return this.curDamage;
    }

    public void addDamage(int d, boolean overMax) {
        this.curDamage += d;
        if (!overMax && curDamage > maxDamage) {
            curDamage = maxDamage;
        }
        updateDamage(this.curDamage);
    }

    public void setDamage(int d, boolean overMax) {
        this.curDamage = d;
        if (!overMax && curDamage > maxDamage) {
            curDamage = maxDamage;
        }
        updateDamage(this.curDamage);
    }

    public void updateDamage(int d) {
        BasicCommands.setUnitAttack(out, this,d);
        try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
    }
    
    public void updateHealth(int h) {
        BasicCommands.setUnitHealth(out, this,h<1?0:h);
        try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
        if(h<1)
        {
            BasicCommands.playUnitAnimation(out, this, UnitAnimationType.death);
            try {Thread.sleep(this.animations.death.getLength());} catch (InterruptedException e) {e.printStackTrace();}
            PlayController.unitDeath(this);
            BasicCommands.deleteUnit(out, this);
            try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
            return;
        }
        unitIdle();
        
    }
    public void unitIdle()
    {
        try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.playUnitAnimation(out, this, UnitAnimationType.idle);
    }
    public void unitChanel()
    {
        BasicCommands.playUnitAnimation(out, this, UnitAnimationType.channel);
		try {Thread.sleep(this.animations.channel.getLength());} catch (InterruptedException e) {e.printStackTrace();}
    }
    public String toString()
    {
        return this.getName()+" owned by "+this.getOwner();
    }

}
