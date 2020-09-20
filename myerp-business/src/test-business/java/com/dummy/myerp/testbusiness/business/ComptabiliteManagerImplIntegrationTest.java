

package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;

import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;

import com.dummy.myerp.technical.exception.NotFoundException;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class ComptabiliteManagerImplIntegrationTest extends BusinessTestCase {

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                vLibelle,
                vDebit, vCredit);
        return vRetour;
    }

    private EcritureComptable vEcritureComptable;
    private static ComptabiliteManager manager;

    @BeforeAll
    static void initAll(){
        manager = getBusinessProxy().getComptabiliteManager();
    }

    @BeforeEach
    public void init() throws ParseException {
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new SimpleDateFormat( "dd/MM/yyyy" ).parse( "09/09/2020" ));
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture()
                .add(new LigneEcritureComptable(new CompteComptable(1),
                        null, new BigDecimal(123),
                        null));
        vEcritureComptable.getListLigneEcriture()
                .add(new LigneEcritureComptable(new CompteComptable(2),
                        null, null,
                        new BigDecimal(123)));
    }

    @Test
    public void checkEcritureComptableTest() throws FunctionalException {
        manager.checkEcritureComptable(vEcritureComptable);
    }

    @Test
    public void checkGetListEcritureComptableTest(){
        List<EcritureComptable> ecritureComptables = manager.getListEcritureComptable();
        assertThat(ecritureComptables).extracting("reference")
                .contains("AC-2016/00001",
                        "VE-2016/00002",
                        "BQ-2016/00003",
                        "VE-2016/00004",
                        "BQ-2016/00005");

    }

    @Test
    public void checkGetListCompteComptableTest(){
        List<CompteComptable>compteComptables = manager.getListCompteComptable();
        assertThat(compteComptables).extracting("libelle")
                .contains("Fournisseurs",
                        "Clients",
                        "Taxes sur le chiffre d'affaires déductibles",
                        "Taxes sur le chiffre d'affaires collectées par l'entreprise",
                        "Banque",
                        "Achats non stockés de matières et fournitures",
                        "Prestations de services");

    }

    @Test
    public void checkListJournalComptableTest(){
        List<JournalComptable> journalComptables = manager.getListJournalComptable();
        assertThat(journalComptables).extracting("libelle")
                .contains("Achat",
                        "Vente",
                        "Banque",
                        "Opérations Diverses");
    }


    @Test
    public void checkAddReferenceTest() throws NotFoundException {
        manager.addReference(vEcritureComptable);

        assertThat(vEcritureComptable.getReference()).isEqualTo("AC-2020/00001");


    }




}