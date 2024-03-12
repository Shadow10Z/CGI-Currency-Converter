package com.cgi.currencyconverter.service;

import com.cgi.currencyconverter.controller.errors.CurrencyQuoteNoteFound;
import com.cgi.currencyconverter.dto.currency.CurrencyDTO;

import java.util.List;
import java.util.Optional;

public interface TestCurrencyImpl {

    List<String> getCurrencyQuotesList();

    String getCurrencyExchange(String from, String to);

    String getCurrencyByCode(String code);

    String getCurrencyByName(String name);

    String deleteCurrencyByCode(String code);

    String updateCurrencyByCode(String code, int newRate);

    Optional<CurrencyDTO> createCurrency(CurrencyDTO currencyDTO) throws CurrencyQuoteNoteFound;
}
