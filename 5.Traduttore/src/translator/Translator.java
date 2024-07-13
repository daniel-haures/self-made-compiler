/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package translator;

/**
 *
 * @author Daniel
 */
import java.io.*;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;
import lexer.Word;
import lexer.NumberTok;
import java.util.*;

import java.io.*;

/**
* Attributi:
* expertlist_val/expertlistp_val= numero di elementi nell'espressione.
* whenlist_noelse/whenlistp_noelse/whenitem_noelse(ereditato)= label a cui saltare 
* se una delle condizioni di whenlist è vera e non si vuole eseguire l'else.
* whenitem_next(ereditato)= label posto alla successiva condizione when a cui il
* programma salta se una codizione when è falsa.
* bexpr_false= label a cui saltare se <bexpr> è falso;
* 
* 
* C-> coda delle istruzioni; emit(x) aggiunge istruzioni x alla coda
*<prog> -> <statlist>EOF {stampa coda}
* <statlist> -> <stat> <statlistp> 
* <statlistp> -> ; <stat> <statlistp>
*                | eps    
* <stat> -> =ID <expr> {emit(istore ID)}
*        |  print (<exprlist>) {for(|exprlist_val|-1) emit(print)}
*        |  read (ID) {emit(read);emit(istore ID)}
*        |  cond {whenlist_noelse=no_else_label} <whenlist> else <stat> {emit(no_else_label: )}
*        |  while {emit(reloop_label:)} (<bexpr>) <stat> {emit(goto reloop_label)} {emit(exit_label:)} 
*        |  {<statlist>}  
* <whenlist> ->  {whenitem_noelse=whenlist_noelse;whenitem_next=no_do_label}<whenitem>{emit(no_do_label: )}{whenlistp_noelse=whenlist_noelse}<whenlistp>
* <whenlistp> -> {whenitem_noelse=whenlistp_noelse;whenitem_next=no_do_label}<whenitem>{emit(no_do_label: )}{whenlistpp_noelse=whenlistp_noelse}<whenlistp>
*             |  eps
* <whenitem> -> when {bexpr_false=whenitem_next}(<bexpr>) do <stat> emit(goto whenitem_no_else)
* <bexpr> -> RELOP <expr> <expr> {emit(if.., bexpr_false) con if=condizione di controllo su <expr><expr> inversa a RELOP}
* <expr>  -> + (<exprlist>) {for(|exprlist_val|-1) emit(iadd)}
*         |  - <expr> <expr> {emit(isub)}
*         |  * (<exprlist>) {for(|exprlist_val|-1) emit(imul)}
*         |  / <expr> <expr> {emit(idiv)}
*         |  NUM {emit(ldc NUM)}
*         |  ID {emit (iload ID)}
* <exprlist> -> <expr><exprlistp> {exprlist_val=1 +exprlistp_val}
* <exprlistp> -> <expr><exprlistp> {exprlistp_val=1 +exprlistpp_val}
*             |  eps  {exprlistp_val=0}
*/

public class Translator {

    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;

    public Translator(Lexer l, BufferedReader br) {
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

    public void prog() {
        if (look.tag == '=' || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.COND
                || look.tag == Tag.WHILE || look.tag == '{') {
            statlist();
            if ((look.tag != Tag.EOF)) error("Error in grammar prog after" + look+" expected: EOF");
            match(Tag.EOF);
            try {
                code.toJasmin();
            } catch (java.io.IOException e) {
                System.out.println("IO error\n");
            };
        } else {
            error("Error in grammar after calling statlist on " + look+" expected: { while cond read print =");
            
        }
    }

    public void statlist() {
        if (look.tag == '=' || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.COND
                || look.tag == Tag.WHILE || look.tag == '{') {
            stat();
            statlistp();
        } else {
            error("Error in grammar after calling statlist on " + look+" expected: { while cond read print =");
        }
    }

    public void statlistp() {
        switch (look.tag) {
            case ';':
                match(';');
                stat();
                statlistp();
                break;
            case '}',Tag.EOF:
                break;
            default:
                error("Error in grammar after calling statlistp on " + look+" expected: ; }");
        }
    }

    public void stat() {
        switch (look.tag) {
            case '=':
                match('=');
                if (look.tag == Tag.ID) {
                    int id_addr = st.lookupAddress(((Word) look).lexeme);
                    if (id_addr == -1) {
                        id_addr = count;
                        st.insert(((Word) look).lexeme, count++);
                    }
                    match(Tag.ID);
                    expr();
                    code.emit(OpCode.istore, id_addr);
                }else error("Error in grammar (stat)->Assign after" + look+" expected: ID");
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                if ((look.tag != '(')) error("Error in grammar (stat)->Print after" + look+" expected: (");
                match('(');
                int exprlist_val=exprlist();
                for(int i=0;i<exprlist_val;i++){
                    code.emit(OpCode.invokestatic, 1);
                }
                if ((look.tag != ')')) error("Error in grammar (stat)->Print after" + look+" expected: )");
                match(')');
                break;
            case Tag.READ:
                /*Controllo della grammatica*/
                match(Tag.READ);
                if ((look.tag != '(')) error("Error in grammar (stat)->Read after" + look+" expected: (");
                match('(');
                if ((look.tag != Tag.ID)) error("Error in grammar (stat)->Read after" + look+" expected a identifier");
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    id_addr = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(Tag.ID);
                if ((look.tag != ')')) error("Error in grammar (stat)->Cond after" + look+" expected: )");
                match(')');
                /*Creazione codice per read*/
                code.emit(OpCode.invokestatic, 0);
                code.emit(OpCode.istore, id_addr);
                break;
            case Tag.COND:
                /*Creazione ettichette necessarie*/
                int whenlist_noelse=code.newLabel();
                match(Tag.COND);
                whenlist(whenlist_noelse);
                if ((look.tag != Tag.ELSE)) error("Error in grammar (stat)->Cond after" + look+" expected: else");
                match(Tag.ELSE);
                stat();
                /*Label a cui saltare se almeno una condizione di whenlist è vera*/
                code.emitLabel(whenlist_noelse);
                break;
            case Tag.WHILE:
                /*Creazione ettichette necessarie*/
                int reloop=code.newLabel();
                int exitloop=code.newLabel();
                match(Tag.WHILE);
                if ((look.tag != '(')) error("Error in grammar (stat)->While after while with " + look+" expected: (");
                match('(');
                /*Punto in cui ricominciare il while*/
                code.emitLabel(reloop);
                bexpr(exitloop);
                if ((look.tag != ')')) error("Error in grammar (stat)->While after while(bexpr with " + look+" expected: )");
                match(')');
                stat();
                code.emit(OpCode.GOto, reloop);
                /*Punto a cai saltere se la condizione di controllo è falsa*/
                code.emitLabel(exitloop);
                break;
            case '{':
                match('{');
                statlist();
                if ((look.tag != '}')) error("Error in grammar (stat)->{statlist} after {statlist with " + look +" expected: }");
                match('}');
                break;
            default:
                error("Error in grammar after calling stat on " + look+" expected: { while cond read print = ");
                
        }
    }

    public void whenlist(int whenlist_noelse) {
        if (look.tag == Tag.WHEN) {
            int whenitem_next=code.newLabel();
            whenitem(whenitem_next,whenlist_noelse);
            /*Punto in cui andare se non eseguo il do del when item*/
            code.emitLabel(whenitem_next);
            whenlistp(whenlist_noelse);
        } else {
            error("Error in grammar after whenlist calling on " + look+" expectet: when");
        }
    }

    public void whenlistp(int whenlistp_noelse) {
        switch (look.tag) {
            case Tag.WHEN:
                int whenitem_next=code.newLabel();
                whenitem(whenitem_next,whenlistp_noelse);
                /*Punto in cui andare se non eseguo il do del when item*/
                code.emitLabel(whenitem_next);
                whenlistp(whenlistp_noelse);
                break;
            case Tag.ELSE:
                break;
            default:
                error("Error in grammar after whenlistp calling on " + look+" expectet: when or else");

        }
    }

    public void whenitem(int whenitem_next,int whenlist_noelse) {
        if (look.tag == Tag.WHEN) {
            match(Tag.WHEN);
            if ((look.tag != '(')) error("Error in grammar (whenitem) after when with " + look+" expected: (");
            match('(');
            bexpr(whenitem_next);
            if ((look.tag != ')')) error("Error in grammar (whenitem) after when(bexpr with " + look+" expected: )");
            match(')');
            if ((look.tag != Tag.DO)) error("Error in grammar (whenitem) after when(bexpr) with " + look+" expected: do");
            match(Tag.DO);
            stat();
            code.emit(OpCode.GOto, whenlist_noelse);
        } else {
            error("Error in grammar after whenitem caling on" + look+" expected:when");
        }
    }

    public void bexpr(int bexper_false) {
        if (look.tag == Tag.RELOP) {
            Word relop=(Word)look;
            match(Tag.RELOP);
            expr();
            expr();
            switch(relop.lexeme){
                case "<=":
                    code.emit(OpCode.if_icmpgt,bexper_false);
                    break;
                case ">=":
                    code.emit(OpCode.if_icmplt,bexper_false);
                    break;
                case ">":
                    code.emit(OpCode.if_icmple,bexper_false);
                    break;
                case "<":
                    code.emit(OpCode.if_icmpge,bexper_false);
                    break;
                case "==":
                    code.emit(OpCode.if_icmpne,bexper_false);
                    break;
                case "<>":
                    code.emit(OpCode.if_icmpeq,bexper_false);
                    break;
            }
            
        } else {
            error("Error in grammar after calling bexpr with " + look+" expected a relational symbol, assert while() or when() have correct conditions");
        }
    }

    public void expr() {
        int exprlist_val;
        switch (look.tag) {
            case '+':
                match('+');
                if ((look.tag != '(')) error("Error in grammar (expr)->sum after " + look+" expected: (");
                match('(');
                exprlist_val=exprlist();
                for(int i=0;i<exprlist_val-1;i++){
                    code.emit(OpCode.iadd);
                }
                if ((look.tag != ')')) error("Error in grammar (expr)->sum after" + look+" expected: )");
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '*':
                match('*');
                if ((look.tag != '(')) error("Error in grammar (expr)->mult after" + look+" expected: (");
                match('(');
                exprlist_val=exprlist();
                for(int i=0;i<exprlist_val-1;i++){
                    code.emit(OpCode.imul);
                }
                if ((look.tag != ')')) error("Error in grammar (expr)->mult after" + look+" expected: )");
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            case Tag.NUM:
                code.emit(OpCode.ldc,((NumberTok)look).lexeme);
                match(Tag.NUM);
                break;
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word) look).lexeme);
                if (id_addr == -1) {
                    error("The ID don't exist (expr)->ID after read( with " + look+")");
                }
                match(Tag.ID);
                code.emit(OpCode.iload,id_addr);
                break;
            default:
                error("Error in grammar after calling expr on" + look+" expected: + / * - NUM ID");

        }
    }

    public int exprlist() {
        if (look.tag == '+' || look.tag == '-' || look.tag == '*' || look.tag == '/'
                || look.tag == Tag.NUM || look.tag == Tag.ID) {
            expr();
            int exprlistp_val=exprlistp();
            return 1+exprlistp_val;
        } else {
            error("Error in grammar after calling exprlist on" + look+" expected: + / * - NUM ID");
            return 0;
        }
    }

    public int exprlistp() {
        switch (look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                int exprlistpp_val=exprlistp();
                return 1+exprlistpp_val;
            case ')':
                return 0;
            default:
                error("Error in grammar after calling exprlistp on" + look+" expected: + / * - NUM ID");
                return 0;
        }
        
    }

    public static void main(String[] args) throws Exception {
        Lexer lex = new Lexer();
        if (args.length < 1) {
            throw new Exception("Usage: no file passed <file_name>");
        }
        String path = args[0];
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator parser = new Translator(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
