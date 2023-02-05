package structures;
import structures.basic.Board;
import structures.basic.Tile;

/**
 * TileAux is used to pass information about tiles to be updated at the front end. It stores
 * Tile reference, and mode that the tile should be rendered
 * @author Stuart Miller
 */
public class TileAux {
		private Tile tile;
		private int mode;
		public TileAux(int x, int y , int mode, Board board) {
			this.tile = board.getTileReference(x,y);
			this.mode=mode;
		}

		public Tile getTileReference() {
			return this.tile;
		}

		public int getMode() {
			return mode;
		}

		public String toString() {
			return ""+tile + mode;
		}

		public boolean equals(TileAux that) {

			if(!(that instanceof TileAux)) return false;

			that = (TileAux) that;

			if(this.tile.equals(that.tile) && this.mode == that.mode) return true;
			return false;
		}

}
