package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JournalComptableTest {

    List<JournalComptable> plist;

    JournalComptable classUnderTest;


    @BeforeEach
    public void init(){

        this.classUnderTest = new JournalComptable("800100", "journal0");
        this.plist = new ArrayList<>();
        plist.add(new JournalComptable("800101","journal1"));
        plist.add(new JournalComptable("800102","journal2"));
        plist.add(new JournalComptable("800103","journal3"));
        plist.add(new JournalComptable("800104","journal4"));
        plist.add(new JournalComptable("800105","journal5"));
    }


    @Test
    public void getByCode_shouldReturn_theExpectedJournalComptable(){

        String expectedCode = "800104";
        String expectedLibelle = "journal4";
        List<JournalComptable> emptyList = new ArrayList<>();

        JournalComptable nonPresentJournal = JournalComptable.getByCode(plist, "800106");
        JournalComptable theJournal = JournalComptable.getByCode(plist, expectedCode);
        JournalComptable theJournalInEmptyList = JournalComptable.getByCode(emptyList, expectedCode);


        assertThat(expectedLibelle).isEqualTo(theJournal.getLibelle());
        assertThat(expectedCode).isEqualTo(theJournal.getCode());
        assertThat(nonPresentJournal).isNull();
        assertThat(theJournalInEmptyList).isNull();

    }

    @Test
    public void toString_shouldReturn_theExpectedResult(){
        String expectedResult = "JournalComptable{code='800100', libelle='journal0'}";
        assertThat(expectedResult).isEqualTo(classUnderTest.toString());
    }

}
