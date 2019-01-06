package com.hongzhucui.cfg;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class LiteralExpression implements Expression {

	
	private CompoundExpression _parent;
	private String _l;
	private Node _node = null;
	
	public LiteralExpression(CompoundExpression parent, String l) {
		_parent = parent;
		_l = l;
	}
	
	@Override
	public CompoundExpression getParent() {
		return _parent;
	}

	@Override
	public void setParent(CompoundExpression parent) {
		_parent = parent;
	}

	@Override
	public Expression deepCopy() {
		return new LiteralExpression(_parent, _l);
	}

	@Override
	public void flatten() {
		//Nothing
	}

	@Override
	public String convertToString(int indentLevel) {
		StringBuffer sb = new StringBuffer("");
		Expression.indent(sb, indentLevel);
		sb.append(_l);
		
		return sb.toString();
	}

	@Override
	public Node getNode() {
		if (_node == null) {
			_node = ExpressionEditor.adjustFont(new Label(_l));
		}
		return _node;
	}

}
