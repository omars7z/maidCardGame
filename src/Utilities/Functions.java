package Utilities;

public class Functions {

//    public void removeInitialPairs() {
//        // Discarding matching pairs, excluding the Joker
//        System.out.print(name + " discarded all initial pairs: [");
//        for (int i = 0; i < hand.size() - 1; i++) {
//            for (int j = i + 1; j < hand.size(); j++) {
//                if (!hand.get(i).getValue().equals("") && !hand.get(j).getValue().equals("") &&
//                        hand.get(i).getValue().equals(hand.get(j).getValue()) &&
//                        !hand.get(i).getSuit().equals(hand.get(j).getSuit())) {
//                    System.out.print(hand.get(i) + " and " + hand.get(j));
//                    if (i < hand.size() - 2) {
//                        System.out.print(", ");
//                    }
//                    discarded.add(hand.get(i));
//                    discarded.add(hand.get(j));
//                    hand.remove(j);
//                    hand.remove(i);
//                }
//            }
//        }
//        System.out.println("]");
//
//        // Remove discarded cards from hand
//        hand.removeAll(discarded);
//    }
//    private void discardMatchingPairs(List<Card> hand, List<Card> discarded) {
//        for (int i = 0; i < hand.size() - 1; i++) {
//            for (int j = i + 1; j < hand.size(); j++) {
//                if (hand.get(i).getValue().equals(hand.get(j).getValue())
//                        && !hand.get(i).getSuit().equals(hand.get(j).getSuit())) {
//                    System.out.println(player.name + " discarded " + hand.get(i) + " and " + hand.get(j));
//                    discarded.add(hand.get(i));
//                    discarded.add(hand.get(j));
//                    hand.remove(j);
//                    hand.remove(i);
//                    // Adjust indices after removal
//                    i--;
//                    break;
//                }
//            }
//        }
//    }
//
//    private void drawCardFromPrevPlayer(List<Card> hand, List<Player> players, int playerIndex, String playerName, List<Card> discarded) {
//        Player prevPlayer = players.get((playerIndex - 1) % players.size());
//        if (!prevPlayer.hand.isEmpty()) {
//            Card drawnCard = prevPlayer.hand.remove(0);
//            System.out.println(playerName + " drew \"" + drawnCard + "\" from " + prevPlayer.name);
//            if (hand.stream().anyMatch(card -> card.getValue().equals(drawnCard.getValue()))) {
//                System.out.println(playerName + " discarded \"" + drawnCard + "\" because it matches a card in the hand.");
//                discarded.add(drawnCard);
//            } else {
//                hand.add(drawnCard);
//            }
//        }
//    }
}
