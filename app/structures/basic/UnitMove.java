package structures.basic;

import java.util.ArrayList;

import commands.BasicCommands;
import structures.GameState;
import structures.ValidMoveAttackController;
import structures.tags.Tag;
import structures.tags.TagParser;
import utils.StaticConfFiles;
import structures.tags.ExecuteTags;
import utils.BasicObjectBuilders;
import controllers.PlayController;
public class UnitMove {
	
	private String enemy; 
	private static Board board;
	public UnitMove(Board board) {
		this.board = board;
	}

    public void attackUnit(int xFrom, int yFrom, int xTo, int yTo, boolean canCounter) {
		System.out.println(String.format("Attack from: (%d,%d) to: (%d,%d)", xFrom, yFrom, xTo, yTo));
		Tile from = board.getTileReference(xFrom,yFrom); //gets position(tile) where attacking from
		Tile to = board.getTileReference(xTo,yTo);  //gets position(tile) of target
		board.highlightOptions(from.getUnit(), xFrom, yFrom); 
		if(to.getMode()!=2) {
			System.out.println("Wrong tile mode "+to.getMode());
			return;
		}
		
		
		
		if(to==from)
		{
			return;
		}
		
		if(to.getUnit().getOwner().equals("player")) {
			BasicCommands.drawTile(board.getGameState().getActorRef(), to, 2);
			try {Thread.sleep(350);} catch (InterruptedException e) {e.printStackTrace();}
		}
	
		
		Unit u = from.getUnit();
		if(u.checkTag("ranged")) //checks if is a ranged attack
		{
			dealDamage(u, from, to, canCounter);
			
		}
		else{
			int dX = Math.abs(xFrom-xTo);
			int dY = Math.abs(yFrom-yTo);
			if(dX>1||dY>1)
			{
				Tile t = ValidMoveAttackController.validMoveAttack( board,  xFrom,  yFrom,  xTo,  yTo);
				
				moveUnit(xFrom, yFrom, t.tilex, t.tiley); //moves unit to position where can attack
				from = t;
			}
			BasicCommands.drawTile(board.getGameState().getActorRef(), to, 0);
			board.resetTiles();
			dealDamage(u, from, to, canCounter);
			
		
		}
    }
	public void dealDamage(Unit u, Tile from ,Tile to, boolean canCounter)
	{
		if(u.checkTag("ranged")){
			EffectAnimation projectile = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
			BasicCommands.playProjectileAnimation(board.getGameState().getActorRef(), projectile, 0, from , to);
		}
		BasicCommands.playUnitAnimation(board.getGameState().getActorRef(), u, UnitAnimationType.attack);
		try {Thread.sleep(u.animations.attack.getLength());} catch (InterruptedException e) {e.printStackTrace();}
		// add negative health to target unit
		ExecuteTags.add(to.getUnit(), 'h', -u.getDamage());
		u.unitIdle();
		if(canCounter)
		{
			if(u.checkTag("attack2") && u.checkTag("attacked")) 
				u.addTag(new Tag("spentAttack2"));
			else
				u.addTag(new Tag("attacked"));
		
			System.out.println(
				"In counter"
			);	
			Unit u_temp = to.getUnit();
			if(u_temp == null) return;
			board.getGameState().setListenToFrontEnd(false);
			u_temp.addTag(new Tag("moved"));
			board.highlightOptions(to.getUnit());
			attackUnit(to.getTilex(),to.getTiley(),from.getTilex(),from.getTiley(),false);
			u_temp.removeTagByName("moved");
			if(board.getGameState().getTurn()%2==1)
				board.getGameState().setListenToFrontEnd(true);
			board.resetTiles();
		}
	}
	public boolean castSpell(int card, int x, int y) {
		GameState gameState = board.getGameState();
		Card c = gameState.getCurrentPlayer().getHand().getCard(card); //gets the current spell card user has
		Tile t = board.getTileReference(x, y);
		ArrayList<Tag> tags = c.getTags();
		if(t.getMode()!=0)
		{
			boolean res =  ExecuteTags.castSpell(t.getUnit(), tags);
			if(res)
			{
				gameState.getEventHandler().spellCast(gameState.getCurrentPlayer());
				gameState.getCurrentPlayer().addMana(-c.getManacost()); 
				PlayController.Message(c.getCardname() + "!");
                gameState.getCurrentPlayer().getHand().removeCard(card);
				if(gameState.getCurrentPlayer().getIndex()==1)
                	gameState.getCurrentPlayer().drawHand();
			}
			return res;
		}
		return false;
    }

    /*
     * ############################################################################################
     * Methods below here are by Stuart Miller
     * ###########################################################################################
     */
    
    
    
	/*
	 * This method will deal move a Unit from a grid position (fromx, fromy) to a position (tox, toy)
	 * This will update Unit references in Tile objects, and call front end animations for this. 
	 * 
	 * @param gameState current gameStateObject
	 * @param fromx moving from position x
	 * @param fromy moving from position y
	 * @param tox moving to position x
	 * @param toy moving to position y
	 */
    
	public void moveUnit(int fromx, int fromy, int tox, int toy) {
	
			setEnemy(board); 
			GameState gameState = board.getGameState();
			if(fromx==tox && fromy==toy) {
				return; 
			}
			 
			//for checking diagonal moves and which way to go. Clockwise, index 0 is the 2 o'clock. 
			 
			Tile from = board.getTileReference(fromx,fromy); 
			Tile to = board.getTileReference(tox,toy);
			
		
			//if valid move 
			if(to.getMode()==1) {
				//board.resetTiles();  
				Unit toMove = from.getUnit();
				
			
				if (blockedOnHorizontal(fromx, fromy, tox, toy, board)) {
					BasicCommands.moveUnitToTile(gameState.getActorRef(), toMove, to, true);
					
				}
				else {
						BasicCommands.moveUnitToTile(gameState.getActorRef(), toMove, to);
					} 
		
				
				//move the Unit reference from attribute of Tiles 
				from.setUnit(null);
				to.setUnit(toMove);
				toMove.setPositionByTile(to); 
				toMove.addTag(new Tag("moved")); 

				int dX = Math.abs(fromx-tox);
				int dY = Math.abs(fromy-toy);
				try {Thread.sleep((dX+dY)*900);} catch (InterruptedException e) {e.printStackTrace();}
				toMove.unitIdle();
				try {Thread.sleep(150);} catch (InterruptedException e) {e.printStackTrace();}
				
			}
		}
	

		  
		
/*
 * Method to determine if the path of movement is blocked on the horizontal by an enemy unit
 * 
 * @param fromx from position x
 * @param fromy from position y 
 * @param tox  to position x
 * @param toy to position y
 * @return blocked. true if blocked on horizontal, false if not blocked		 
 */
	

	public boolean blockedOnHorizontal(int fromx, int fromy, int tox, int toy, Board board) {
		setEnemy(board); 
		
		if(Math.abs(toy-fromy) !=1 || Math.abs(tox-fromx)!= 1) return false;  
		final int[][] moves = {{-1,1},{1,1},{1,-1},{-1,-1}};
		int xdifference = tox-fromx; 
		int ydifference = toy-fromy; 
		int target = 0; 
		boolean blocked = false; 
		
		if(Math.abs(tox-fromx) + Math.abs(toy-fromy) == 2) return false; 
		
		for(int i =0; i < moves.length; i++) {
			
				if(moves[i][0] == xdifference && moves[i][1] == ydifference) {
					target = i; 
			
		
				}
		
		if(target == 2 || target == 1) {
				try {
					if( board.getTileReference(fromx+1, fromy).getUnit().getOwner().equals(enemy)){
						blocked = true; 
					}
				}catch (NullPointerException e) {} // Tile does not have unit, do nothing
				
		}else if(target == 0 || target == 3) {
			
				try {
					if(board.getTileReference(fromx-1, fromy).getUnit().getOwner().equals(enemy)){
						blocked = true; 
					}
				}catch (NullPointerException e) {} // Tile does not have unit, do nothing
		}
		
		
		}
		return blocked; 
	}
	
	
	/*
	 * This method can be called to signal that either a unit is to move from (fromx, fromy) to (tox,toy) or
	 * to attack unit on (tox,toy), or to move from 'from' to a position where it can attack unit on 'to'
	 * Used by the AI.
	 * @param gameState current gamestate 
	 * @param fromx initial x position 
	 * @param fromy initial y position 
	 * @param tox target x position
	 * @param toy target y position 
	 */
	// public void doYaThang(int fromx, int fromy, int tox, int toy) {
		
	// 	board.highlightOptions(new Taggable(), fromx, fromy); 
	// 	if(board.getTileReference(tox,toy).getMode() == 1) {
	// 		moveUnit(fromx, fromy, tox, toy); 
	// 	}else if(board.getTileReference(tox, toy).getMode() == 2) {
	// 		attackUnit(fromx, fromy, tox, toy,true ); 
	// 	}
	// }
	
	
	public void setEnemy(Board board) {
		if(board.getGameState().getTurn()%2==1) enemy = "ai";
		else enemy = "player";
}
	public boolean spawnUnit(int cardNo, int x, int y) {
		
		if(board.getTileReference(x,y).getMode() == 1) {
			board.getGameState().spawnUnit(cardNo, x, y);
			return true;
		}
		return false;
	}
	
}