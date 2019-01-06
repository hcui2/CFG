package com.hongzhucui.cfg;

import javafx.scene.control.Label;

public class ExpressionLabel extends Label {

	private Expression _expr;
	
	public ExpressionLabel(Expression expr) {
		super();
		_expr = expr;
	}
	
	public Expression getExpression() {
		return _expr;
	}
}
