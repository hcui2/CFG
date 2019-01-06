package com.hongzhucui.cfg;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ExpressionEditor extends Application {
	
	private static final int fontSize = 16;
	
	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {
		
		private Pane _pane;
		private CompoundExpression _rootExpression, _previousParent = null;
		private Expression _focus = null, _ghost = null;
		
		private List<Integer> _configurationIndices = new ArrayList<Integer>();
		private List<Double> _configurationLengths = new ArrayList<Double>();
		
		private double prevX = 0, prevY = 0;
		
		MouseEventHandler (Pane pane_, CompoundExpression rootExpression_) {
			_pane = pane_;
			_rootExpression = rootExpression_;
		}

		public void handle (MouseEvent event) {
			
			double x = event.getSceneX(), y = event.getSceneY();
			
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				if (_focus == null) {
					_focus = getChildClicked(_rootExpression, x, y);
					_previousParent = _rootExpression;
				}
				else {
					_focus.getNode().setStyle("-fx-focus-color: transparent");
					
					Expression child = getChildClicked(_focus, x,y);
					if (child == null) {
						if (isInNode(_focus.getNode(),x,y)) {
							_ghost = _focus.deepCopy();
							Node ghostNode = _ghost.getNode();
							Bounds oldBounds = getSceneBounds(_focus.getNode());
							
							_pane.getChildren().add(ghostNode);
							
							ghostNode.setLayoutX(oldBounds.getMinX());
							ghostNode.setLayoutY(oldBounds.getMinY());
							
							generateConfigurations();
							
							prevX = x;
							prevY = y;
							
							editTextColor(_focus.getNode(), Expression.GHOST_COLOR);
						}
						else {
							_focus = null;
						}
					}
					else {
						_previousParent = (CompoundExpression) _focus;
						_focus = child;
					}
				}
			} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				if (_ghost != null) {
					Node n = _ghost.getNode();
					n.setTranslateX(n.getTranslateX()+x-prevX);
					n.setTranslateY(n.getTranslateY()+y-prevY);
					
					updateFocusPos();
					
					prevX = x;
					prevY = y;
				}
			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				if (_ghost != null) {
					_pane.getChildren().remove(_ghost.getNode());
					
					_configurationLengths.clear();
					_configurationIndices.clear();
					
					_ghost = null;
					
					editTextColor(_focus.getNode(), Color.BLACK);
					
					System.out.println("-----");
					System.out.println(_rootExpression.convertToString(0));
				}
			}
			
			
			if (_focus != null) {
				_focus.getNode().setStyle("-fx-border-color: red;");
			}
			
		}
		
		private Expression getChildClicked(Expression clicked, double x, double y) {
			if (clicked instanceof AbstractCompoundExpression) {
				for (Expression child : ((AbstractCompoundExpression)clicked).getChildren()) {
					if (isInNode(child.getNode(), x, y)) return child;
				}
			}
			return null;
		}
		private boolean isInNode(Node n, double x, double y) {
			return getSceneBounds(n).contains(x,y);
		}
		private Bounds getSceneBounds(Node n) {
			return n.localToScene(n.getBoundsInLocal());
		}
		
		private void updateFocusPos() {
			double ghostX = getSceneBounds(_ghost.getNode()).getMinX()-getSceneBounds(_previousParent.getNode()).getMinX();
			
			int closestNodeIndex = -1, focusNodeIndex = -1, focusExprIndex = -1, closestExprIndex = -1;
			double closestDistance = 0;
			
			for (int i = 0; i < _configurationLengths.size(); i++) {
				double distance = Math.abs(ghostX-_configurationLengths.get(i));
				if (closestNodeIndex < 0 || closestDistance > distance) {
					closestNodeIndex = _configurationIndices.get(i);
					closestDistance = distance;
					closestExprIndex = i;
				}
				
				if (((AbstractCompoundExpression)_previousParent).getChildren().get(i) == _focus) {
					focusNodeIndex = _configurationIndices.get(i);
					focusExprIndex = i;
				}
			}
			
			if (focusExprIndex != closestExprIndex) {
				
				HBox hb = (HBox) _previousParent.getNode();
				ObservableList<Node> swapping = FXCollections.observableArrayList(hb.getChildren());
				Collections.swap(swapping, focusNodeIndex, closestNodeIndex);
				((HBox)_previousParent.getNode()).getChildren().setAll(swapping);
				
				Collections.swap(((AbstractCompoundExpression)_previousParent).getChildren(), closestExprIndex, focusExprIndex);
			}
		}
		
		private void generateConfigurations() {
			
			List<Node> childNodes = ((HBox)_previousParent.getNode()).getChildren();
			List<Expression> children = ((AbstractCompoundExpression)_previousParent).getChildren();
			double netLength = 0;
			
			for (int i = 0; i < childNodes.size(); i++) {
				Node n = childNodes.get(i);
				Expression found = null;
				
				for (int childIndex = 0; childIndex < children.size(); childIndex++) {
					Expression child = children.get(childIndex);
					if (child.getNode() == n) {
						found = child;
						break;
					}
				}
				
				if (found != null) {
					_configurationIndices.add(i);
					_configurationLengths.add(netLength);
					
					if (found == _focus) {
						continue;
					}
				}
				
				netLength+=n.getBoundsInLocal().getMaxX() - n.getBoundsInLocal().getMinX();
			}
		}
		
		private void editTextColor(Node n, Color textColor) {
			if (n instanceof Label) {
				((Label) n).setTextFill(textColor);
			}
			else if (n instanceof HBox) {
				Iterator<Node> children = ((HBox) n).getChildren().iterator();
				while (children.hasNext()) {
					editTextColor(children.next(), textColor);
				}
			}
		}
	}

	/**
	 * Size of the GUI
	 */
	private static final int WINDOW_WIDTH = 500, WINDOW_HEIGHT = 250;

	/**
	 * Initial expression shown in the textbox
	 */
	private static final String EXAMPLE_EXPRESSION = "2*x+3*y+4*z+(7+6*z)";

	/**
	 * Parser used for parsing expressions.
	 */
	private final ExpressionParser expressionParser = new SimpleExpressionParser();

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Expression Editor");

		// Add the textbox and Parser button
		final Pane queryPane = new HBox();
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button button = new Button("Parse");
		queryPane.getChildren().add(textField);

		final Pane expressionPane = new Pane();

		// Add the callback to handle when the Parse button is pressed	
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle (MouseEvent e) {
				// Try to parse the expression
				try {
					// Success! Add the expression's Node to the expressionPane
					final Expression expression = expressionParser.parse(textField.getText(), true);
					System.out.println(expression.convertToString(0));
					expressionPane.getChildren().clear();
					expressionPane.getChildren().add(expression.getNode());
					expression.getNode().setLayoutX(WINDOW_WIDTH/4);
					expression.getNode().setLayoutY(WINDOW_HEIGHT/2);

					// If the parsed expression is a CompoundExpression, then register some callbacks
					if (expression instanceof CompoundExpression) {
						((Pane) expression.getNode()).setBorder(Expression.NO_BORDER);
						final MouseEventHandler eventHandler = new MouseEventHandler(expressionPane, (CompoundExpression) expression);
						expressionPane.setOnMousePressed(eventHandler);
						expressionPane.setOnMouseDragged(eventHandler);
						expressionPane.setOnMouseReleased(eventHandler);
					}
				} catch (ExpressionParseException epe) {
					// If we can't parse the expression, then mark it in red
					textField.setStyle("-fx-text-fill: red");
				}
			}
		});
		queryPane.getChildren().add(button);

		// Reset the color to black whenever the user presses a key
		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));
		
		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(expressionPane);

		primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
		primaryStage.show();
	}
	
	
	public static Label adjustFont(Label l) {
		l.setFont(Font.font(l.getFont().getName(), FontWeight.BOLD, fontSize));
		return l;
	}
}
