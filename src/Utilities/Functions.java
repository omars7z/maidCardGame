package Utilities;

import Components.Card;
import Components.Player;

import java.util.List;

public class Functions {

    public static void removeInitialPairs(Player player) {
        // Discarding matching pairs, excluding the Joker
        System.out.print(player.name + " discarded all initial pairs: [");
        for (int i = 0; i < player.hand.size() - 1; i++) {
            for (int j = i + 1; j < player.hand.size(); j++) {
                if (!player.hand.get(i).getValue().equals("") && !player.hand.get(j).getValue().equals("") &&
                        player.hand.get(i).getValue().equals(player.hand.get(j).getValue()) &&
                        !player.hand.get(i).getSuit().equals(player.hand.get(j).getSuit())) {
                    System.out.print(player.hand.get(i) + " and " + player.hand.get(j));
                    if (i < player.hand.size() - 2) {
                        System.out.print(", ");
                    }
                    player.discarded.add(player.hand.get(i));
                    player.discarded.add(player.hand.get(j));
                    player.hand.remove(j);
                    player.hand.remove(i);
                }
            }
        }
        System.out.println("]");

        // Remove discarded cards from hand
        player.hand.removeAll(player.discarded);
    }

    public static void discardMatchingPairs(Player player, List<Card> hand, List<Card> discarded) {
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

    public static void drawCardFromPrevPlayer(Player player, List<Card> hand, List<Player> players, int playerIndex) {
        Player prevPlayer = players.get((playerIndex - 1) % players.size());
        if (!prevPlayer.hand.isEmpty()) {
            Card drawnCard = prevPlayer.hand.remove(0);
            System.out.println(player.name + " drew \"" + drawnCard + "\" from " + prevPlayer.name);
            if (hand.stream().anyMatch(card -> card.getValue().equals(drawnCard.getValue()))) {
                System.out.println(player.name + " discarded \"" + drawnCard + "\" because it matches a card in the hand.");
                player.discarded.add(drawnCard);
            } else {
                hand.add(drawnCard);
            }
        }
    }
}
