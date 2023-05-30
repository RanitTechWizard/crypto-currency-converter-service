package com.crypto.convert.dto;

public class CoinGeckoResponse {
    private Bitcoin bitcoin;

    public Bitcoin getBitcoin() {
        return bitcoin;
    }

    public void setBitcoin(Bitcoin bitcoin) {
        this.bitcoin = bitcoin;
    }

    public static class Bitcoin {
        private double usd;
        private double localizedPrice; // New field for localized price

        public double getUsd() {
            return usd;
        }

        public void setUsd(double usd) {
            this.usd = usd;
        }

        public double getLocalizedPrice() {
            return localizedPrice;
        }

        public void setLocalizedPrice(double localizedPrice) {
            this.localizedPrice = localizedPrice;
        }
    }
}

