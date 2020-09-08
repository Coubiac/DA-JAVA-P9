package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CompteComptableTest {

    List<CompteComptable> plist;

    CompteComptable classUnderTest;


    @BeforeEach
    public void init(){

        this.classUnderTest = new CompteComptable(800100, "compte0");
        this.plist = new ArrayList<>();
        plist.add(new CompteComptable(800101,"compte1"));
        plist.add(new CompteComptable(800102,"compte2"));
        plist.add(new CompteComptable(800103,"compte3"));
        plist.add(new CompteComptable(800104,"compte4"));
        plist.add(new CompteComptable(800105,"compte5"));

        System.out.println(this.classUnderTest.toString());
    }


    @Test
    public void getByCode_shouldReturn_theExpectedCompteComptable(){

        Integer expectedCode = 800104;
        String expectedLibelle = "compte4";
        List<CompteComptable> emptyList = new ArrayList<>();

        CompteComptable nonPresentCompte = CompteComptable.getByNumero(plist, 800106);
        CompteComptable theCompte = CompteComptable.getByNumero(plist, expectedCode);
        CompteComptable theCompteInEmptyList = CompteComptable.getByNumero(emptyList, expectedCode);


        assertThat(expectedLibelle).isEqualTo(theCompte.getLibelle());
        assertThat(expectedCode).isEqualTo(theCompte.getNumero());
        assertThat(nonPresentCompte).isNull();
        assertThat(theCompteInEmptyList).isNull();

    }

    @Test
    public void toString_shouldReturn_theExpectedResult(){
        String expectedResult = "CompteComptable{numero=800100, libelle='compte0'}";
        assertThat(expectedResult).isEqualTo(classUnderTest.toString());
    }

}
