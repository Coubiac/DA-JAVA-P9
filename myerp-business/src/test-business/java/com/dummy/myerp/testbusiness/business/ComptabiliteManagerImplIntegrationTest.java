

package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;

import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;

import com.dummy.myerp.technical.exception.NotFoundException;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
        List<CompteComptable> compteComptables = manager.getListCompteComptable();
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new SimpleDateFormat( "dd/MM/yyyy" ).parse( "09/09/2020" ));
        vEcritureComptable.setLibelle("TestLibelle");
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture()
                .add(new LigneEcritureComptable(compteComptables.get(0),
                        null, new BigDecimal(123),
                        null));
        vEcritureComptable.getListLigneEcriture()
                .add(new LigneEcritureComptable(compteComptables.get(1),
                        null, null,
                        new BigDecimal(123)));
    }

    @Test
    public void checkEcritureComptableTest() throws FunctionalException {
        manager.checkEcritureComptable(vEcritureComptable);

    }

    @Test
    public void checkEcritureComptableExceptionTest() throws ParseException {
        List<CompteComptable> compteComptables = manager.getListCompteComptable();
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("BQ", "Banque"));
        vEcritureComptable.setDate(new SimpleDateFormat( "dd/MM/yyyy" ).parse( "27/12/2016" ));
        vEcritureComptable.setLibelle("TestLibelle");
        vEcritureComptable.setReference("BQ-2016/00005");
        vEcritureComptable.getListLigneEcriture()
                .add(new LigneEcritureComptable(compteComptables.get(0),
                        null, new BigDecimal(123),
                        null));
        vEcritureComptable.getListLigneEcriture()
                .add(new LigneEcritureComptable(compteComptables.get(1),
                        null, null,
                        new BigDecimal(123)));

        Throwable theException = assertThrows(FunctionalException.class, () ->
                manager.checkEcritureComptable(vEcritureComptable));

        vEcritureComptable.setId(null);
        Throwable theSecondException = assertThrows(FunctionalException.class, () ->
                manager.checkEcritureComptable(vEcritureComptable));

        assertThat(theException.getMessage())
               .isEqualTo("Une autre écriture comptable existe déjà avec la même référence.");
        assertThat(theSecondException.getMessage())
                .isEqualTo("Une autre écriture comptable existe déjà avec la même référence.");


    }

    @Test
    public void checkGetListEcritureComptableTest(){
        List<EcritureComptable> ecritureComptables = manager.getListEcritureComptable();
        assertThat(ecritureComptables).extracting("reference")
                .contains("AC-2016/00001",
                        "VE-2016/00002",
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
        vEcritureComptable.setReference(null); // On supprime la référence avant de tester l'ajout
        manager.addReference(vEcritureComptable);

        assertThat(vEcritureComptable.getReference()).isEqualTo("AC-2020/00001");


    }

    @Test
    public void checkInsertEcritureComptableTest() throws FunctionalException {
        manager.insertEcritureComptable(vEcritureComptable);
        List<EcritureComptable> ecritureComptables = manager.getListEcritureComptable();
        assertThat(ecritureComptables).extracting("libelle")
                .contains("TestLibelle");

    }

    @Test
    public void checkUpdateEcritureComptableTest() throws FunctionalException, NotFoundException {
        vEcritureComptable.setLibelle("TestLibelleNew");
        manager.addReference(vEcritureComptable);
        manager.updateEcritureComptable(vEcritureComptable);
        List<EcritureComptable> ecritureComptables = manager.getListEcritureComptable();
        assertThat(ecritureComptables).extracting("libelle")
                .contains("TestLibelleNew");
        assertThat(ecritureComptables).extracting("reference")
                .contains("AC-2020/00002");

    }

    @Test
    public void checkDeleteEcritureComptableTest() {
        manager.deleteEcritureComptable(-3);
        List<EcritureComptable> ecritureComptables = manager.getListEcritureComptable();
        assertThat(ecritureComptables).extracting("libelle")
                .doesNotContain("Paiement Facture F110001");
    }








}