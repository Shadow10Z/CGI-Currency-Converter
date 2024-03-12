package com.cgi.currencyconverter.repository;

import com.cgi.currencyconverter.domain.currency.CurrencyEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@Transactional
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {

    @Query("SELECT C FROM CurrencyEntity C WHERE C.code = :code")
    CurrencyEntity getDetailedEntityByCode(@Param("code") String code);

    @Query("SELECT C FROM CurrencyEntity C WHERE C.name = :name")
    CurrencyEntity getDetailedEntityByName(@Param("name") String name);

    @Modifying
    @Query("DELETE FROM CurrencyEntity C WHERE C.code = :code")
    void deleteEntityByCode(@Param("code") String code);

    @Modifying
    @Query("UPDATE CurrencyEntity C SET C.rate = :newRate WHERE C.code = :code")
    void updateEntityByCode(@Param("code") String code, @RequestBody int newRate);
}
