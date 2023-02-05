package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import controllers.PlayController;
/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 *
 * {
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 *
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		/*
		 * If this message is received during the player's turn, act on click
		 * Otherwise, do nothing 
		 */
		
		if (gameState.getListenToFrontEnd()) {
			int handPosition = message.get("position").asInt();
			
			PlayController.handlePlay(handPosition);
		}
	}

}
