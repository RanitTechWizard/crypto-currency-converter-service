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

        public double getUsd() {
            return usd;
        }

        public void setUsd(double usd) {
            this.usd = usd;
        }
    }
}

