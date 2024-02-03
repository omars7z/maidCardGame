import Components.Generate;
import Components.Card;
import Components.Player;

import java.util.List;

public class Simulate implements Runnable {
    private final Player player;
    private final List<Player> players;
    private final Object lock;

    public Simulate(Player player, List<Player> players, Object lock) {
        this.player = player;
        this.players = players;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            while (true) {
                playTurn();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void playTurn() throws InterruptedException {
        synchronized (lock) {
            lock.wait(); // Wait for its turn to play
            if (player.hand.isEmpty()) {
                System.out.println("Game Over! " + player.name + " lost!");
                return;
            }
            // Clear the discarded cards list before each turn
            player.discarded.clear();
            // Discard matching pairs
            discardMatchingPairs(player.hand, player.discarded);
            // Draw a card from the next player and discard if it matches
            drawCardFromNextPlayer(player.hand, player.players, player.playerIndex, player.name, player.discarded);
            // Notify the next player to play
            Player nextPlayer = players.get((player.playerIndex + 1) % players.size());
            synchronized (nextPlayer.lock) {
                nextPlayer.lock.notify();
            }
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

    private void drawCardFromNextPlayer(List<Card> hand, List<Player> players, int playerIndex, String playerName, List<Card> discarded) {
        Player nextPlayer = players.get((playerIndex + 1) % players.size());
        if (!nextPlayer.hand.isEmpty()) {
            Card drawnCard = nextPlayer.hand.remove(0);
            System.out.println(playerName + " drew " + drawnCard + " from " + nextPlayer.name);
            if (hand.stream().anyMatch(card -> card.getValue().equals(drawnCard.getValue()))) {
                System.out.println(playerName + " discarded " + drawnCard + " because it matches a card in the hand.");
            } else {
                hand.add(drawnCard);
            }
        }
    }
}