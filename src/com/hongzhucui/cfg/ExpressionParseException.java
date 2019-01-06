package com.hongzhucui.cfg;

/**
 * Exception thrown when a ExpressionParser fails to parse a specified string.
 */
@SuppressWarnings("serial")
class ExpressionParseException extends Exception {
	public ExpressionParseException (String message) {
		super(message);
	}
}
