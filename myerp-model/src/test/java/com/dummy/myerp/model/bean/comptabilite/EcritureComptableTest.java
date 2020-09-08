package com.dummy.myerp.model.bean.comptabilite;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


import org.apache.commons.lang3.ObjectUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class EcritureComptableTest {

    EcritureComptable classUnderTest;



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

    @BeforeEach
    public void initEcritureComptable(){
        classUnderTest = new EcritureComptable();
        classUnderTest.setId(1);
        classUnderTest.setLibelle("Une écriture comptable");
        classUnderTest.setReference("");
        classUnderTest.setJournal(new JournalComptable("BQ", "BANQUE"));


        Calendar myCalendar = new GregorianCalendar(2020, 8, 23);
        Date myDate = myCalendar.getTime();
        classUnderTest.setDate(myDate);

        classUnderTest.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        classUnderTest.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        classUnderTest.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        classUnderTest.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
    }

    @Test
    public void testWhen__DebitEqualsCredit_isEquilibree__ShouldReturnTrue() {
        classUnderTest = spy(classUnderTest);
        doReturn(new BigDecimal("100.0")).when(classUnderTest).getTotalCredit();
        doReturn(new BigDecimal("100.00")).when(classUnderTest).getTotalDebit();
        assertThat(classUnderTest.isEquilibree()).isTrue();

    }


    @Test
    public void When__DebitNotEqualsCredit_isEquilibree__ShouldReturnFalse() {
        classUnderTest = spy(classUnderTest);
        doReturn(new BigDecimal("100")).when(classUnderTest).getTotalCredit();
        doReturn(new BigDecimal("50.0")).when(classUnderTest).getTotalDebit();
        assertThat(classUnderTest.isEquilibree()).isFalse();

    }

    @Test
    public void When_AllCreditArePositiveValues_getTotalCredit_shouldReturnAnAdditionOfAllValues(){
        BigDecimal expectedResult = new BigDecimal(341);
        assertThat(classUnderTest.getTotalCredit().compareTo(expectedResult)).isEqualTo(0);
    }

    @Test
    public void When_aNegativeValueIsPresent_getTotalCredit_shouldReturnAnAdditionOfAllValues(){
        classUnderTest.getListLigneEcriture().add(this.createLigne(2, "-10", "7"));
        BigDecimal expectedResult = new BigDecimal(348);
        assertThat(classUnderTest.getTotalCredit().compareTo(expectedResult)).isEqualTo(0);
    }

    @Test
    public void When_AllCreditArePositiveValues_getTotalDebit_shouldReturnAnAdditionOfAllValues(){
        classUnderTest.getListLigneEcriture().add(this.createLigne(2, "10", "7"));
        BigDecimal expectedResult = new BigDecimal(351);
        assertThat(classUnderTest.getTotalDebit().compareTo(expectedResult)).isEqualTo(0);
    }

    @Test
    public void When_aNegativeValueIsPresent_getTotalDebit_shouldReturnAnAdditionOfAllValues(){
        classUnderTest.getListLigneEcriture().add(this.createLigne(2, "-7", "10"));
        BigDecimal expectedResult = new BigDecimal(334);
        assertThat(classUnderTest.getTotalDebit().compareTo(expectedResult)).isEqualTo(0);
    }

    @Test
    public void toString_ShouldReturn_ExpectedFormat(){
        String expectedString = "EcritureComptable{id=1, journal=JournalComptable{code='BQ', libelle='BANQUE'}, reference='', date=Wed Sep 23 00:00:00 CEST 2020, " +
                "libelle='Une écriture comptable', totalDebit=341.00, totalCredit=341, listLigneEcriture=[\n" +
                "LigneEcritureComptable{compteComptable=CompteComptable{numero=1, libelle='null'}, libelle='200.50', debit=200.50, credit=null}\n" +
                "LigneEcritureComptable{compteComptable=CompteComptable{numero=1, libelle='null'}, libelle='67.50', debit=100.50, credit=33}\n" +
                "LigneEcritureComptable{compteComptable=CompteComptable{numero=2, libelle='null'}, libelle='-301', debit=null, credit=301}\n" +
                "LigneEcritureComptable{compteComptable=CompteComptable{numero=2, libelle='null'}, libelle='33', debit=40, credit=7}\n]}";

        assertThat(expectedString).isEqualTo(classUnderTest.toString());

    }

    public void referenceRegex(String reference, boolean validates) throws NoSuchFieldException {
        Field field = EcritureComptable.class.getDeclaredField("reference");
        javax.validation.constraints.Pattern[] annotations = field.getAnnotationsByType(javax.validation.constraints.Pattern.class);
        Assertions.assertThat(reference.matches(annotations[0].regexp())).isEqualTo(validates);


    }

    @Test
    public void reference_asExpected() throws NoSuchFieldException {
        referenceRegex("BQ-2016/00001", true);
        referenceRegex("20-2016/00001", true);
        referenceRegex("20AZ-2016/00001", true);
        referenceRegex("20AZ1-2016/00001", true);
    }

    @Test
    public void reference_withTooShort_sequence() throws NoSuchFieldException {
        referenceRegex("BQ-2016/0001", false);
    }

    @Test
    public void reference_withTooLong_Prefix() throws NoSuchFieldException {
        referenceRegex("12ABCD-2016/00001", false);
    }

    @Test
    public void reference_withWrong_yearFormat() throws NoSuchFieldException {
        referenceRegex("BQ-16/00001", false);
    }

}
