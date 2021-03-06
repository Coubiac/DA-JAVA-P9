package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.*;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import javax.validation.ConstraintViolationException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class ComptabiliteManagerImplTest {

    private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();


    private EcritureComptable vEcritureComptable;


    @BeforeEach
    public void init() throws ParseException {
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new SimpleDateFormat( "dd/MM/yyyy" ).parse( "09/09/2020" ));
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");
    }

    @AfterEach
    public void end(){
        vEcritureComptable = null;
    }



    @Test
    @Tag("Gestion")
    public void checkEcritureComptableUnit() throws Exception {
        vEcritureComptable.getListLigneEcriture()
                .add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture()
                .add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    @Tag("Gestion")
    public void checkEcritureComptableUnitViolation() {
        vEcritureComptable = new EcritureComptable();
        Throwable theException = assertThrows(FunctionalException.class, () ->
                manager.checkEcritureComptableUnit(vEcritureComptable));
        assertThat(theException.getMessage())
                .isEqualTo("L'écriture comptable ne respecte pas les règles de gestion.");

        assertThat(theException).hasCauseInstanceOf(ConstraintViolationException.class);

        assertThat(theException.getCause().getMessage())
                .isEqualTo("L'écriture comptable ne respecte pas les contraintes de validation");
    }

    @Test
    @Tag("Gestion")
    @Tag("RG_Compta_2")
    public void checkEcritureComptableUnitRG2() throws Exception {
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(1234)));
        Throwable exceptionThatWasThrown = assertThrows(FunctionalException.class, () ->
                manager.checkEcritureComptableUnit(vEcritureComptable));
        assertThat(exceptionThatWasThrown.getMessage()).isEqualTo("L'écriture comptable n'est pas équilibrée.");
    }

    @Test
    @Tag("Gestion")
    @Tag("RG_Compta_3")
    public void checkEcritureComptableUnitRG3() {
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));

        Throwable exceptionThatWasThrown = assertThrows(FunctionalException.class, () ->
                manager.checkEcritureComptableUnit(vEcritureComptable));
        assertThat(exceptionThatWasThrown.getMessage()).isEqualTo("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
    }

    @Test
    @Tag("Gestion")
    @Tag("RG_Compta_5")
    public void checkEcritureComptableUnitRG5_yearReference() throws ParseException, FunctionalException {
        vEcritureComptable.setReference("AC-2021/00001"); // L'année ne correspond pas à la date

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        Throwable exceptionThatWasThrown = assertThrows(FunctionalException.class, () ->
                manager.checkEcritureComptableUnit(vEcritureComptable));
        assertThat(exceptionThatWasThrown.getMessage()).isEqualTo("L'année dans la référence ne correspond pas à la date de l'écriture");

    }

    @Test
    @Tag("Gestion")
    @Tag("RG_Compta_5")
    public void checkEcritureComptableUnitRG5_codeJournalReference() throws ParseException, FunctionalException {
         vEcritureComptable.setReference("BC-2020/00001"); //Le code journal ne correspond pas

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        Throwable exceptionThatWasThrown = assertThrows(FunctionalException.class, () ->
                manager.checkEcritureComptableUnit(vEcritureComptable));
        assertThat(exceptionThatWasThrown.getMessage()).isEqualTo("Le code dans la référence ne correspond pas au code du journal ");

    }

    public void checkEcritureComptable() throws FunctionalException {
        manager.checkEcritureComptable(vEcritureComptable);
    }

}
