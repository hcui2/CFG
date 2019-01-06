package com.hongzhucui.cfg;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class ParentheticalExpression extends AbstractCompoundExpression {

	public ParentheticalExpression(CompoundExpression parent) {
		super(parent, new LinkedList<Expression>());
	}

	@Override
	protected AbstractCompoundExpression generateExpression(CompoundExpression parent) {
		return new ParentheticalExpression(parent);
	}

	@Override
	protected String getExpressionSymbol() {
		return "()";
	}
	
	@Override
	protected Node getLink() {
		return null;
	}

	@Override
	protected void editStart(List<Node> children) {
		children.add(ExpressionEditor.adjustFont(new Label("(")));
	}

	@Override
	protected void editEnd(List<Node> children) {
		children.add(ExpressionEditor.adjustFont(new Label(")")));
	}


}
