package com.ibm.etools.gef.examples.logicdesigner.edit;
/*
 * Licensed Material - Property of IBM
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import com.ibm.etools.gef.*;
import com.ibm.etools.gef.editparts.AbstractConnectionEditPart;

import com.ibm.etools.gef.examples.logicdesigner.model.*;
import com.ibm.etools.gef.examples.logicdesigner.figures.*;

import com.ibm.etools.draw2d.*;

/**
 * Implements a Connection Editpart to represnt a Wire like
 * connection.
 *
 */
public class WireEditPart
	extends AbstractConnectionEditPart
	implements PropertyChangeListener
{

public static final Color
	alive = new Color(Display.getDefault(),0,74,168),
	dead  = new Color(Display.getDefault(),0,0,0);

public void activate(){
	super.activate();
	getWire().addPropertyChangeListener(this);
}

public void activateFigure(){
	super.activateFigure();
	/*Once the figure has been added to the ConnectionLayer, start listening for its
	 * router to change.
	 */
	getFigure().addPropertyChangeListener(Connection.PROPERTY_CONNECTION_ROUTER, this);
}

/**
 * Adds extra EditPolicies as required. 
 */
protected void createEditPolicies() {
	installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new WireEndpointEditPolicy());
	//Note that the Connection is already added to the diagram and knows its Router.
	refreshBendpointEditPolicy();
	installEditPolicy(EditPolicy.CONNECTION_ROLE,new WireEditPolicy());
}

/**
 * Returns a newly created Figure to represent the connection.
 *
 * @return  The created Figure.
 */
protected IFigure createFigure() {
	if (getWire() == null)
		return null;
	Connection connx = FigureFactory.createNewBendableWire(getWire());
	return connx;
}

public void deactivate(){
	getWire().removePropertyChangeListener(this);
	super.deactivate();
}

public void deactivateFigure(){
	getFigure().removePropertyChangeListener(Connection.PROPERTY_CONNECTION_ROUTER, this);
	super.deactivateFigure();
}

/**
 * Returns the model of this represented as a Wire.
 * 
 * @return  Model of this as <code>Wire</code>
 */
protected Wire getWire() {
	return (Wire)getModel();
}

/**
 * Returns the Figure associated with this, which draws the
 * Wire.
 *
 * @return  Figure of this.
 */
protected IFigure getWireFigure() {
	return (PolylineConnection) getFigure();
}

/**
 * Listens to changes in properties of the Wire (like the
 * contents being carried), and reflects is in the visuals.
 *
 * @param event  Event notifying the change.
 */
public void propertyChange(PropertyChangeEvent event) {
	String property = event.getPropertyName();
	if ("connectionRouter".equals(property)){ //$NON-NLS-1$
		refreshBendpoints();
		refreshBendpointEditPolicy();
	}
	if ("value".equals(property))   //$NON-NLS-1$
		refreshVisuals();
	if ("bendpoint".equals(property))   //$NON-NLS-1$
		refreshBendpoints();       
}

/**
 * Updates the bendpoints, based on the model.
 */
protected void refreshBendpoints() {
	if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter)
		return;
	List modelConstraint = getWire().getBendpoints();
	List figureConstraint = new ArrayList();
	for (int i=0; i<modelConstraint.size(); i++) {
		WireBendpoint wbp = (WireBendpoint)modelConstraint.get(i);
		RelativeBendpoint rbp = new RelativeBendpoint(getConnectionFigure());
		rbp.setRelativeDimensions(wbp.getFirstRelativeDimension(),
									wbp.getSecondRelativeDimension());
		rbp.setWeight((i+1) / ((float)modelConstraint.size()+1));
		figureConstraint.add(rbp);
	}
	getConnectionFigure().setRoutingConstraint(figureConstraint);
}

private void refreshBendpointEditPolicy(){
	if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter)
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, null);
	else
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new WireBendpointEditPolicy());
}

/**
 * Refreshes the visual aspects of this, based upon the
 * model (Wire). It changes the wire color depending on
 * the state of Wire.
 * 
 */
protected void refreshVisuals() {
	refreshBendpoints();
	if (getWire().getValue())
		getWireFigure().setForegroundColor(alive);
	else
		getWireFigure().setForegroundColor(dead);
}

}
