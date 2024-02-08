import Components.Card;
import Components.Player;
import Utilities.Functions;

import java.util.List;

class Simulate implements Runnable {
    private final Player player;
    private final List<Player> players;
    private final Object lock;
    private volatile boolean running;  //changes to its value by main thread are immediately visible to all simulate threads

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
                    lock.wait(); // wait for the main thread to notify before starting the turn
                    playTurn();
                    lock.notify(); // notify the main thread that this player's turn is over
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // interrupted status
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
                // clear discarded cards, draw from previous player, discard matching pairs immediately
                player.discarded.clear();
                Functions.drawCardFromPrevPlayer(player, player.hand, players, player.playerIndex);
                Functions.discardMatchingPairs(player, player.hand, player.discarded);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }
}