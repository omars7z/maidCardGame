package Components;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public final String name;
    public final List<Card> hand;
    public final Object lock;
    public final List<Player> players;
    public int playerIndex;
    public final List<Card> discarded;

    public Player(String name, Object lock, List<Player> players, int playerIndex) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.lock = lock;
        this.players = players;
        this.playerIndex = playerIndex;
        this.discarded = new ArrayList<>(); // Initialize the discarded cards list
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public List<Card> getDeck() {
        return new ArrayList<>(hand);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" deck: [");
        for (int i = 0; i < hand.size(); i++) {
            sb.append(hand.get(i));
            if (i < hand.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public void removeInitialPairs() {
        // Discarding matching pairs
        System.out.print(name + " discarded all initial pairs: [");
        for (int i = 0; i < hand.size() - 1; i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).getValue().equals(hand.get(j).getValue())
                        && !hand.get(i).getSuit().equals(hand.get(j).getSuit())) {
                    System.out.print(hand.get(i) + " and " + hand.get(j));
                    if (i < hand.size() - 2) {
                        System.out.print(", ");
                    }
                    discarded.add(hand.get(i));
                    discarded.add(hand.get(j));
                    hand.remove(j);
                    hand.remove(i);
                }
            }
        }
        System.out.println("]");

        // Remove discarded cards from hand
        hand.removeAll(discarded);

    }
}
