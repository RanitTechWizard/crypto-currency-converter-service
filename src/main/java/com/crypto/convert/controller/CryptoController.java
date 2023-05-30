package com.crypto.convert.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Currency;
import java.text.NumberFormat;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import com.crypto.convert.dto.CoinGeckoResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cryptocurrencies")
public class CryptoController {

    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3";

    // Map locales to corresponding vs_currencies
    private static final Map<String, String> localeCurrencyMap = new HashMap<>();

    static {
        localeCurrencyMap.put("en_US", "usd");
        localeCurrencyMap.put("de_DE", "eur");

        // Add more locale to vs_currency mappings as needed
    }

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
    public ResponseEntity<CoinGeckoResponse> getCryptocurrencyPrice(@PathVariable("symbol") String symbol,
                                                                    @RequestHeader(value = "X-Forwarded-For", required = false) String userIpAddress,
                                                                    HttpServletRequest request) {

        // Use user-provided IP if available, else use request IP
        String ipAddress = (userIpAddress != null) ? userIpAddress : request.getRemoteAddr();

        // Determine the locale from the IP address
        Locale locale = determineLocaleFromIpAddress(ipAddress);

        // Determine the vs_currency based on the locale
        String vsCurrency = determineVsCurrencyFromLocale(locale);


        // Make a request to the CoinGecko API
        String apiUrl = COINGECKO_API_URL + "/simple/price?ids=" + symbol + "&vs_currencies=" + vsCurrency;
        RestTemplate restTemplate = new RestTemplate();
        CoinGeckoResponse response = restTemplate.getForObject(apiUrl, CoinGeckoResponse.class);

        // Customize the response with the localized price
        customizeResponseWithLocalizedPrice(response, locale);

        // Return the response
        return ResponseEntity.ok(response);
    }

    private String determineVsCurrencyFromLocale(Locale locale) {
        // Get the corresponding vs_currency based on the locale from the localeCurrencyMap
        String vsCurrency = localeCurrencyMap.get(locale.toString());
        if (vsCurrency == null) {
            // If no mapping found, fallback to a default vs_currency
            vsCurrency = "usd";
        }
        return vsCurrency;
    }

    private Locale determineLocaleFromIpAddress(String ipAddress) {
        // Implement your logic to determine the locale from the IP address
        // You can use a third-party library or service to perform IP-to-locale mapping
        // For this example, we assume the locale is determined successfully
        // and return a default locale
        return Locale.getDefault();
    }

    private void customizeResponseWithLocalizedPrice(CoinGeckoResponse response, Locale locale) {
        // Implement your logic to customize the response with the localized price
        // You can use appropriate number formatting or currency formatting based on the locale
        // For this example, we'll just use a dummy localized price

        // Assume the localized price is fetched based on the locale
        double localizedPrice = getLocalizedPrice(response.getBitcoin().getUsd(), locale);
        System.out.println("localized price =" + localizedPrice);
        // Set the localized price in the response
        response.getBitcoin().setLocalizedPrice(localizedPrice);
    }

    private double getLocalizedPrice(double price, Locale locale) {
        // Create a NumberFormat instance with the given locale
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        // Set the currency code to USD for simplicity (you can customize this as needed)
        Currency usdCurrency = Currency.getInstance("USD");
        numberFormat.setCurrency(usdCurrency);

        // Format the price with currency symbol and correct decimal separator
        String formattedPrice = numberFormat.format(price);

        // Parse the formatted price back to a double (to remove any grouping separators, currency symbols, etc.)
        double localizedPrice;
        try {
            Number parsedNumber = numberFormat.parse(formattedPrice);
            localizedPrice = parsedNumber.doubleValue();
        } catch (Exception e) {
            // Handle any parsing exceptions here
            localizedPrice = price; // Fallback to original price if parsing fails
        }

        return localizedPrice;
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
