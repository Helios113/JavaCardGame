package structures.basic.ai;

import structures.basic.Position;
import structures.basic.Tile;

/**
* stores information about a valid action that an ai unit can take:
* 	- its current tile
*	- the target tile of this action
*	- a score indicating how good an action the AI thinks it is
* 	note this action could be a move, an attack, or a move-and-attack, and this is taken into account inside the Brain class when scoring.
* @author Mike Parr-Burman
*/

public class PlayableAction implements Comparable<PlayableAction>{

	private Tile from; // where is the unit
	private Tile target; // where can it go/attack?
	private int score; // how good is the move?

	//constructor (only tile refs not score)
	public PlayableAction(Tile from, Tile target) {
		this.from = from;
		this.target = target;
	}

	// getter/setters
	public Tile getFrom() {
		return from;
	}
	public int getFromX() {
		return from.getTilex();
	}

	public int getFromY() {
		return from.getTiley();
	}

	public Tile getTarget() {
		return target;
	}
	public int getTargetX() {
		return target.getTilex();
	}

	public int getTargetY() {
		return target.getTiley();
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return score;
	}

	// comparable based on score so we can compare which action  is best easily
	@Override
	public int compareTo(PlayableAction a) {
		int that = a.getScore();
		if (this.getScore() == that) return 0;
		else if (this.getScore() > that) return 1;
		else return -1;
	}

}
