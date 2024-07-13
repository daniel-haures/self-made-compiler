/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package valutatore;


import java.io.*;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;
import lexer.NumberTok;
import java.util.*;

import java.io.*;

public class Valutatore {

    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) {
                move();
            }
        } else {
            error("syntax error on match()");
        }
    }

    public void start() {
        if(look.tag=='(' || look.tag==Tag.NUM){
            int expr_val=expr();
            match(Tag.EOF);
            System.out.println(expr_val);
        }else{
            error("syntax error on start()");
        }
    }

    private int expr() {
        if(look.tag=='(' || look.tag==Tag.NUM){
            int term_val=term();
            int exprp_i=term_val;
            int exprp_val=exprp(exprp_i);
            int expr_val=exprp_val;
            return expr_val;
        }else{
            error("syntax error on expr()");
            return 0;
        }
    }

    private int exprp(int exprp_i) {
        int term_val,exprpp_val,exprpp_i,exprp_val;
        switch (look.tag) {
            case '+':
                match('+');
                term_val=term();
                exprpp_i=exprp_i+term_val;
                exprpp_val=exprp(exprpp_i);
                exprp_val=exprpp_val;
                return exprp_val;
            case '-':
                match('-');
                term_val=term();
                exprpp_i=exprp_i-term_val;
                exprpp_val=exprp(exprpp_i);
                exprp_val=exprpp_val;
                return exprp_val;
            case Tag.EOF,')':  
                exprp_val=exprp_i;
                return exprp_val;
            default:
                error("syntax error on exprp()");
                return 0;
        }
    }
    
    private int term() {
        if(look.tag=='(' || look.tag==Tag.NUM){
            int fact_val=fact();
            int termp_i=fact_val;
            int termp_val=termp(termp_i);
            int term_val=termp_val;
            return term_val;
        }else{
            error("syntax error on term()");
            return 0;
        }
    }
    


    private int termp(int termp_i) {
        int fact_val,termpp_i,termp_val,termpp_val;
        switch (look.tag) {
           case '*':
                match((int)'*');
                fact_val=fact();
                termpp_i=termp_i*fact_val;
                termpp_val=termp(termpp_i);
                termp_val=termpp_val;
                return termp_val;
            case '/':
                match((int)'/');
                fact_val=fact();
                termpp_i=termp_i/fact_val;
                termpp_val=termp(termpp_i);
                termp_val=termpp_val;
                return termp_val;
            case Tag.EOF,')','+','-':  
                termp_val=termp_i;
                return termp_val;
            default:
                error("syntax error on termp()");
                return 0;
        }
    }

    private int fact() {
        int expr_val,fact_val,num_value;
        switch (look.tag) {
            case '(':
                match((int)'(');
                expr_val=expr();
                match((int)')');
                fact_val=expr_val;
                return fact_val;
            case Tag.NUM:
                num_value=((NumberTok)look).lexeme;
                match(Tag.NUM);
                fact_val=num_value;
                return fact_val;
            default:
                error("syntax error on fact()");
                return 0;
        }
    }

    public static void main(String[] args) throws Exception {
        Lexer lex = new Lexer();
        if (args.length < 1) {
            throw new Exception("Usage: no file passed <file_name>");
        }
        String path=args[0];
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore parser = new Valutatore(lex, br);
            parser.start();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
