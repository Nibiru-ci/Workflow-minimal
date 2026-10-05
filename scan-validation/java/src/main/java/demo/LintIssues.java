// ECHANTILLON LINT UNIQUEMENT (aucune faille de sécurité) : sert à vérifier que Codacy remonte les problèmes de style/qualité.
package demo;

import java.util.*;
import java.io.*;

class lint_issues {
    public int Counter;
    private String unused;

    public void DoSomething(int a){
        if(a==1) System.out.println("one");
        try { Integer.parseInt("x"); } catch(Exception e) {}
        int magic = 42 * 17;
    }
}
