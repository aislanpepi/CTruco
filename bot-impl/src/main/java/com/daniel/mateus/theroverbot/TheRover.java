package com.daniel.mateus.theroverbot;

import com.bueno.spi.model.CardToPlay;
import com.bueno.spi.model.GameIntel;
import com.bueno.spi.model.TrucoCard;
import com.bueno.spi.service.BotServiceProvider;

public class TheRover implements BotServiceProvider {

    @Override
    public boolean getMaoDeOnzeResponse(GameIntel intel) {
        return false;
    }

    @Override
    public boolean decideIfRaises(GameIntel intel) {
        return getCurrentRound(intel) == 2 && isPlayingFirst(intel) && handHasCardOverRelativeValue(intel, 8);
    }

    @Override
    public CardToPlay chooseCard(GameIntel intel) {
        return switch (getCurrentRound(intel)) {
            case 1 -> CardToPlay.of(chooseCardFirstHand(intel));
            case 2 -> CardToPlay.of(chooseCardSecondHand(intel));
            case 3 -> CardToPlay.of(chooseCardThirdHand(intel));
            default -> CardToPlay.of(intel.getCards().get(0));
        };
    }

    @Override
    public int getRaiseResponse(GameIntel intel) {
        return 0;
    }

    @Override
    public String getName() {
        return "The Rover";
    }

    public boolean isPlayingFirst(GameIntel intel) {
        return intel.getOpponentCard().isEmpty();
    }

    public int getCurrentRound (GameIntel intel) {
        int cardsInHand = intel.getCards().size();
        if (cardsInHand == 3) return 1;
        if (cardsInHand == 2) return 2;
        if (cardsInHand == 1) return 3;
        return -1;
    }

    public TrucoCard chooseCardFirstHand (GameIntel intel) {

        if (isPlayingFirst(intel)) {
            int manilhaQtd = 0;
            for(int i = 0; i < intel.getCards().size(); i++) {
                if(intel.getCards().get(i).isManilha(intel.getVira())) {
                    manilhaQtd++;
                }
            }

            if(manilhaQtd >= 2) {
                TrucoCard lowestManilha = intel.getCards().get(0);
                for(int i = 0; i < intel.getCards().size(); i++) {
                    TrucoCard currentCard = intel.getCards().get(i);
                    if ((currentCard.relativeValue(intel.getVira()) < lowestManilha.relativeValue(intel.getVira()) &&
                            currentCard.isManilha(intel.getVira())) || !lowestManilha.isManilha(intel.getVira()) )  {
                        lowestManilha = currentCard;
                    }

                }
                return lowestManilha;
            }

            TrucoCard highestCard = intel.getCards().get(0);
            for(int i = 0; i < intel.getCards().size(); i++) {
                TrucoCard currentCard = intel.getCards().get(i);
                if ((currentCard.relativeValue(intel.getVira()) > highestCard.relativeValue(intel.getVira()) &&
                        !currentCard.isManilha(intel.getVira())) || highestCard.isManilha(intel.getVira())) {
                    highestCard = currentCard;
                }

            }
            return highestCard;
        }

        TrucoCard cardToPlay = getLowestCardInHandThatBeatOpponentCard(intel);
        if(cardToPlay == null) cardToPlay = getLowestCardInHand(intel);
        return cardToPlay;
    }

    public int countManilhasInHand(GameIntel intel){
        return 0;
    }

    public TrucoCard getLowestCardInHandThatBeatOpponentCard (GameIntel intel) {

        if(intel.getOpponentCard().isEmpty()) {
            return null;
        }

        TrucoCard opponentCard = intel.getOpponentCard().get();
        TrucoCard lowestHandCard = null;
        for(int i = 0; i < intel.getCards().size(); i++){
            TrucoCard currentCard = intel.getCards().get(i);
            if(currentCard.relativeValue(intel.getVira()) > opponentCard.relativeValue(intel.getVira()) &&
                    (lowestHandCard == null || lowestHandCard.relativeValue(intel.getVira()) > currentCard.relativeValue(intel.getVira()))){
                lowestHandCard = currentCard;
            }
        }
        return lowestHandCard;
    }

    public TrucoCard getLowestCardInHand(GameIntel intel) {
        TrucoCard lowestCardsInHand = intel.getCards().get(0);
        for(int i = 0; i < intel.getCards().size(); i++) {
            TrucoCard currentCard = intel.getCards().get(i);
            if(currentCard.relativeValue(intel.getVira()) < lowestCardsInHand.relativeValue(intel.getVira())) {
                lowestCardsInHand = currentCard;
            }
        }
        return lowestCardsInHand;
    }

    public TrucoCard chooseCardSecondHand (GameIntel intel) {
        if(isPlayingFirst(intel)) return getLowestCardInHand(intel);
        TrucoCard cardToPlay = getLowestCardInHandThatBeatOpponentCard(intel);
        if(cardToPlay == null) cardToPlay = intel.getCards().get(0);
        return  cardToPlay;
    }

    public TrucoCard chooseCardThirdHand (GameIntel intel) {
        return intel.getCards().get(0);
    }

    public boolean handHasCardOverRelativeValue (GameIntel intel, int relativeValue) {
        for(int i = 0; i < intel.getCards().size(); i++) {
            TrucoCard currentCard = intel.getCards().get(i);
            if(currentCard.relativeValue(intel.getVira()) > relativeValue) {
                return true;
            }
        }
        return false;
    }
}
