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
public class Tag {
    public final static int
	EOF = -1, NUM = 256, ID = 257, RELOP = 258,
	COND = 259, WHEN = 260, THEN = 261, ELSE = 262, 
	WHILE = 263, DO = 264, SEQ = 265, PRINT = 266, READ = 267, 
	OR = 268, AND = 269;
}