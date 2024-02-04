import Components.Card;
import Components.Player;
import Utilities.Functions;

import java.util.List;

public class Simulate implements Runnable {
    private final Player player;
    private final List<Player> players;
    private final Object lock;
    private boolean running;

    public Simulate(Player player, List<Player> players, Object lock) {
        this.player = player;
        this.players = players;
        this.lock = lock;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            synchronized (lock) {
                lock.wait(); // Wait for the main thread to notify before starting the turn
            }
            while (running) {
                playTurn();
                synchronized (lock) {
                    lock.notify(); // Notify the main thread that this player's turn is over
                    if (running) {
                        lock.wait(); // Wait for the main thread to notify before starting the next turn
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
    }

    public void playTurn() throws InterruptedException {
        synchronized (lock) {
            if (player.hand.isEmpty()) {
                System.out.println(player.name + " is safe! (discarded all his cards)");
                stop(); // Stop the thread when the game is over for this player
                return;
            }
            // Clear the discarded cards list before each turn
            player.discarded.clear();
            // Discard matching pairs
            Functions.discardMatchingPairs(player, player.hand, player.discarded);
            // Draw a card from the previous player and discard if it matches
            Functions.drawCardFromPrevPlayer(player, player.hand, player.players, player.playerIndex);
        }
    }
}
