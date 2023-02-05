package structures.basic;

import structures.GameState;

/**
* utility thread that waits for a set time before resetting the
* listenToFrontEnd bool in gameState, after an AI turn.
* Gives the main game thread time to process (and ignore) any click 
* events made during the AI turn that might be in the queue.
* @author Mike Parr Burman
**/
public class Waiter implements Runnable {
    private int WAIT_TIME = 1000;
    GameState gameState;

    public Waiter(GameState gameState) {
        this.gameState = gameState;
    }

    public void run() {

        // wait first
        try {Thread.sleep(WAIT_TIME);} catch (InterruptedException e) {e.printStackTrace();}

        // then set the flag
        gameState.setListenToFrontEnd(true);
    }
}
