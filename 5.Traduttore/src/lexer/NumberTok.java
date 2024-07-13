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
public class NumberTok extends Token {
	public final int lexeme;
        public NumberTok(int tag,int s) { super(tag); lexeme=s; }
        @Override
        public String toString() { return "<" + tag + ", " + lexeme + ">"; }
}