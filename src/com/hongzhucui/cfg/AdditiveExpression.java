package com.hongzhucui.cfg;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class AdditiveExpression extends AbstractCompoundExpression {

	public AdditiveExpression(CompoundExpression parent) {
		super(parent, new LinkedList<Expression>());
	}

	@Override
	protected AbstractCompoundExpression generateExpression(CompoundExpression parent) {
		return new AdditiveExpression(parent);
	}

	@Override
	protected String getExpressionSymbol() {
		return "+";
	}

	@Override
	protected Node getLink() {
		return ExpressionEditor.adjustFont(new Label("+"));
	}

	@Override
	protected void editStart(List<Node> children) {
	}

	@Override
	protected void editEnd(List<Node> children) {
	}
}
