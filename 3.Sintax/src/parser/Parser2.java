/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

/**
 * @author Daniel
 * 
 * guide(<prog>) >> {=,print,read,cond,'{'}
 * guide(<statlist>) >> {=,print,read,cond,'{'}
 * guide(<statlistp>) >> {;}
 * guide(<statlistp>->eps) >> {EOF,'}'}
 * guide(<stat>->=ID) >> {=}  
 * guide(<stat>->print(<exprlist>)) >> {print}
 * guide(<stat>->read(ID)) >> {read}
 * guide(<stat>->cond <whenlist> else <stat>) >> {cond}
 * guide(<stat>->while(<bexpr>)<stat>) >> {while}
 * guide(<stat>->{<statlist>}) >> {'{'}
 * guide(<whenlist>)  >> {when}
 * guide(<whenitem>)  >> {when}
 * guide(<whenlistp>-><whenitem><whentlistp>)  >> {when}
 * guide(<whenlistp>->eps)  >> {else}
 * guide(<bexpr>->RELOP<expr><expr>)  >> {RELOP}
 * guide(<expr>->+(<exprlist>))  >> {+}
 * guide(<expr>->*(<exprlist>))  >> {*}
 * guide(<expr>->-<expr><expr>)  >> {-}
 * guide(<expr>->/<expr><expr>)  >> {/}
 * guide(<expr>->NUM)  >> {NUM}
 * guide(<expr>->ID)  >> {ID}
 * guide(<exprlist>-><expr><exprlist>)  >> {+,*,-,/,NUM,ID}
 * guide(<exprlistp>-><expr><exprlistp>)  >> {+,*,-,/,NUM,ID}
 * guide(<exprlistp>->eps)  >> {')'}
 */


import java.io.*;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;
import lexer.Word;
import java.util.*;

import java.io.*;

public class Parser2 {

    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser2(Lexer l, BufferedReader br) {
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
            error("syntax error in method match()");
        }
    }

    public void prog(){
        if(look.tag=='=' || look.tag==Tag.PRINT || look.tag==Tag.READ || look.tag==Tag.COND
                || look.tag==Tag.WHILE || look.tag=='{'){
            statlist();
            if ((look.tag != Tag.EOF)) error("grammar error in method prog() after" + look+" expected: EOF");
            match(Tag.EOF);
        }else{
            error("syntax error in method prog()");
        }
    }
    
    public void statlist(){
        if(look.tag=='=' || look.tag==Tag.PRINT || look.tag==Tag.READ || look.tag==Tag.COND
                || look.tag==Tag.WHILE || look.tag=='{'){
            stat();
            statlistp();
        }else{
            error("syntax error in method statlist()");
        }
    }
    
    public void statlistp(){
        switch(look.tag){
            case ';':
                match(';');
                stat();
                statlistp();
                break;
            case '}',Tag.EOF:
                break;
            default:
                error("syntax error in method statlistp()");
        }
    }
    
    public void stat(){
        switch(look.tag){
            case '=':
                match('=');
                if ((look.tag != Tag.ID)) error("grammar error in method stat()->asign after" + look+" expected: ID");
                match(Tag.ID);
                expr();
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                if ((look.tag != '(')) error("grammar error in method stat()->print after" + look+" expected: (");
                match('(');
                exprlist();
                if ((look.tag != ')')) error("grammar error in method stat()->print after" + look+" expected: )");
                match(')');
                break;
            case Tag.READ:
                match(Tag.READ);
                if ((look.tag != '(')) error("grammar error in method stat()->read after" + look+" expected: (");
                match('(');
                if ((look.tag != Tag.ID)) error("grammar error in method stat()->read after" + look+" expected: ID");
                match(Tag.ID);
                if ((look.tag != ')')) error("grammar error in method stat()->read after" + look+" expected: )");
                match(')');
                break;
            case Tag.COND:
                match(Tag.COND);
                whenlist();
                if ((look.tag != Tag.ELSE)) error("grammar error in method stat()->cond after" + look+" expected: else)");
                match(Tag.ELSE);
                stat();
                break;
            case Tag.WHILE:
                match(Tag.WHILE);
                if ((look.tag != '(')) error("grammar error in method stat()->while after" + look+" expected: (");
                match('(');
                bexpr();
                if ((look.tag != ')')) error("grammar error in method stat()->while after" + look+" expected: )");
                match(')');
                stat();
                break;
            case '{':
                match('{');
                statlist();
                if ((look.tag != '}')) error("grammar error in method stat()->{statlist} after" + look+" expected: }");
                match('}');
                break;
            default:
                error("syntax error in method stat() after "+look+" expected: { while cond read print = ");
                
        }
    }
    
    public void whenlist(){
        if(look.tag==Tag.WHEN){
            whenitem();
            whenlistp();
        }else{
            error("syntax error in method whenlist() after "+look+" expected: when");
        }
    }
    
    public void whenlistp(){
         switch(look.tag){
            case Tag.WHEN:
                whenitem();
                whenlistp();
                break;
            case Tag.ELSE:
                break;
            default:
                error("syntax error in method whenlistp() after "+look+" expected: when else");
                
        }
    }
   
    public void whenitem(){
        if(look.tag==Tag.WHEN){
            match(Tag.WHEN);
            if ((look.tag != '(')) error("grammar error in method stat()->{statlist} after" + look+" expected: (");
            match('(');
            bexpr();
            if ((look.tag != ')')) error("grammar error in method stat()->{statlist} after" + look+" expected: )");
            match(')');
            if ((look.tag != Tag.DO)) error("grammar error in method stat()->{statlist} after" + look+" expected: do");
            match(Tag.DO);
            stat();
        }else{
            error("syntax error in method whenitem() after "+look+" expected: when");
        }
    }
    
    public void bexpr(){
        if(look.tag==Tag.RELOP){
            match(Tag.RELOP);
            expr();
            expr();
        }else{
            error("syntax error in method bexpr() after "+look+" expected: a relational symbol");
        }
    }
    
    public void expr(){
        switch(look.tag){
            case '+':
                match('+');
                if ((look.tag != '(')) error("grammar error in method expr()->sum after" + look+" expected: (");
                match('(');
                exprlist();
                if ((look.tag != ')')) error("grammar error in method expr()->sum after" + look+" expected: )");
                match(')');
                break;
             case '-':
                match('-');
                expr();
                expr();
                break;
            case '*':
                match('*');
                if ((look.tag != '(')) error("grammar error in method expr()->mult after" + look+" expected: (");
                match('(');
                exprlist();
                if ((look.tag != ')')) error("grammar error in method expr()->mult after" + look+" expected: )");
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                break; 
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                match(Tag.ID);
                break;
            default:
                error("syntax error in method expr() after "+look+" expected: + / * - NUM ID");
                
        }
    }
    
    public void exprlist(){
        if(look.tag=='+' || look.tag=='-' || look.tag=='*' || look.tag=='/'
                || look.tag==Tag.NUM || look.tag==Tag.ID){
            expr();
            exprlistp();
        }else{
            error("syntax error in method exprlist() after "+look+" expected: + / * - NUM ID");
        }
    }
    
    public void exprlistp(){
        switch (look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            case ')':
                break;
            default:
                error("syntax error in method exprlistp() after "+look+" expected: + / * - NUM ID");
                break;
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
            Parser2 parser = new Parser2(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

