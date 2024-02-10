package Utilities;

import Components.Card;
import Components.Player;

import java.util.List;

public class Functions {

    public static void removeInitialPairs(Player player) {
        // Discarding matching pairs, excluding the Joker
        System.out.print(player.name + " discarded all initial pairs: [");
        System.out.println();
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
        boolean foundMatchingPair = true;
        while (foundMatchingPair) {
            foundMatchingPair = false; // Reset flag for each iteration
            for (int i = 0; i < hand.size() - 1; i++) {
                for (int j = i + 1; j < hand.size(); j++) {
                    Card card1 = hand.get(i);
                    Card card2 = hand.get(j);

                    // Check if the two cards form a matching pair
                    if (areMatchingSuits(card1, card2) && card1.getValue().equals(card2.getValue())) {
                        System.out.println(player.name + " discarded " + card1 + " and " + card2);
                        discarded.add(card1);
                        discarded.add(card2);
                        hand.remove(card1);
                        hand.remove(card2);
                        foundMatchingPair = true;
                        break;
                    }
                }
                if (foundMatchingPair) {
                    break;
                }
            }
        }
    }

    private static boolean areMatchingSuits(Card card1, Card card2) {
        String suit1 = card1.getSuit();
        String suit2 = card2.getSuit();
        return (suit1.equals("Spades") && suit2.equals("Clubs")) || (suit1.equals("Clubs") && suit2.equals("Spades")) ||
                (suit1.equals("Diamonds") && suit2.equals("Hearts")) || (suit1.equals("Hearts") && suit2.equals("Diamonds"));
    }

    public static void drawCardFromPrevPlayer(Player player, List<Card> hand, List<Player> players, int playerIndex) {
        Player prevPlayer = players.get((playerIndex - 1 + players.size()) % players.size());
        if (!prevPlayer.hand.isEmpty()) {
            Card drawnCard = prevPlayer.hand.remove(0);
            System.out.println(player.name + " drew \"" + drawnCard + "\" from " + prevPlayer.name);

            boolean matchingCardFound = hand.removeIf(card -> {
                if (card.getValue().equals(drawnCard.getValue())) {
                    System.out.println(player.name + " discarded \"" + card + "\" because it matches a card in deck");
                    player.discarded.add(card);
                    return true;
                }
                return false;
            });

            if (!matchingCardFound) {
                hand.add(drawnCard);
            } else {
                discardMatchingPairs(player, hand, player.discarded);
            }
        }
    }
}
