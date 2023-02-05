package structures;

import java.util.ArrayList;

import commands.BasicCommands;
import structures.basic.Board;
import structures.basic.Taggable;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * This static class is to assess whether each move is valid, and allowed under the rules.
 * It can be called to give all possible moves from one position on the board, and it will
 * return info to the Board class to update the front end. 
 *
 * The indexes in the int[][] moves refer to the move options in the order below starting at
 * point x (for internal calculation. Users do not need to have knowledge of this)
 *  				 [0 ]
 * 				  [1][2 ][3 ]
 *             [4][5][x ][6 ][7]
 *                [8][9 ][10]
 *                   [11]
 *                   
 *   @author Stuart Miller
 *
 */

public class ValidMoveAttackController {

	static final int[][] moves = { { 0, -2 }, { -1, -1 }, { 0, -1 }, { 1, -1 }, { -2, 0 }, { -1, 0 }, { 1, 0 },
			{ 2, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 }, { 0, 2 } };
	private static String enemy;

	/**
	 * 
	 * Main entry to highlight features. Given an x,y coordinate, this will return true if there
	 * is at least one move or attack option for unit on x,y
	 * ArrayList of TileAux (Tile, highlight-mode) is used to pass information of move and attack options
	 * around this class
	 *
	 * @param board Board object being used
	 * @param x starting x-coordinate of unit
	 * @param y starting y-coordinate of unit
	 * @param t unit on grid position
	 * @return validOptions returns true if there is one or more valid options for given unit. False if not. 
	 */

	public static boolean validMoves(Board board, int x, int y, Unit t) {

		ArrayList<TileAux> validOptions = new ArrayList<>();
		setEnemy(t);

		Unit unit = board.getTileReference(x, y).getUnit();
		System.out.print("The unit ");
		System.out.print(unit);
		if (unit == null) {
			System.out.println("is null");
			return false;
		} else if (unit.getOwner().equals(enemy)) {
			return false;
		} else if (unit.checkTag("new")) {
			System.out.println("is new");
			return false;
		} else if (unit.checkTag("attacked") && unit.checkTag("attack2") && !unit.checkTag("spentAttack2")) {
			System.out.println("Can second attack");
			validOptions = doubleAttackSecondAttack(board, x, y);
		} else if (unit.checkTag("attacked")) {
			return false;
		} else if (unit.checkTag("moved")) {
			System.out.println("can Attack");
			validOptions = highlightAttackTiles(board, x, y);
		} else {
			System.out.println("can move");
			validOptions = highlightMoveAttackTiles(board, x, y);
		}
		updateModes(validOptions);
		return !validOptions.isEmpty();
	}

	/**
	 * This method will highlight all tile spaces that a unit can spawn on.
	 * @param board the board being used
	 * @param s special ability
	 * @return options true if there is a valid spawn location for unit. False if not
	 */
	public static boolean highlightSpawn(Board board, String s) {

		setEnemy(board);
		ArrayList<TileAux> options = new ArrayList<>();
		if (s.equals("airdrop"))
			options = flying(board);
		else {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 5; j++) {
					if (board.getTileReference(i, j).getUnit() != null
							&& !(board.getTileReference(i, j).getUnit().getOwner().equals(enemy))) {
						boolean[] valid = GridOptions.nextToOnBoard(i, j);
						for (int k = 0; k < valid.length; k++) {
							if (valid[k]
									&& board.getTileReference(i + moves[k][0], j + moves[k][1]).getUnit() == null) {
								options.add(new TileAux(i + moves[k][0], j + moves[k][1], 1, board));
							}
						}
					}
				}
			}
		}

		updateModes(options);
		return !options.isEmpty();
	}

	/**
	 * Main method for highlighting the targets of spells. These are different for
	 * every spell card
	 * 
	 * @param board the board currently being used
	 * @param t The taggable object (Card) that the highlighting should reflect
	 * @return options true if there is at least one option for playing card. False if not
	 */

	public static boolean highlightSpellTargets(Board board, Taggable t) {
		ArrayList<TileAux> options = new ArrayList<>();
		setEnemy(board);
		if (t.checkTag("onEnemy"))
			options = rangedFlyingAttack(board);
		if (t.checkTag("notAvatar")) {
			for (int i = 0; i < options.size(); i++) {
				if (options.get(i).getTileReference().getUnit().checkTag("avatar")
						|| options.get(i).getTileReference().getUnit().checkTag("enemy_avatar"))
					options.remove(i);
			}
		} else if (t.checkTag("onSelf")) {
			options = findFriendlies(board);
		} else if (t.checkTag("ownAvatar")) {
			ArrayList<TileAux> allFriendlies = findFriendlies(board);
			for (TileAux ta : allFriendlies) {
				if (ta.getTileReference().getUnit().checkTag("avatar")
						|| ta.getTileReference().getUnit().checkTag("enemy_avatar"))
					options.add(ta);
			}
		}
		updateModes(options);
		System.out.println("Spells options" + !options.isEmpty());
		return !options.isEmpty();
	}

	/**
	 * This method will take a list of TileAux objects, and change the attributes in
	 * the Tile object to match the move and attack modes in the list
	 * 
	 * @param options list of options that the unit can make as TileAux objects
	 */
	private static void updateModes(ArrayList<TileAux> options) {

		for (TileAux ta : options)
			ta.getTileReference().setMode(ta.getMode());
	}

	/**
	 * To set the class attribute of owner. Used to determine friendly units from enemy
	 * @param board the board being used
	 */

	public static void setEnemy(Board board) {
		if (board.getGameState().getTurn() % 2 == 1)
			enemy = "ai";
		else
			enemy = "player";
	}
	/**
	 * To set the class attribute of owner. Used to determine friendly units from enemy
	 * @param unit a unit that is wanting to be moved
	 */

	public static void setEnemy(Unit unit) {
		System.out.println(unit);
		if (unit.getOwner().equals("player"))
			enemy = "ai";
		else
			enemy = "player";
	}

	/**
	 * This method will return an ArrayList of TileAux objects relating to tiles that all friendly units are on.
	 * This is used for highlighting targets of Spells
	 * 
	 * @param board being used
	 * @return options list of tiles with highlight option 1
	 */
	private static ArrayList<TileAux> findFriendlies(Board board) {

		ArrayList<TileAux> options = new ArrayList<TileAux>();

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				if (board.getTileReference(i, j).getUnit() != null
						&& !board.getTileReference(i, j).getUnit().getOwner().equals(enemy)) {
					options.add(new TileAux(i, j, 1, board));
				}
			}
		}
		return options;
	}

	/**
	 * Will check to see if a valid move & attack combo has been selected. If valid, this
	 * will return an appropriate Tile to move to before attack. If not, null. It will avoid moving to a tile under provoke if possible
	 *@param board the board being used
	 * @param fromx starting x of attacker
	 * @param fromy starting y of attacker
	 * @param tox x-value of attacked unit
	 * @param toy y-value of attacked unit
	 * @return tile appropriate move option before attack
	 */

	public static Tile validMoveAttack(Board board, int fromx, int fromy, int tox, int toy) {

		System.out.println(String.format("so we need to move from %d,%d", fromx, fromy));
		if (board.getTileReference(fromx, fromy).getUnit().checkTag("attack2")) {
			updateModes(highlightMoveAttackTiles(board, fromx, fromy));
		} else {
			validMoves(board, fromx, fromy, board.getTileReference(fromx, fromy).getUnit());
		}
		Tile attack = board.getTileReference(tox, toy);
		int dX = 0, dY = 0;
		double min = Double.MAX_VALUE;
		int X = 0, Y = 0;
		// test to see if desired attack is valid.
		if (!(attack.getMode() == 2))
			return null;

		// finding a tile to move to, avoiding Provoke if possible

		else {
			System.out.println("Its valid, lets find a tile");

			if (attack.getUnit().checkTag("provoke")) { // attacking provoke
				boolean[] valid = GridOptions.nextToOnBoard(tox, toy);
				for (int i = 0; i < valid.length; i++) {
					if (valid[i] && board.getTileReference(moves[i][0] + tox, moves[i][1] + toy).getMode() == 1) {
						// return board.getTileReference(moves[i][0]+tox, moves[i][1] + toy);
						dX = Math.abs(moves[i][0] + tox - fromx);
						dY = Math.abs(moves[i][1] + toy - fromy);
						if (Math.sqrt(dY * dY + dX * dX) < min) {
							min = Math.sqrt(dY * dY + dX * dX);
							X = moves[i][0] + tox;
							Y = moves[i][1] + toy;
						}
					}
				}
			} else {
				boolean[] valid = GridOptions.nextToOnBoard(tox, toy);
				for (int i = 0; i < valid.length; i++) {
					if (valid[i] && board.getTileReference(moves[i][0] + tox, moves[i][1] + toy).getMode() == 1
							&& !nextToProvoke(board, moves[i][0] + tox, moves[i][1] + toy)) {
						dX = Math.abs(moves[i][0] + tox - fromx);
						dY = Math.abs(moves[i][1] + toy - fromy);
						if (Math.sqrt(dY * dY + dX * dX) < min) {
							min = Math.sqrt(dY * dY + dX * dX);
							X = moves[i][0] + tox;
							Y = moves[i][1] + toy;
						}
					}
				}
			}
			System.out.println(String.format("move to tile %d,%d", X, Y));

		}
		return board.getTileReference(X, Y);
	}

	/**
	 * This will return valid attacks from one position. Considers abilities of provoke, ranges, and flying
	 * @param board the board being used
	 * @param x x-position of attacker
	 * @param y y-position of attacker
	 * @return result TileAux ArrayList of valid attacks
	 */

	private static ArrayList<TileAux> highlightAttackTiles(Board board, int x, int y) {

		ArrayList<TileAux> result = new ArrayList<>();
		Unit unit = board.getTileReference(x, y).getUnit();

		if (nextToProvoke(board, x, y)) {
			result.addAll(attackUnderProvoke(board, x, y));
		} else if (unit.checkTag("flying") || unit.checkTag("ranged")) {
			result.addAll(rangedFlyingAttack(board));
		} else {
			result.addAll(highlightStandardAttack(board, x, y));
		}
		return result;
	}

	/**
	 * Returns the move options for a unit with no special move abilities
	 * @param x starting x-position
	 * @param y starting y-position
	 * @return result TileAux ArrayList of move options
	 */

	private static ArrayList<TileAux> highlightStandardAttack(Board board, int x, int y) {

		ArrayList<TileAux> result = new ArrayList<>();
		boolean[] valid = GridOptions.nextToOnBoard(x, y);

		for (int i = 0; i < moves.length; i++) {
			try {
				if (valid[i] && board.getTileReference(x + moves[i][0], y + moves[i][1]).getUnit().getOwner()
						.equals(enemy)) {
					System.out.println("Should be adding attack");
					result.add(new TileAux(moves[i][0] + x, moves[i][1] + y, 2, board));
				}
			} catch (NullPointerException e) {
			}
		}
		return result;
	}
	/**
	 * Returns all possible move and attack options for a unit
	 * @param board the board being used
	 * @param x starting x-position of unit
	 * @param y starting y-position of unit
	 * @return result TileAux ArrayList of all move and attack options
	 */

	private static ArrayList<TileAux> highlightMoveAttackTiles(Board board, int x, int y) {

		ArrayList<TileAux> result = new ArrayList<>();
		Unit unit = board.getTileReference(x, y).getUnit();

		result.addAll(highlightMoveTiles(board, x, y));
		result.addAll(highlightAttackTiles(board, x, y));
		if (unit.checkTag("flying") || unit.checkTag("ranged"))
			return result;
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).getMode() == 1) {
				Tile t = result.get(i).getTileReference();
				if (nextToProvoke(board, t.getTilex(), t.getTiley())) {
					result.addAll(attackUnderProvoke(board, t.getTilex(), t.getTiley()));
				} else {
					result.addAll(highlightStandardAttack(board, t.getTilex(), t.getTiley()));
				}
			}
		}
		return result;
	}

	/**
	 * Gives all move options for a given starting position. Considers effects of provoke and flying units
	 * @param board the board being used
	 * @param x starting x-position of unit
	 * @param y starting y-position of unit
	 * @return result TileAux ArrayList of move options
	 */

	private static ArrayList<TileAux> highlightMoveTiles(Board board, int x, int y) {

		ArrayList<TileAux> result = new ArrayList<>();
		Unit unit = board.getTileReference(x, y).getUnit();
		if (!nextToProvoke(board, x, y)) {
			if (unit.checkTag("flying")) {
				result.addAll(flying(board));
			} else {
				boolean[] valid = validStandardMoves(board, x, y);
				for (int i = 0; i < valid.length; i++) {
					if (valid[i])
						result.add(new TileAux(moves[i][0] + x, moves[i][1] + y, 1, board));
				}
			}
		}
		return result;
	}

	/**
	 * Gives all second attack options for the double attack unit feature
	 * @param board the board being used
	 * @param x starting x-position of unit
	 * @param y starting y-position of unit
	 * @return valid TileAux ArrayList of move options
	 */

	public static ArrayList<TileAux> doubleAttackSecondAttack(Board board, int x, int y) {

		ArrayList<TileAux> valid = highlightMoveAttackTiles(board, x, y);

		for (int i = 0; i < valid.size();) {
			if (valid.get(i).getMode() != 2)
				valid.remove(i);
			else
				i++;
		}
		return valid;
	}

	/**
	 * Returns move options that a unit can make. Considers enemy unit placement, and positions
	 * that may be blocked off as a result
	 * @param board the board being used
	 * @param x starting x-position
	 * @param y starting y-position
	 * @return valid move options allowed, relating to the moves class attribute
	 */

	private static boolean[] validStandardMoves(Board board, int x, int y) {

		boolean[] valid = GridOptions.onBoard(x, y);

		///// currently all on board options == true, and all off board options == false
		//// filter out any spaces with enemies
		for (int i = 0; i < moves.length; i++) {
			// not safe
			int safeX = x + moves[i][0];
			int safeY = y + moves[i][1];
			if ((safeX < 0 || safeX > 8) || (safeY < 0 || safeY > 4))
				continue;
			Unit unit = board.getTileReference(safeX, safeY).getUnit();
			if (valid[i] && unit != null && unit.getOwner().equals(enemy)) {
				valid[i] = false;
			}
		}
		//// now Off board and enemy spaces are false
		//// now check to see any blocked of areas -> can not pass through enemy unit

		if (!valid[2])	valid[0] = false;
		if (!valid[5])	valid[4] = false;
		if (!valid[9])	valid[11] = false;
		if (!valid[6])	valid[7] = false;

		if (!valid[2] && !valid[6])	valid[3] = false;
		if (!valid[9] && !valid[6])	valid[10] = false;
		if (!valid[9] && !valid[5]) valid[8] = false;
		if (!valid[5] && !valid[2]) valid[1] = false;

		// now all valid moves and spaces occupied with friendly units are true, all
		// else is false
		// need to remove friendly units

		for (int i = 0; i < moves.length; i++) {
			if (valid[i] && board.getTileReference(x + moves[i][0], y + moves[i][1]).getUnit() != null) {
				valid[i] = false;
			}
		}
		return valid;
	}

	/**
	 * Checks if a given position is next to an provoke unit
	 * @param board the board being used
	 * @param x x-pos of friendly unit
	 * @param y y-pos of friendly unit
	 * @return nextToProvoke whether there is an enemy provoke next to unit
	 */
	private static boolean nextToProvoke(Board board, int x, int y) {

		boolean[] valid = GridOptions.nextToOnBoard(x, y);
		boolean nextToProvoke = false;

		for (int i = 0; i < valid.length; i++) {
			try {

				if (valid[i] && board.getTileReference(x + moves[i][0], y + moves[i][1]).getUnit().checkTag("provoke")
						&& board.getTileReference(x + moves[i][0], y + moves[i][1]).getUnit().getOwner()
								.equals(enemy)) {
					nextToProvoke = true;
				}
			} catch (NullPointerException e) {
			}
		}
		return nextToProvoke;
	}

	/**
	 * For flying ability. Returns all unoccupied spaces on the board
	 * @param board the board being used
	 * @return options TileAux ArrayList of all unoccupied spaces
	 */
	public static ArrayList<TileAux> flying(Board board) {

		ArrayList<TileAux> options = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				if (board.getTileReference(i, j).getUnit() == null) {
					options.add(new TileAux(i, j, 1, board));
				}
			}
		}
		return options;
	}

	/**
	 * returns attack options for unit under provoke. Could be one or more tiles with provoke units on them
	 * @param board the board being used
	 * @param x x-position of attacker
	 * @param y y-pos of attacker
	 * @return options TileAux ArrayList of attack options
	 */

	private static ArrayList<TileAux> attackUnderProvoke(Board board, int x, int y) {

		ArrayList<TileAux> options = new ArrayList<>();
		boolean[] valid = GridOptions.nextToOnBoard(x, y);
		for (int i = 0; i < valid.length; i++) {
			try {
				if (valid[i]) {
					Unit unit = board.getTileReference(x + moves[i][0], y + moves[i][1]).getUnit();
					if (unit.checkTag("provoke") && unit.getOwner().equals(enemy)) {
						options.add(new TileAux(moves[i][0] + x, moves[i][1] + y, 2, board));
					}
				}
			} catch (NullPointerException e) {}
		}
		return options;
	}

	/**
	 * Returns all enemy units on the board for ranges attack. Does not consider provoke.
	 * @board the board being used
	 * @return options TileAux ArrayList of all enemy units
	 */

	private static ArrayList<TileAux> rangedFlyingAttack(Board board) {

		ArrayList<TileAux> options = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				try {
					if (board.getTileReference(i, j).getUnit().getOwner().equals(enemy)) {
						options.add(new TileAux(i, j, 2, board));
					}
				} catch (NullPointerException e) {
				}
			}
		}
		return options;
	}
}
