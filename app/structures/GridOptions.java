package structures;
 /**
  * Class for providing coordinate for move and attack options 
  * 
  * @author Stuart Miller
  */
public class GridOptions {

	/**
	 * Returns options relating to the moves class attribute that are still on the
	 * board from the starting x,y point
	 * @param x start x-position
	 * @param y start y-position
	 * @return valid positions on the board relating to class attribute moves
	 */
	
	static boolean[] onBoard(int x, int y) {
	
		boolean[] valid = new boolean[12];
		for (int i = 0; i < ValidMoveAttackController.moves.length; i++) {
			if (x + ValidMoveAttackController.moves[i][0] >= 0 && x + ValidMoveAttackController.moves[i][0] <= 8 && y + ValidMoveAttackController.moves[i][1] >= 0 && y + ValidMoveAttackController.moves[i][1] <= 4)
				valid[i] = true;
			else
				valid[i] = false;
		}
		return valid;
	}

	/**
	 * Returns the positions that are immediately surrounding a given x,y point
	 * @param x start x-position
	 * @param y start y-position
	 * @return valid positions on the board relating to class attribute moves
	 */
	static boolean[] nextToOnBoard(int x, int y) {
	
		boolean[] valid = onBoard(x, y);
		valid[0] = false;
		valid[4] = false;
		valid[7] = false;
		valid[11] = false;
		return valid;
	}

	/** 
	 * This method takes two grid references and checks if they are adjacent.
	 * @param x1 
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return true if next to each other, false otherwise
	 */
	public static boolean areTilesNextToEachOther(int x1, int y1, int x2, int y2) {
		boolean[] valid = nextToOnBoard(x1, y1);
		for (int i = 0; i < valid.length; i++) {
			if (valid[i] && x2 == (ValidMoveAttackController.moves[i][0] + x1) && y2 == (ValidMoveAttackController.moves[i][1] + y1)) {
				return true;
			}
		}
		return false;
	}

}
