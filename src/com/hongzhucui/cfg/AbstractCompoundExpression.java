package com.hongzhucui.cfg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

public abstract class AbstractCompoundExpression implements CompoundExpression {

	private static final int spacing = 1;
	
	private CompoundExpression _parent;
	private List<Expression> _children;
	private Node _node = null;
	
	public AbstractCompoundExpression(CompoundExpression parent, List<Expression> children) {
		_parent = parent;
		_children = children;
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
		List<Expression> children = new LinkedList<Expression>();
		for (Expression child : _children) {
			children.add(child.deepCopy());
		}
		
		AbstractCompoundExpression ace = generateExpression(_parent);
		ace._children = children;
		return ace;
	}

	@Override
	public void flatten() {
		
		List<Expression> children = new LinkedList<Expression>();
		for (Expression child : _children) {
			child.flatten();
			
			if (child instanceof AbstractCompoundExpression && 
					((AbstractCompoundExpression)child).getExpressionSymbol().equals(getExpressionSymbol())) {
				
				AbstractCompoundExpression ace = (AbstractCompoundExpression) child;
				for (Expression grandchild : ace._children) {
					grandchild.setParent(this);
					children.add(grandchild);
				}
			} else {
				children.add(child);
			}
		}
		
		_children = children;
	}

	@Override
	public String convertToString(int indentLevel) {
		StringBuffer sb = new StringBuffer("");
		Expression.indent(sb, indentLevel);
		sb.append(getExpressionSymbol());
		sb.append('\n');
		
		for (Expression child : _children) {
			sb.append(child.convertToString(indentLevel+1) + '\n');
		}
		
		for (int i = 0; i < sb.length()-1; i++) {
			while (i < sb.length()-1 && sb.charAt(i) == '\n' && sb.charAt(i+1) == '\n') {
				sb.delete(i, i+1);
				i++;
			}
		}
		return sb.toString();
	}

	@Override
	public void addSubexpression(Expression subexpression) {
		_children.add(subexpression);
	}
	
	public List<Expression> getChildren() {
		return _children;
	}
	
	@Override
	public Node getNode() {
		if (_node == null) {
			final HBox hb = new HBox(spacing);
			final List<Node> childNodes = hb.getChildren();
			
			editStart(childNodes);
			
			Iterator<Expression> iter = _children.iterator();
			while (iter.hasNext()) {
				Expression child = iter.next();
				
				childNodes.add(child.getNode());
				if (iter.hasNext()) {
					Node link = getLink();
					if (link != null) childNodes.add(link);
				}
			}
			
			editEnd(childNodes);
			_node = hb;
		}
		
		return _node;
	}
	
	protected abstract AbstractCompoundExpression generateExpression(CompoundExpression parent);
	
	protected abstract String getExpressionSymbol();
	protected abstract void editStart(List<Node> children);
	protected abstract void editEnd(List<Node> children);
	protected abstract Node getLink();
}
