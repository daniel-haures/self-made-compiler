/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

/**
 *
 * @author Daniel
 */

/*
  guide(start > exprEOF)= First(exprEOF)=first(expr)={(,NUM}
  guide(expr > term exprp)= first(term)= {(,NUM}
  guide(exprp > +....)= {+}
  guide(exprp > -.....)={-}
  guide(exprp > eps)= follow(exprp)={')',EOF}
  guide(term > fact termp)= ('(',NUM)
  guide(termp > *....)={*}
  guide(termp > /....)={/}
  guide(termp > eps)=Follow(termp)={'+','-',')',EOF}
  guide(fact > (....  )={'('}
  guide(fact > NUM )={NUM}
  */
import java.io.*;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;
import java.util.*;

import java.io.*;

public class Parser {

    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
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
            error("syntax error");
        }
    }

    public void start() {
        if(look.tag=='(' || look.tag==Tag.NUM){
            expr();
            if ((look.tag != Tag.EOF)) error("grammar error in method start() after" + look+" expected: EOF");
            match(Tag.EOF);
        }else{
            error("syntax error in method start() after "+look+" expected: (");
        }
    }

    private void expr() {
        if(look.tag=='(' || look.tag==Tag.NUM){
            term();
            exprp();
        }else{
            error("syntax error in method expr() after "+look+" expected: (");
        }
    }

    private void exprp() {
        switch (look.tag) {
            case '+':
                match((int)'+');
                term();
                exprp();
                break;
            case '-':
                match((int)'-');
                term();
                exprp();
                break;
            case Tag.EOF,')':  
                break;
            default:
                error("syntax error in method exprp() after "+look+" expected: + - )");
        }
    }
    
    private void term() {
        if(look.tag=='(' || look.tag==Tag.NUM){
            fact();
            termp();
        }else{
            error("syntax error in method term() after "+look+" expected: (");
        }
    }


    private void termp() {
        switch (look.tag) {
            case '*':
                match((int)'*');
                fact();
                termp();
                break;
            case '/':
                match((int)'/');
                fact();
                termp();
                break;
            case Tag.EOF,')','+','-':  
                break;
            default:
                error("syntax error in method termp() after "+look+" expected: * / ) + -");
        }
    }

    private void fact() {
        switch (look.tag) {
            case '(':
                match((int)'(');
                expr();
                if ((look.tag != ')')) error("Error in grammar fact after" + look+" expected: )");
                match((int)')');
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            default:
                error("syntax error in method fact() after "+look+" expected: NUM (");
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
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}