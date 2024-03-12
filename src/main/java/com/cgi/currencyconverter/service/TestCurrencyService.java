package com.cgi.currencyconverter.service;

import com.cgi.currencyconverter.controller.errors.CurrencyConverterErrorsEnum;
import com.cgi.currencyconverter.controller.errors.CurrencyQuoteNoteFound;
import com.cgi.currencyconverter.domain.currency.CurrencyEntity;
import com.cgi.currencyconverter.dto.currency.CurrencyDTO;
import com.cgi.currencyconverter.repository.CurrencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;

@Service
public class TestCurrencyService implements TestCurrencyImpl {

    @Value("${rapidapi.host}")
    private String rapidapiHost;

    @Value("${rapidapi.key}")
    private String rapidapiKey;

    // Create a RestTemplate for making HTTP requests
    private final RestTemplate restTemplate;

    private final CurrencyRepository currencyRepository;

    public TestCurrencyService(RestTemplateBuilder restTemplateBuilder, CurrencyRepository currencyRepository) {
        // Initialize the RestTemplate using RestTemplateBuilder
        this.restTemplate = restTemplateBuilder.build();
        this.currencyRepository = currencyRepository;
    }

    @Override
    public List<String> getCurrencyQuotesList() {
        try {
            HttpHeaders headers = createHeaders();
            HttpEntity entity = new HttpEntity<>(headers);

            // Make a GET request to the currency quotes API
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://currency-exchange.p.rapidapi.com/listquotes", // Where
                    HttpMethod.GET, // Method
                    entity, // Headers
                    String.class // Return
            );

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String[] currencies = objectMapper.readValue(response.getBody(), String[].class);
                return Arrays.asList(currencies);
            } else {
                // Handle unexpected response status codes
                System.out.println("Unexpected response status: " + response.getStatusCodeValue());
            }
        } catch (RestClientException | IOException e) {
            // Handle any RestClientExceptions (e.g., network issues)
            System.out.println("Error while fetching currency quotes: " + e.getMessage());
        }
        return Collections.emptyList(); // Return an empty list if there's an error or no response body
    }

    @Override
    public String getCurrencyExchange(String from, String to){
        try {
            HttpHeaders headers = createHeaders();
            HttpEntity entity = new HttpEntity<>(headers);

            // Make a GET request to the currency quotes API
            ResponseEntity<String> response = restTemplate.exchange(
                    changeCurrency(from, to),
                    HttpMethod.GET, // Method
                    entity, // Headers
                    String.class // Return
            );

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                return response.getBody();
            } else {
                // Handle unexpected response status codes
                System.out.println("Unexpected response status: " + response.getStatusCodeValue());
            }
        } catch (RestClientException e) {
            // Handle any RestClientExceptions (e.g., network issues)
            System.out.println("Error response status: " + e.getMessage());
        }
        return "";
    }

    private String changeCurrency(String from, String to){
        Map<String, String> params = new HashMap<>();
        params.put("from", from);
        params.put("to", to);

        String url = "https://currency-exchange.p.rapidapi.com/exchange";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        for(Map.Entry<String, String> entry : params.entrySet()){
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        return builder.toUriString();
    }

    @Override
    public String getCurrencyByCode(String code){
        try {

            CurrencyEntity detailedEntity = currencyRepository.getDetailedEntityByCode(code);
            return detailedEntity.toString();

        } catch (RestClientException e) {
            // Handle any RestClientExceptions (e.g., network issues)
            System.out.println("Error response status: " + e.getMessage());
        }
        return "";
    }

    @Override
    public String getCurrencyByName(String name){
        try {

            CurrencyEntity detailedEntity = currencyRepository.getDetailedEntityByName(name);
            return detailedEntity.toString();

        } catch (RestClientException e) {
            // Handle any RestClientExceptions (e.g., network issues)
            System.out.println("Error response status: " + e.getMessage());
        }
        return "";
    }

    @Override
    public Optional<CurrencyDTO> createCurrency(CurrencyDTO currencyDTO) {
        List<String> currencyQuotesList = getCurrencyQuotesList();

        if (!currencyQuotesList.contains(currencyDTO.getCode())) {
            throw new CurrencyQuoteNoteFound(CurrencyConverterErrorsEnum.ERR001_INVALID_QUOTE);
        } else {
            CurrencyEntity savedEntity = currencyRepository.saveAndFlush(new CurrencyEntity(currencyDTO));
            return Optional.of(new CurrencyDTO(savedEntity));
        }
    }

    // Create headers for the HTTP request
    public HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("x-rapidapi-key", rapidapiKey);
        headers.set("x-rapidapi-host", rapidapiHost);
        return headers;
    }

    @Override
    public String deleteCurrencyByCode(String code){
        try {

            currencyRepository.deleteEntityByCode(code);
            return "Data has been deleted from the DataBase";

        } catch (RestClientException e) {
            // Handle any RestClientExceptions (e.g., network issues)
            System.out.println("Error response status: " + e.getMessage());
        }
        return "";
    }

    @Override
    public String updateCurrencyByCode(String code, int newRate){
        try {

            currencyRepository.updateEntityByCode(code, newRate);
            return "Data has been updated from the DataBase";

        } catch (RestClientException e) {
            // Handle any RestClientExceptions (e.g., network issues)
            System.out.println("Error response status: " + e.getMessage());
        }
        return "";
    }
}
