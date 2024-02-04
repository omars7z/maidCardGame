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
            discardMatchingPairs(player.hand, player.discarded);
            // Draw a card from the next player and discard if it matches
            drawCardFromPrevPlayer(player.hand, player.players, player.playerIndex, player.name, player.discarded);
        }
    }

    private void discardMatchingPairs(List<Card> hand, List<Card> discarded) {
        for (int i = 0; i < hand.size() - 1; i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).getValue().equals(hand.get(j).getValue())
                        && !hand.get(i).getSuit().equals(hand.get(j).getSuit())) {
                    System.out.println(player.name + " discarded " + hand.get(i) + " and " + hand.get(j));
                    discarded.add(hand.get(i));
                    discarded.add(hand.get(j));
                    hand.remove(j);
                    hand.remove(i);
                    // Adjust indices after removal
                    i--;
                    break;
                }
            }
        }
    }

    private void drawCardFromPrevPlayer(List<Card> hand, List<Player> players, int playerIndex, String playerName, List<Card> discarded) {
        Player prevPlayer = players.get((playerIndex - 1) % players.size());
        if (!prevPlayer.hand.isEmpty()) {
            Card drawnCard = prevPlayer.hand.remove(0);
            System.out.println(playerName + " drew \"" + drawnCard + "\" from " + prevPlayer.name);
            if (hand.stream().anyMatch(card -> card.getValue().equals(drawnCard.getValue()))) {
                System.out.println(playerName + " discarded \"" + drawnCard + "\" because it matches a card in the hand.");
                discarded.add(drawnCard);
            } else {
                hand.add(drawnCard);
            }
        }
    }
}
