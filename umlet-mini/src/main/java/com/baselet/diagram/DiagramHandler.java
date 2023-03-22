package com.baselet.diagram;

import java.awt.MouseInfo;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baselet.control.HandlerElementMap;
import com.baselet.control.SharedUtils;
import com.baselet.control.basics.Converter;
import com.baselet.control.basics.geom.Point;
import com.baselet.control.constants.Constants;
import com.baselet.diagram.io.DiagramFileHandler;
import com.baselet.element.ComponentSwing;
import com.baselet.element.NewGridElement;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.old.custom.CustomElement;
import com.baselet.element.old.element.Relation;

public class DiagramHandler {

	private static final Logger log = LoggerFactory.getLogger(DiagramHandler.class);

	private boolean isChanged;
	private final DiagramFileHandler fileHandler;
	private FontHandler fontHandler;

	protected DrawPanel drawpanel;
	private String helptext;
	private boolean enabled;
	private int gridSize;

	public static DiagramHandler forExport(FontHandler fontHandler) {
		DiagramHandler returnHandler = new DiagramHandler(null);
		if (fontHandler != null) {
			returnHandler.fontHandler = fontHandler;
		}
		return returnHandler;
	}

	public static DiagramHandler forExport(String diagram) {
		return new DiagramHandler(diagram);
	}

	private DiagramHandler(String diagram) {
		gridSize = Constants.DEFAULTGRIDSIZE;
		isChanged = false;
		enabled = true;
		drawpanel = createDrawPanel();
		fontHandler = new FontHandler(this);
		fileHandler = DiagramFileHandler.createInstance(this, diagram);
		fileHandler.doParse();
	}

	protected DrawPanel createDrawPanel() {
		return new DrawPanel(this, true);
	}

	public void setEnabled(boolean en) {
		if (!en && enabled) {
			enabled = false;
		}
		else if (en && !enabled) {
			enabled = true;
		}
	}

	public void setChanged(boolean changed) {
		if (isChanged != changed) {
			isChanged = changed;
		}
	}

	public DrawPanel getDrawPanel() {
		return drawpanel;
	}

	public FontHandler getFontHandler() {
		return fontHandler;
	}

	public void setHelpText(String helptext) {
		this.helptext = helptext;
	}

	public String getHelpText() {
		if (helptext == null) {
			return Constants.getDefaultHelptext();
		}
		else {
			return helptext;
		}
	}

	public boolean isChanged() {
		return isChanged;
	}

	public int getGridSize() {
		return gridSize;
	}

	public float getZoomFactor() {
		return (float) getGridSize() / (float) Constants.DEFAULTGRIDSIZE;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	public int realignToGrid(double val) {
		return realignToGrid(true, val, false);
	}

	public int realignToGrid(boolean logRealign, double val) {
		return realignToGrid(logRealign, val, false);
	}

	public int realignToGrid(boolean logRealign, double val, boolean roundUp) {
		return SharedUtils.realignTo(logRealign, val, roundUp, gridSize);
	}

	public static int realignTo(int val, int toVal) {
		return SharedUtils.realignTo(false, val, false, toVal);
	}

	public static void zoomEntity(int fromFactor, int toFactor, GridElement e) {
		Vector<GridElement> vec = new Vector<GridElement>();
		vec.add(e);
		zoomEntities(fromFactor, toFactor, vec);
	}

	public static void zoomEntities(int fromFactor, int toFactor, List<GridElement> selectedEntities) {

		/**
		 * The entities must be resized to the new factor
		 */

		for (GridElement entity : selectedEntities) {
			int newX = entity.getRectangle().x * toFactor / fromFactor;
			int newY = entity.getRectangle().y * toFactor / fromFactor;
			int newW = entity.getRectangle().width * toFactor / fromFactor;
			int newH = entity.getRectangle().height * toFactor / fromFactor;
			entity.setLocation(realignTo(newX, toFactor), realignTo(newY, toFactor));
			// Normally there should be no realign here but relations and custom elements sometimes must be realigned therefore we don't log it as an error
			if (entity instanceof CustomElement) {
				entity.setSize(newW, newH); // #478: do not realign width and height for custom elements, because this would mess up the CustomElement.changeSizeIfNoBugfix() call
			}
			else {
				entity.setSize(realignTo(newW, toFactor), realignTo(newH, toFactor));
			}

			// Resize the coordinates of the points of the relations
			if (entity instanceof Relation) {
				for (Point point : ((Relation) entity).getLinePoints()) {
					newX = point.getX() * toFactor / fromFactor;
					newY = point.getY() * toFactor / fromFactor;
					point.setX(realignTo(newX, toFactor));
					point.setY(realignTo(newY, toFactor));
				}
			}
		}
	}

	public void setGridAndZoom(int factor) {

		/**
		 * Store the old gridsize and the new one. Furthermore check if the zoom process must be made
		 */

		int oldGridSize = getGridSize();

		if (factor < 1 || factor > 20) {
			return; // Only zoom between 10% and 200% is allowed
		}
		if (factor == oldGridSize) {
			return; // Only zoom if gridsize has changed
		}

		setGridSize(factor);

		/**
		 * Zoom entities to the new gridsize
		 */

		zoomEntities(oldGridSize, gridSize, getDrawPanel().getGridElements());

		// AB: Zoom origin
		getDrawPanel().zoomOrigin(oldGridSize, gridSize);
	}

	public void setHandlerAndInitListeners(GridElement element) {
		HandlerElementMap.setHandlerForElement(element, this);
		if (element instanceof NewGridElement) {
			((ComponentSwing) element.getComponent()).setHandler(this);
		}
		element.updateModelFromText(); // must be updated here because the new handler could have a different zoom level
	}

	public void close() {
		HandlerElementMap.clear();
	}
}
