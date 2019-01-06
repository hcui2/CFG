package com.hongzhucui.cfg;

/**
 * Starter code to implement an ExpressionParser. Your parser methods should use the following grammar:
 * E := A | X
 * A := A+M | M
 * M := M*M | X
 * X := (E) | L
 * L := [0-9]+ | [a-z]
 */
public class SimpleExpressionParser implements ExpressionParser {
	/*
	 * Attempts to create an expression tree -- flattened as much as possible -- from the specified String.
         * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @param withJavaFXControls you can just ignore this variable for R1
	 * @return the Expression object representing the parsed expression tree
	 */
	
	public Expression parse (String str, boolean withJavaFXControls) throws ExpressionParseException {
		// Remove spaces -- this simplifies the parsing logic
		str = str.replaceAll(" ", "");
		Expression expression = parseExpression(str);
		if (expression == null) {
			// If we couldn't parse the string, then raise an error
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Flatten the expression before returning
		expression.flatten();
		
		return expression;
	}
	
	protected Expression parseExpression (String str) {
		/* 
		 * Generation Rules:
		 * 
		 * E := A | X
		 * A := A+M | M
		 * M := M*M | X
		 * X := (E) | L
		 * L := [0-9]+ | [a-z]
		 * 
		 * Parse Rules
		 * A := E + E
		 * M := E + E
		 * X := (E)
		 * E := A | M | X | L
		 */
		
		return parseE(null, str);
	}
	
	private Expression parseE(CompoundExpression parent, String s) {
		Expression expression =  parseX(parent, s);
		if (expression == null) expression = parseSimpleCompound(parent, s, true);
		if (expression == null) expression = parseSimpleCompound(parent, s, false);
		if (expression == null) expression = parseL(parent, s);
		
		return expression;
	}
	private Expression parseSimpleCompound(CompoundExpression parent, String s, boolean add) {
		Expression expression = null;
		char indexChar = add? '+':'*';
		
		CompoundExpression pseudoParent = add? new AdditiveExpression(parent):new MultiplicativeExpression(parent);
		int index = s.indexOf(indexChar);
		
		while (index > -1 && expression == null) {
			Expression exp1 = parseE(pseudoParent, s.substring(0,index));
			Expression exp2 = parseE(pseudoParent,  s.substring(index+1, s.length()));
			
			if (exp1 != null && exp2 != null) {
				pseudoParent.addSubexpression(exp1);
				pseudoParent.addSubexpression(exp2);
				expression = pseudoParent;
			} else {
				index = s.indexOf(indexChar, index+1);
			}
		}
		return expression;
	}
	
	private Expression parseX(CompoundExpression parent, String s) {
		Expression expression = null;
		if (s.length() > 2 && s.charAt(0) == '(' && s.charAt(s.length()-1) == ')') {
			CompoundExpression pseudoParent = new ParentheticalExpression(parent);
			Expression child = parseE(pseudoParent, s.substring(1,s.length()-1));
			
			if (child != null) {
				pseudoParent.addSubexpression(child);
				expression = pseudoParent;
			}
		}
		return expression;
	}
	private Expression parseL(CompoundExpression parent, String s) {
		
		int status = -1;
		
		for (int i = 0; i < s.length() && i > -2; i++) {
			char c = s.charAt(i);
			if (isLetter(c)) {
				if (status != -1) status = -2;
				else status = 1;
			} else if (isNumber(c)) {
				if (status == 1) status = -2;
				else if (status == -1) status = 0;
			} else {
				status = -2;
			}
		}
		
		if (status < 0) return null;
		else return new LiteralExpression(parent, s);	
	}
	
	private static boolean isLetter(char c) {
		return c >= 'a' && c <= 'z';
	}
	private static boolean isNumber(char c) {
		return c >= '0' && c <= '9';
	}
}
