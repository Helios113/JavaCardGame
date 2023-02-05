package events;


import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import java.util.Date;

/**
 * Indicates that a unit instance has stopped moving. 
 * The event reports the unique id of the unit.
 * 
 * { 
 *   messageType = “unitStopped”
 *   id = <unit id>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class UnitStopped implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		Date date = new Date();
		int unitid = message.get("id").asInt();
		System.out.println(date.getTime());
		if(gameState.getTurn()%2==1) gameState.setListenToFrontEnd(true);
		else gameState.setListenToFrontEnd(false);
	}

}
