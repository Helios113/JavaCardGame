package events;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Date;
import akka.actor.ActorRef;
import structures.GameState;
import java.util.Date;
/**
 * Indicates that a unit instance has started a move. 
 * The event reports the unique id of the unit.
 * 
 * { 
 *   messageType = “unitMoving”
 *   id = <unit id>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class UnitMoving implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		Date date = new Date();
		int unitid = message.get("id").asInt();
		System.out.println(date.getTime());
		gameState.setListenToFrontEnd(false);
		
	}

}
