package structures.basic;

import static org.junit.Assert.*;

import org.junit.Test;

import akka.actor.ActorRef;
import commands.BasicCommands;
import utils.BasicObjectBuilders;

public class BoardMoveTest {
	
	/*
	 * A test of all the basic move logic. Tests for flying, ranged, provoke abilites, with standard move and attack options, blocked paths
	 * Only a test of correct logic on a prototype board representation. Test then to be expanded into the proper version
	 * which reads the tags and tile, unit objects rather than the int[][]
	 * 
	 */

	@Test
	public void provokeTest1() {
		
		Board2 board = new Board2(null); 
		board.highlightMoveAttackTiles(1,2) ;
		assertTrue(board.isItTheSame(board.expectedBoard1)); 	
	}
	
	@Test
	public void provokeTest2() {
		Board2 board = new Board2(null); 
		board.highlightMoveAttackTiles(1,0) ;
		assertTrue(board.isItTheSame(board.expectedBoard1)); 
	}
	@Test
	public void rangedTest() {
		Board2 board = new Board2(null); 
		board.highlightMoveAttackTiles(5,0) ;
		assertTrue(board.isItTheSame(board.expectedBoardRanged)); 
	}
	@Test
	public void moveBlocking() {
		Board2 board = new Board2(null); 
		board.highlightMoveAttackTiles(4,2) ;
		assertTrue(board.isItTheSame(board.expectedBoardBlocking)); 
	}
	@Test
	public void moveAndAttack() {
		Board2 board = new Board2(null); 
		board.highlightMoveAttackTiles(2,4) ;
		assertTrue(board.isItTheSame(board.expectedBoardMAA)); 
	}
	@Test
	public void flyingTest() {
		Board2 board = new Board2(null); 
		board.highlightMoveAttackTiles(7,1) ;
		assertTrue(board.isItTheSame(board.expectedBoardFly));
	}
	
	
	/*
	 * Primative version of the board for testing logic of the moves, with correct tiles being highlighted 
	 * 
	 */
	
	private class Board2 {
		
		private Tile[][] tileReferences; 
		private ActorRef out =null; 
		private int[][] moves={{0,-2},{-1,-1},{0,-1},{1,-1},{-2,0},{-1,0},{1,0},{2,0},{-1,1},{0,1},{1,1},{0,2}}; 
		
		public int[][] colours = new int[9][5]; 
		public int[][] expectedBoard1 = {{0,2,0,0,0},{0,0,0,0,0},{0,2,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};
		public int[][] expectedBoardRanged = {{0,2,0,0,0},{0,0,0,2,0},{0,2,0,0,0},{1,0,2,0,0},{1,0,0,2,0},{0,1,0,0,0},{1,1,0,0,0},{1,0,0,0,0},{0,0,0,0,0}};
		public int[][] expectedBoardBlocking = {{0,0,0,0,0},{0,0,0,0,0},{0,2,0,0,0},{0,1,2,0,0},{1,0,0,2,0},{0,1,0,1,0},{0,0,1,0,0},{0,0,0,0,0},{0,0,0,0,0}};
		public int[][] expectedBoardMAA = {{0,0,0,0,1},{0,0,0,2,1},{0,2,1,1,0},{0,0,2,1,1},{0,0,0,2,1},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0},{0,0,0,0,0}};
		public int[][] expectedBoardFly = {{1,2,0,1,1},{0,1,0,2,1},{1,2,1,1,0},{1,1,2,1,1},{1,0,0,2,1},{0,1,0,1,1},{1,1,1,1,1},{1,0,1,1,1},{1,1,1,1,1}};
		
		//for testing 
		private int[][] testBoard = new int[9][5]; 
		
		///////constructor/////////////////
		
		public Board2(ActorRef out) {
			tileReferences = new Tile[9][5]; 
			this.out = out; 
			
			//draw all initial board and store references 
			for(int i = 0; i < tileReferences.length; i++) {
				for(int j = 0; j< tileReferences[i].length; j++) {
					tileReferences[i][j] = BasicObjectBuilders.loadTile(i,j); 
					colours[i][j] = 0;
				}
			}	 
			/////////for testing////////
			for(int i = 0; i<9; i++) {
				for(int j=0; j<5; j++) {
					testBoard[i][j] = -1; 
				}
			}
			testBoard[1][0] = 2; 
			testBoard[5][0] = 3; 
			testBoard[0][1] = 4; 
			testBoard[7][1] = 2;
			testBoard[0][2] = 3;
			testBoard[1][2] = 1;
			testBoard[3][2] = 5;
			testBoard[4][2] = 1; 
			testBoard[4][3] = 5; 
			testBoard[4][1] = 1; 
			testBoard[2][4] = 1; 
			testBoard[5][2] = 1;
			testBoard[2][1] = 4; 
			testBoard[1][3] = 5; 
			
			
			
		} //end of constructor
			
		
		////////accessors//////////
	
		
		public void higlightAttackTiles(int x, int y) {
			
			if(nextToProvoke(x,y)) {
				boolean[] valid = nextToOnBoard(x,y); 
				for(int i = 0; i< valid.length; i++) {
					if(valid[i] && testBoard[x+moves[i][0]][y+moves[i][1]]== 4){
				//if(true /*Tile(moves[i][0]+x, moves[i][1] + y).hasProvoke*/){
						
						colours[moves[i][0]+x][moves[i][1]+y] = 2; 
					}
				}
			}
		//	}else if(false /* isOfTypeRanged, may need to alter to move and attack*/) {
			else if(testBoard[x][y] == 3|| testBoard[x][y] ==2) {
				for(int i = 0; i<9 ; i++) {
					for(int j = 0; j < 5; j++) {
		//				if(true /*Tile has enemy unit*/) {
						if(testBoard[i][j]==4||testBoard[i][j]==5) {
							colours[i][j] = 2; 
						}
					}
				}	
			}else {
				highlightStandardAttack(x,y); 
			}
			
			
		}
		 /*
		  *  This method will update game state with valid moves for a Unit on Tile(x,y). 
		  *  This method will not check if Unit has already moved, nor attacked 
		  */
		
		public void highlightMoveAttackTiles(int x, int y) {
			
			highlightMoveTiles(x,y); 
			higlightAttackTiles(x,y); 
			if(!nextToProvoke(x,y)) {
				boolean[] valid = validStandardMoves(x,y); 
				for(int i = 0; i<valid.length ; i++) {
					if(valid[i]) highlightStandardAttack(x+moves[i][0], y+moves[i][1]); 
				}	
			}		
		}
		
			
		public boolean[] validStandardMoves(int x, int y) {
				
			    boolean[] valid = onBoard(x,y); 
			       
			       /////currently all on board options == true, and all off board options == false 
			       
			      ////filter out any spaces with enemies 
			   	for(int i = 0; i<moves.length; i++) {
			   		
			   	//		if(valid[i] && Tile(moves[i][0]+x,moves[i][1]+y).hasEnemy()) {
			   		    if(valid[i] && (testBoard[x+moves[i][0]][y+moves[i][1]] == 4 || testBoard[x+moves[i][0]][y+moves[i][1]] == 5)) {
			   				valid[i] = false; 
			   			}
			   		
			   	}
			   	////now Off board and enemy spaces are false
			   	////now check to see any blocked of areas -> can not pass through enemy unit
			   	
				   	if(!valid[2]) valid[0] = false ; 
				   	if(!valid[5]) valid[4] = false; 
				   	if(!valid[9]) valid[11] = false; 
				   	if(!valid[6]) valid[7] = false; 
				   	
				   	if(!valid[2] && !valid[6]) valid[3] = false; 
					if(!valid[9] && !valid[6]) valid[10] = false; 
					if(!valid[9] && !valid[5]) valid[8] = false; 
					if(!valid[5] && !valid[2]) valid[1] = false; 
				
				//now all valid moves and spaces occupied with friendly units are true, all else is false 
				//need to remove friendly units 
					
				for(int i = 0; i < moves.length ; i++) {
				//	if(valid[i] && Tile(moves[i][0]+x,moves[i][1]+y).hasUnit()) {
					if(valid[i] && testBoard[x+moves[i][0]][y+moves[i][1]]!=-1) {
					   valid[i] = false; 	
					}
				}
				return valid;   
			}
		
		
			public void resetTiles() {
				for(int x = 0; x < tileReferences.length; x++) {
					for(int y = 0; y< tileReferences[x].length; y++) {
						try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
						colours[x][y] = 0;
					}
				}
			}
			
			public void secondAttack() {
				
			}
			
			
			///////////////PRIVATE METHODS ///////////////////
			
			private boolean nextToProvoke(int x,int y) {
				
				//get all on board options, and set 0,4,9,11 to false if not already, as these spaces are not affected by provoke
				boolean[] valid = nextToOnBoard(x,y); 
				
				boolean nextToProvoke = false; 
				for(int i = 0; i < valid.length ; i++) {
				//	if(valid[i] && Tile(x+moves[i][0],y+moves[i][1]) has enemy provoke) {
					if(valid[i] && testBoard[x+moves[i][0]][y+moves[i][1]] == 4 ) {
					nextToProvoke = true;	
					}	
				}
				return nextToProvoke; 
			}
		
		
			private boolean[] onBoard(int x, int y) {
					
				 boolean[] valid = new boolean[12]; 
				 for(int i = 0; i<moves.length; i++){
				   		if(x+moves[i][0]>=0 && x+moves[i][0]<=8 && y+moves[i][1]>=0 && y+moves[i][1] <=4) valid[i] = true; 
				   		else valid[i]= false; 
				 }
				 return valid; 
			}
			
			private boolean[] nextToOnBoard(int x, int y) {
				
				boolean[] valid = onBoard(x, y); 
				valid[0]=false; valid[4] = false; valid[7]=false; valid[11]=false; 
				return valid; 
			}
			
			private boolean[] cardinallyNextToOnBoard(int x, int y) {
				
				boolean[] valid = nextToOnBoard(x,y); 
				valid[1]=false; valid[3] = false; valid[8]=false ; valid[10] = false;
				return valid; 				
			}
			
			private void highlightStandardAttack(int x, int y) {
				boolean[] valid = cardinallyNextToOnBoard(x,y);
				for(int i = 0; i < moves.length; i++) {
				//	if(valid[i] /* && Tile(moves[i][0]+x, moves[i][1]+y).containsEnemyUnit*/) {
					if(valid[i] && (testBoard[x+moves[i][0]][y+moves[i][1]]==4 || testBoard[x+moves[i][0]][y+moves[i][1]]==5)) {
						colours[moves[i][0]+x][moves[i][1]+y] = 2; 
					}
				}	
			}
			/* 
			 * This method will show valid move options, 
			 */
			private void highlightMoveTiles(int x, int y) {
				
				
				if(!nextToProvoke(x,y)) { //can not move. No tiles highlighted. 
					
				//	if(true/*TypeFlying()*/) { //flying move, any unoccupied space is highlighted 
					if(testBoard[x][y]==2) {
						for(int i = 0; i< tileReferences.length ; i++) {
							for(int j = 0; j < tileReferences[i].length; j++) {
				//				if(true /*Tile is empty*/) {
								if(testBoard[i][j]==-1) {
									try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
									colours[i][ j] =  1;
								}
							}
						}
					}else {			
						
						boolean[] valid = validStandardMoves(x,y); 
						for(int i = 0; i<valid.length; i++) {
							if(valid[i])colours[moves[i][0]+x][moves[i][1]+y] =  1; 
						}
					}
				}
			}
			
			public boolean isItTheSame(int[][] grid) {
				
				for(int i = 0; i < grid.length; i++) {
					for(int j = 0; j < grid[i].length; j++) {
						if(grid[i][j] != this.colours[i][j]) return false; 
					}
				}
				return true; 
			}

	}
	
	



}
