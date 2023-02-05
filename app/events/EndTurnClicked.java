package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 *
 * {
 *   messageType = “endTurnClicked”
 * }
 *
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// only humans can click the end turn button
		if(gameState.getListenToFrontEnd()) {
			// end the human turn
			gameState.nextTurn();
			// do an AI turn
			gameState.getAI().takeTurn();
		}

	}

}
