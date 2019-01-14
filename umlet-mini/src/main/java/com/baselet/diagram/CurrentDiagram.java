package com.baselet.diagram;

public class CurrentDiagram {

	private final static CurrentDiagram instance = new CurrentDiagram();

	public static CurrentDiagram getInstance() {
		return instance;
	}

	private DiagramHandler currentDiagramHandler;

	// sets the current diagram the user works with - that may be a palette too
	public void setCurrentDiagramHandler(DiagramHandler handler) {
		currentDiagramHandler = handler;
	}

	public void setCurrentDiagramHandlerAndZoom(DiagramHandler handler) {
		setCurrentDiagramHandler(handler);
	}

	// returns the current diagramhandler the user works with - may be a diagramhandler of a palette too
	public DiagramHandler getDiagramHandler() {
		return currentDiagramHandler;
	}

}
