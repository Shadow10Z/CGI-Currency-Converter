package com.cgi.currencyconverter.controller;

import com.cgi.currencyconverter.controller.errors.BadRequestAlertException;
import com.cgi.currencyconverter.controller.errors.CurrencyQuoteNoteFound;
import jakarta.validation.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import com.cgi.currencyconverter.dto.currency.CurrencyDTO;
import com.cgi.currencyconverter.service.CurrencyConverterService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency-converter")
public class CurrencyConverterController {
    private final Logger log = LoggerFactory.getLogger(CurrencyConverterController.class);

    private static final String ENTITY_NAME = "currency";

    private final CurrencyConverterService currencyConverterService;

    public CurrencyConverterController(CurrencyConverterService currencyConverterService) {
        this.currencyConverterService = currencyConverterService;
    }

    @GetMapping("/currency-quotes")
    public String getCurrencyQuotes() {
        return currencyConverterService.getCurrencyQuotesList().toString();
    }

    @GetMapping("/exchange-rate")
    public String getCurrencyExchange(@Param("from") String from, @Param("to") String to){
        return currencyConverterService.getCurrencyExchange(from, to).toString();
    }

    @GetMapping("/currency-code")
    public String getCurrencyByCode(@Param("code") String code){
        return currencyConverterService.getCurrencyByCode(code).toString();
    }

    @GetMapping("/currency-name")
    public String getCurrencyByName(@Param("name") String name){
        return currencyConverterService.getCurrencyByName(name).toString();
    }

    @DeleteMapping("/currency-delete/{code}")
    public String deleteCurrencyByCode(@PathVariable("code") String code){
        return currencyConverterService.deleteCurrencyByCode(code).toString();
    }

    @PutMapping("/currency-update/{code}")
    public String updateCurrencyByCode(
            @PathVariable("code") String code,
            @RequestBody int newRate
    ){
        return currencyConverterService.updateCurrencyByCode(code, newRate).toString();
    }

    @PostMapping("/save")
    public ResponseEntity createCurrency(@Valid @RequestBody CurrencyDTO currencyDTO) {

        try {
            if (!currencyDTO.isNew()) {
                throw new BadRequestAlertException("A new Currency cannot already have an ID", ENTITY_NAME, "idexists");
            }

            Optional<CurrencyDTO> createdCurrency = currencyConverterService.createCurrency(currencyDTO);

            if (createdCurrency.isPresent()) {
                CurrencyDTO currency = createdCurrency.get();
                return ResponseEntity.created(new URI("/api/client/" + currency.getId())).body(currency);
            }

        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (CurrencyQuoteNoteFound e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return ResponseEntity.internalServerError().build();
    }
}
