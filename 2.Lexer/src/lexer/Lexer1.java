/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

/**
 *
 * @author Daniel
 */
import java.io.*; 
import java.util.*;

public class Lexer1 {

    public static int line = 1;
    private char peek = ' ';
    public boolean alreadyRead=false;
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;
            case '/':
                peek = ' ';
                return Token.div;
            case ';':
                peek = ' ';
                return Token.semicolon;
            
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }
                
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
            case '<':
                readch(br);
                if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                }else if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else {
                    return Word.lt;
                }
            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    return Word.gt;
                }
            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    return Token.assign;
                }
            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek)) {
                    String res=new String();
                    res=res+peek;
                    do{
                    readch(br);
                    if(Character.isLetter(peek) || Character.isDigit(peek)){
                        res=res+peek;
                    }
                    }while(Character.isLetter(peek) || Character.isDigit(peek));
                    if(res.equals("cond"))return Word.cond;
                    if(res.equals("when"))return Word.when;
                    if(res.equals("then"))return Word.then;
                    if(res.equals("else"))return Word.elsetok;
                    if(res.equals("while"))return Word.whiletok;
                    if(res.equals("do"))return Word.dotok;
                    if(res.equals("seq"))return Word.seq;
                    if(res.equals("print"))return Word.print;
                    if(res.equals("read"))return Word.read;
                    return new Word(Tag.ID,res);
                    
                } else if (Character.isDigit(peek)) {
                    
                    if(peek=='0'){
                        readch(br);
                        if(Character.isDigit(peek)){
                            System.err.println("Useles 0 before number: " 
                                + peek );
                        return null;
                        }
                        return new NumberTok(Tag.NUM,0);
                    }else{
                        int num=Character.getNumericValue(peek);
                        do{
                            readch(br);
                            if(Character.isDigit(peek)){
                            num=(num*10)+Character.getNumericValue(peek);
                            }
                        }while(Character.isLetter(peek) || Character.isDigit(peek));
                        return new NumberTok(Tag.NUM,num);
                    }
                    
                } else {
                        System.err.println("Erroneous character: " 
                                + peek );
                        return null;
                }
         }
    }
		
    public static void main(String[] args) throws Exception {
        Lexer1 lex = new Lexer1();
        if (args.length < 1) {
            throw new Exception("Usage: no file parameter <file_name>");
         }
        try {
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}