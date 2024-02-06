import Components.Card;
import Components.Player;
import Utilities.Functions;

import java.util.List;

class Simulate implements Runnable {
    private final Player player;
    private final List<Player> players;
    private final Object lock;
    private volatile boolean running;

    public Simulate(Player player, List<Player> players, Object lock) {
        this.player = player;
        this.players = players;
        this.lock = lock;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while (running && !Thread.interrupted()) {
                synchronized (lock) {
                    lock.wait(); // Wait for the main thread to notify before starting the turn
                    playTurn();
                    lock.notify(); // Notify the main thread that this player's turn is over
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }

    public void stop() {
        running = false;
    }

    public void playTurn() {
        synchronized (lock) {
            try {
                if (player.hand.isEmpty()) {
                    System.out.println(player.name + " is safe! (discarded all their cards)");
                    stop(); // stop thread when player discards all cards
                    return;
                }
                // clear the discarded cards, draw from previous player, discard matching pairs immediately
                player.discarded.clear();
                Functions.drawCardFromPrevPlayer(player, player.hand, players, player.playerIndex);
                Functions.discardMatchingPairs(player, player.hand, player.discarded);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }
}