/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.editparts;

import org.eclipse.draw2d.*;

import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.tools.MarqueeDragTracker;

/**
 * A graphical root that uses {@link org.eclipse.draw2d.FreeformFigure FreeformFigures} as
 * the layers in the diagram.  The {@link EditPartViewer#setContents(EditPart) contents}
 * editpart must provide a FreeformFigure as its figure. Freeform figures are special
 * because they can expand in any direction. This allows the user to drag objects or
 * bendpoints into the negative X and Y coordinates of a diagram.  If this feature is not
 * being used, clients should use the {@link FreeformGraphicalRootEditPart} as their
 * viewer's root editpart.
 * <P>
 * <EM>IMPORTANT:</EM> The contents editpart that is added to a freeform root should
 * have a <code>FreeformFigure</code> (such as FreeformLayer) as its Figure.  The primary
 * layer is <EM>not</EM> using a draw2d LayoutManager, and will not size the contents'
 * figure properly unless it is a freeform figure.
 * <P>
 * <EM>IMPORTANT:</EM>The freeform root uses a <code>FreeformViewport</code> as its
 * primary figure. This class must be used with the {@link
 * org.eclipse.gef.ui.parts.ScrollingGraphicalViewer}. The viewport gets installed into
 * that viewer's {@link org.eclipse.draw2d.FigureCanvas}, which provides native scrollbars
 * for scrolling the viewport.
 * <P>
 * This root serves as the diagram's {@link org.eclipse.gef.editparts.LayerManager},
 * providing the following layer structure, in top-to-bottom order:
 * <table cellspacing="0" cellpadding="0">
 *   <tr>
 *     <td colspan="2">Root Freeform Layered Pane</td>
 *   </tr>
 *   <tr>
 *     <td>&#9500;</td>
 *     <td>&nbsp;Feedback Layer</td>
 *   </tr>
 *   <tr>
 *     <td>&#9500;</td>
 *     <td>&nbsp;Handle Layer</td>
 *   </tr>
 *   <tr>
 *     <td>&#9492;</td>
 *     <td>&nbsp;Printable Layers</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;</td>
 *     <td>&#9500; Connection Layer</td>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;</td>
 *     <td>&#9492; Primary Layer</td>
 *   </tr>
 * </table>
 * 
 */
public class FreeformGraphicalRootEditPart
	extends AbstractGraphicalEditPart
	implements RootEditPart, LayerConstants, LayerManager
{

/**
 * @deprecated call getContents()
 */
protected EditPart contents;

/**
 * @deprecated call getViewer()
 */
protected EditPartViewer viewer;
private LayeredPane innerLayers;
private LayeredPane printableLayers;

/**
 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
 */
protected void createEditPolicies() { }

/**
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
protected IFigure createFigure() {
	FreeformViewport viewport = new FreeformViewport();
	innerLayers = new FreeformLayeredPane();
	createLayers(innerLayers);
	viewport.setContents(innerLayers);
	return viewport;
}

/**
 * Creates the top-most set of layers on the given layered pane.
 * @param layeredPane the parent for the created layers
 */
protected void createLayers(LayeredPane layeredPane) {
	layeredPane.add(getPrintableLayers(), PRINTABLE_LAYERS);
	layeredPane.add(new FreeformLayer(), HANDLE_LAYER);
	layeredPane.add(new FeedbackLayer(), FEEDBACK_LAYER);
	layeredPane.add(new GuideLayer(), GUIDE_LAYER);
}

/**
 * Creates a layered pane and the layers that should be printed.
 * @see org.eclipse.gef.print.PrintGraphicalViewerOperation
 * @return a new LayeredPane containing the printable layers
 */
protected LayeredPane createPrintableLayers() {
	FreeformLayeredPane layeredPane = new FreeformLayeredPane();
	layeredPane.add(new FreeformLayer(), PRIMARY_LAYER);
	layeredPane.add(new ConnectionLayer(), CONNECTION_LAYER);
	return layeredPane;
}

/**
 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
 */
public Object getAdapter(Class adapter) {
	if (adapter == AutoexposeHelper.class)
		return new ViewportAutoexposeHelper(this);
	return super.getAdapter(adapter);
}

/**
 * The RootEditPart should never be asked for a command. The implementation returns an
 * unexecutable command.
 * @see org.eclipse.gef.EditPart#getCommand(org.eclipse.gef.Request)
 */
public Command getCommand(Request req) {
	return UnexecutableCommand.INSTANCE;
}

/**
 * The contents' Figure will be added to the PRIMARY_LAYER.
 * @see org.eclipse.gef.GraphicalEditPart#getContentPane()
 */
public IFigure getContentPane() {
	return getLayer(PRIMARY_LAYER);
}

/**
 * @see org.eclipse.gef.RootEditPart#getContents()
 */
public EditPart getContents() {
	return contents;
}

/**
 * Should not be called, but returns a MarqeeDragTracker for good measure.
 * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
 */
public DragTracker getDragTracker(Request req) {
	/* 
	 * The root will only be asked for a drag tracker if for some reason the contents
	 * editpart says it is neither selector nor opaque.
	 */
	return new MarqueeDragTracker();
}

/**
 * Returns the layer indicated by the key. Searches all layered panes.
 * @see LayerManager#getLayer(Object)
 */
public IFigure getLayer(Object key) {
	if (innerLayers == null)
		return null;
	IFigure layer = innerLayers.getLayer(key);
	if (layer != null)
		return layer;
	if (printableLayers == null)
		return null;
	return printableLayers.getLayer(key);
}

/**
 * The root editpart does not have a real model.  The LayerManager ID is returned so that
 * this editpart gets registered using that key.
 * @see org.eclipse.gef.EditPart#getModel()
 */
public Object getModel() {
	return LayerManager.ID;
}

/**
 * Returns the LayeredPane that should be used during printing. This layer will be
 * identified using {@link LayerConstants#PRINTABLE_LAYERS}.
 * @return the layered pane containing all printable content
 */
protected LayeredPane getPrintableLayers() {
	if (printableLayers == null)
		printableLayers = createPrintableLayers();
	return printableLayers;
}

/**
 * Returns <code>this</code>.
 * @see org.eclipse.gef.EditPart#getRoot()
 */
public RootEditPart getRoot() {
	return this;
}

/**
 * Returns the viewer that was set.
 * @see org.eclipse.gef.EditPart#getViewer()
 */
public EditPartViewer getViewer() {
	return viewer;
}

/**
 * Overridden to do nothing, child is set using setContents(EditPart)
 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshChildren()
 */
protected void refreshChildren() { }

/**
 * @see org.eclipse.gef.RootEditPart#setContents(org.eclipse.gef.EditPart)
 */
public void setContents(EditPart editpart) {
	if (contents != null)
		removeChild(contents);
	contents = editpart;
	if (contents != null)
		addChild(contents, 0);
}

/**
 * Sets the EditPartViewer.
 * @param newViewer EditPartViewer.
 */
public void setViewer(EditPartViewer newViewer) {
	if (viewer == newViewer)
		return;
	if (viewer != null)
		unregister();
	viewer = newViewer;
	if (viewer != null)
		register();
}

class FeedbackLayer
	extends FreeformLayer
{
	FeedbackLayer() {
		setEnabled(false);
	}
}

}
