package com.crypto.convert.controller;

import org.springframework.http.ResponseEntity;
import com.crypto.convert.dto.CoinGeckoResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cryptocurrencies")
public class CryptoController {

    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3";

    @GetMapping("/list")
    public ResponseEntity<?> getCryptocurrencies() {
        String url = COINGECKO_API_URL + "/coins/list";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Crypto[]> response = restTemplate.getForEntity(url, Crypto[].class);

        Crypto[] cryptocurrencies = response.getBody();
        if (cryptocurrencies == null || cryptocurrencies.length == 0) {
            return ResponseEntity.noContent().build();
        }

        String[] cryptoNames = new String[cryptocurrencies.length];
        for (int i = 0; i < cryptocurrencies.length; i++) {
            cryptoNames[i] = cryptocurrencies[i].getName();
        }

        return ResponseEntity.ok(cryptoNames);
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<CoinGeckoResponse> getCryptocurrencyPrice(@PathVariable("symbol") String symbol) {
        // Make a request to the CoinGecko API

        String apiUrl = COINGECKO_API_URL + "/simple/price?ids=" + symbol + "&vs_currencies=usd";

        RestTemplate restTemplate = new RestTemplate();
        CoinGeckoResponse response = restTemplate.getForObject(apiUrl, CoinGeckoResponse.class);

        // Return the response
        return ResponseEntity.ok(response);
    }

    private static class Crypto {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
