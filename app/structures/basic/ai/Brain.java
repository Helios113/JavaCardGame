package structures.basic.ai;

import java.util.ArrayList;
import java.util.Collections;
import controllers.PlayController;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Hand;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.tags.Tag;

/**
* thinking part of the AI
* has methods for returning collections of valid actions to the main AI class (card plays/unit actions)
* also has methods for scoring each of these 
* @author Anna Smeaton
* @author Mike Parr-Burman
*/

public class Brain {

    static final int AVATAR_HEALTH_THRESHOLD = 12; // to determine whether it's worth boosting avatar health or not
    static final int HIGH_ATTACK_THRESHOLD = 4; 	// in this instance of the game the attack stat considered "high"
    public static final int ACTION_WAIT_TIME = 1000; // sleep time after each AI action

    // weighting for how to score various move types
    private static int ATTACK_WEIGHT = 3;
    private static final int PROVOKE_WEIGHT = 10;
    private static final int BEEFCAKE_MOVE_WEIGHT = 5;
    private static final int DEFENSIVE_MOVE_WEIGHT = 5;

    // attributes needed to analyse board/game conditions
    GameState gameState;
    Board board;

    public Brain(GameState gameState) {
        this.gameState = gameState;
        this.board = gameState.getBoard();
    }

    
    // takes a Hand and a mana budget and tells you what cards you can afford
    // returns in PlayableCard format
    // (i.e. also tells you the card's hand position, and its score)
    public ArrayList<PlayableCard> getPlayableCards(Hand hand, int manaBudget, int avatarHealth) {

        ArrayList<PlayableCard> playableCards = new ArrayList<PlayableCard>();

        // access the cards indexed to one (Hand uses front-end compatible indexing)
        for (int pos = 1; pos <= hand.getSize(); pos++) {

            Card thisCard = hand.getCard(pos);
            boolean isAffordable = thisCard.getManacost() <= manaBudget;

            
            // checks for affordable cards
            if (isAffordable) {
            	
            	// if thisCard is a health boost card,
                // we're assuming it's 'tactically best' to save it for your avatar
                // but we'll wait until the avatar health is below AVATAR_HEALTH_THRESHOLD
                // so we can get the full value of the health boost
            	if (!thisCard.checkTag("canBoostHealth") || 
            			(thisCard.checkTag("canBoostHealth") && avatarHealth < AVATAR_HEALTH_THRESHOLD)) 
            	{
            		PlayableCard pc = new PlayableCard(thisCard, pos);
                    scoreCard(pc);
                    pc.setTargetTile(findBestTile(pc));
                    // only consider the card playable if there is a valid target!
                    if (pc.getTargetTile() != null) playableCards.add(pc);
            	}
            		
            }
        
        }
        
        return playableCards;
       
    }

    
    // card scoring system to a max value of 10
    private void scoreCard(PlayableCard card) {
        // takes a PlayableCard, and updates the score to tell the AI how good a move it is

    	// if health boost playable then avatar health is below threshold max priority (AS)
    	if (card.getCard().checkTag("canBoostAvatar")) {
    		card.setScore(10);
    	}
    	// second best option
    	else if (card.getCard().checkTag("provoke")) {
    		card.setScore(9);
    	}
    	
    	else if (card.getCard().checkTag("spell")){
    		card.setScore(8);
    	}
    	
    	else if (card.getCard().checkTag("hasAbility")) {

    		card.setScore(7);
    	}
    	else {

    		// sets the score to the attack ability of the card to a max of 7
    		card.setScore(Math.min(card.getCard().getBigCard().getAttack(), 7));
    	}

    }

    // takes an arraylist of playableCards, tells you the hand position of the one to play
    public PlayableCard decideBestCardPlay(ArrayList<PlayableCard> playableCards) {

    	if (playableCards.size() == 0) {
    		return null;
    	}
    	// note currently if cards have the same score the best one is determined by whatever compareTo has highest
        // test print- System.out.println("CARDS CONSIDERED FOR BEST CARD PLAY:" + playableCards.size());
    	PlayableCard best = Collections.max(playableCards);
        // test print- System.out.println("ai best card name: " + best.getCard().getCardname());
    	System.out.println("ai best card is: " + best);
        return best;
    }

    // executes the best action when called for a given unit
    public void executeBestAction(Unit u) {

        // useful tile positions
    	Tile curr = board.findUnitOnBoard(u);
    	Tile humanAv = findAvatar("player");
    	Tile aiAv = findAvatar("ai");

        // this unit's attack stat
        int uAttack = u.getDamage();

    	// check if unit is ranged and if so focus on attacking enemy avatar
    	if (u.checkTag("ranged")) {

    		PlayController.handlePlay(curr.getTilex(), curr.getTiley());
    		PlayController.handlePlay(humanAv.getTilex(), humanAv.getTiley());
    	}
        // get and score all the available attack actions
        //if there is at least one with a score > 0, do the best one
        boolean didAttack = doBestAttack(u, curr, humanAv);
        if (didAttack) return;

        // if no good attack, do best move
        // ***future improvement*** atm this code always moves (doesn't consider staying on the same tile). This is something that could be worth implementing in future updates

        // init best tile to current tile (avoid nulls)
        // will also default to resetting the board if for some reason we don't assign a move destination
        Tile best = curr;
        // highlight board tiles
        int[][] debug = board.highlightOptions(u);
        
        // based on attack stat, choose the closet poss tile to either:
        // - ai avatar (be defensive)
        // - player avater (be agressive)
        // enemy_avatar tag = ai avatar unit
        
        // if I am ai avatar and health is low retreat
        if (u.checkTag("enemy_avatar") && u.getHealth() < AVATAR_HEALTH_THRESHOLD) {
        	best = closestValidTileTo(board.getTileReference(8, 2));
        } 
        // if I'm ai avatar with good health hold my ground
        else if (u.checkTag("enemy_avatar")) {
        	best = curr;
        } 
        // if I'm a weaker unit stay near my avatar like a pawn
        else if (uAttack < HIGH_ATTACK_THRESHOLD) {
            best = closestValidTileTo(aiAv);
        } 
        // if I'm a strong unit press towards enemy avatar
        else if (uAttack >= HIGH_ATTACK_THRESHOLD) {
            best = closestValidTileTo(humanAv);
        }

        board.resetTiles();

        // if best == null, the unit has no valid moves
        // this should almost never happen at his point (we have filtered out all units that are spent already)
        // catch errors for diagnosis
        if (best == null) {
            System.err.println(String.format("ERROR: AI best move == NULL\n\tfor unit at pos: (%d, %d)", curr.getTilex(), curr.getTiley()));
            System.out.println("\tint[][] debug = " + debug);
            return;
        }

        // do the best move
        if (best == curr) {
        	u.addTag(new Tag("attacked"));
        	return;
        }
        
        // test print- System.out.println(String.format("AI move from (%d,%d) tp (%d,%d)", curr.getTilex(), curr.getTiley(), best.getTilex(), best.getTiley()));
        PlayController.handlePlay(curr.getTilex(), curr.getTiley());
        PlayController.handlePlay(best.getTilex(), best.getTiley());

        //sleep for a bit to allow the animation to take place
        try {Thread.sleep(ACTION_WAIT_TIME);} catch (InterruptedException e) {e.printStackTrace();}

    }

    private boolean doBestAttack(Unit u, Tile curr, Tile humanAv) {
        // - gets and scores all available attack options,
        // - scores them
        // - does the best if it scores higher than 0

        // create list to hold all candidate attacks
        ArrayList<PlayableAction> playableActions = new ArrayList<PlayableAction>();

        // this unit's attack stat
        int uAttack = u.getDamage();

        // highlight the board
        board.highlightOptions(u);

        // iterate through all board tiles and check if they are an attack option
        for (int i = 0; i < Board.boardWidth; i++) {
			for (int j = 0; j < Board.boardHeight; j++) {

                // check the current tile
				Tile thisTile = board.getTileReference(i, j);

				// if this tile is an attack option
				if (thisTile.getMode() == 2) {
                    PlayableAction pa = new PlayableAction(curr, thisTile);
                    Unit enemy = thisTile.getUnit();
                    int enemyAttack = enemy.getDamage();

                    if (u.checkTag("enemy_avatar")) {
                    	ATTACK_WEIGHT = -1;
                    }
                    // if we can kill the enemy without taking counterattack damage, don't factor their damage stat
                    if ((enemy.getHealth() - uAttack) <= 0) enemyAttack = 0;
                    // score the attack based on the difference between how much damage it will do and how much counterattack damage you'll receive
                    pa.setScore(uAttack - enemyAttack + ATTACK_WEIGHT);
                    // ... but if the enemy unit is the avatar, score it really high regardless (unless it's our avatar attacking)
                    if (thisTile == humanAv) pa.setScore(666);
                    // add the attack to the list
                    playableActions.add(pa);
				}
			}
		} // end of board check

        board.resetTiles();

        // if there are no valid attacks, return false
        if (playableActions.size() == 0) return false;

        // find max action score
        PlayableAction best = Collections.max(playableActions);

        if (best.getScore() > 0) {
            PlayController.handlePlay(curr.getTilex(), curr.getTiley());

    		PlayController.handlePlay(best.getTargetX(), best.getTargetY());
            System.out.println(String.format("AI attacked (%d,%d) from (%d,%d)", best.getTargetX(), best.getTargetY(), curr.getTilex(), curr.getTiley()));
            //sleep for a bit to allow the animation to take place
            try {Thread.sleep(ACTION_WAIT_TIME);} catch (InterruptedException e) {e.printStackTrace();}
            return true; // we did an attack
        }

        // if we got here, we didn't do an attack
        return false;

    }

    
    /* Currently not the sleekest code but should work for now
     * completely open to refactoring but wanted to get something working for now
     * 	Maybe a BrainMaths class would help keep it tighter to store all the helper methods that are mathsy- AS
     */
    private Tile findBestTile(PlayableCard pc) {

    	Tile avatarTile = findAvatar("ai");
    	Tile enemyAvTile = findAvatar("player");			
    	Tile best;

    	// deals with simple spells that can be played on avatars
    	if (pc.getCard().checkTag("spell") && pc.getCard().checkTag("canBoostAvatar")) {

    		return avatarTile;

    	}

    	else if (pc.getCard().checkTag("spell") && pc.getCard().checkTag("canDamageAvatar")) {

    		return enemyAvTile;
    	}

    	// for non-avatar affecting cards calculate by distances
    	//changes tile modes on board to show options
    	board.highlightOptions(pc.getCard());


    	// if unit is provoke or low attack just put it close to ai avatar
        // ***future improvements***: prefer placement on a tile that blocks the most enemies / the strongest enemy

    	if (pc.getCard().checkTag("provoke") || pc.getScore() < HIGH_ATTACK_THRESHOLD) {

    		best = closestValidTileTo(avatarTile);
    		board.resetTiles();
    		return best;
    	}

    	// if unit is beefy send it to the enemy avatar
    	// if damage spell weaken units around enemy avatar
    	else if (pc.getScore() >= HIGH_ATTACK_THRESHOLD || ( pc.getCard().checkTag("damageSpell") && pc.getCard().checkTag("notAvatar"))) {

    		best = closestValidTileTo(enemyAvTile);
    		board.resetTiles();
    		return best;
    	}

    	// this should never execute but just in case (avoiding null pointers)
    	board.resetTiles();
    	return board.getTileReference(1, 1);
    }


    // finds the tile of the avatar of either player
    private Tile findAvatar(String player) {

		ArrayList<Unit> units = board.getUnitsOnBoard();
		for (Unit u: units) {

			String owner = u.getOwner();

			if ((u.checkTag("avatar")||u.checkTag("enemy_avatar")) && owner.equals(player)) {
				return board.findUnitOnBoard(u);
			}
		}

		return null;
    }

    // helper method to return the closest highlighted tile to a target tile
    private Tile closestValidTileTo(Tile target) {

    	Tile closest = null;
    	System.out.println(String.format("Target tile: %d, %d", target.getTilex(), target.getTiley()));
		// just so that it's automatically overwritten the first time
		int closestDistance = 666;

		for (int i = 0; i < Board.boardWidth; i++) {
			for (int j = 0; j < Board.boardHeight; j++) {

				Tile thisTile = board.getTileReference(i, j);

				// checks for a highlighted tile closest to the avatar but not behind
				if (thisTile.getMode() != 0) {

					int distance = Math.abs(target.getTilex() - i) + Math.abs(target.getTiley() - j);

					if (distance < closestDistance) {
						closestDistance = distance;
						closest = thisTile;
					}
				}
			}
		}

		// return the closest tile
		return closest;
    }

    // a simple method so that game flow is less sudden
    public void think() {
    	try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();};;
    }
}
