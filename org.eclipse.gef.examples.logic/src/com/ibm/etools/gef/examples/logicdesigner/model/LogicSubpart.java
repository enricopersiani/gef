package com.ibm.etools.gef.examples.logicdesigner.model;
/*
 * Licensed Material - Property of IBM
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 */

import java.util.*;
import org.eclipse.swt.graphics.Image;
import com.ibm.etools.draw2d.geometry.*;
import org.eclipse.ui.views.properties.*;

import com.ibm.etools.gef.KeyHandler;
import com.ibm.etools.gef.examples.logicdesigner.LogicResources;

abstract public class LogicSubpart
	extends LogicElement
{

private String id;
protected Hashtable inputs = new Hashtable (7);
protected Point location = new Point(0,0);
protected Vector  outputs  = new Vector (4,4);
static final long serialVersionUID = 1;
protected Dimension size = new Dimension(-1,-1);

protected static IPropertyDescriptor[] descriptors = null;
public static String ID_SIZE = "size";         //$NON-NLS-1$
public static String ID_LOCATION = "location"; //$NON-NLS-1$

static{
	descriptors = new IPropertyDescriptor[]{
		new PropertyDescriptor(ID_SIZE, LogicResources.
			getString("PropertyDescriptor.LogicSubPart.Size")),    //$NON-NLS-1$
		new PropertyDescriptor(ID_LOCATION,LogicResources.
			getString("PropertyDescriptor.LogicSubPart.Location")) //$NON-NLS-1$
	};
}

public LogicSubpart() {
	setID(getNewID());
}

public void connectInput(Wire w) {
	inputs.put(w.getTargetTerminal(), w);
	update();
	fireStructureChange(INPUTS, w);
}

public void connectOutput(Wire w) {
	outputs.addElement(w);
	update();
	fireStructureChange(OUTPUTS, w);
}

public void disconnectInput(Wire w) {
	inputs.remove(w.getTargetTerminal());
	update();
	fireStructureChange(INPUTS,w);
}

public void disconnectOutput(Wire w) {
	outputs.removeElement(w);
	update();
	fireStructureChange(OUTPUTS,w);
}

public Vector getConnections() {
	Vector v = (Vector)outputs.clone();
	Enumeration ins = inputs.elements();
	while (ins.hasMoreElements())
		v.addElement(ins.nextElement());
	return v;
}

public Image getIcon() {
	return getIconImage();
}

abstract public Image getIconImage();

public String getID() {
	return id; 
}

protected boolean getInput(String terminal) {
	Wire w = (Wire)inputs.get(terminal);
	return (w == null) ? false : w.getValue();
}

public Point getLocation() {
	return location;
}

abstract protected String getNewID();

/**
 * Returns useful property descriptors for the use
 * in property sheets. this supports location and
 * size.
 *
 * @return  Array of property descriptors.
 */
public IPropertyDescriptor[] getPropertyDescriptors() {
	return descriptors;
}


/**
 * Returns an Object which represents the appropriate
 * value for the property name supplied.
 *
 * @param propName  Name of the property for which the
 *                  the values are needed.
 * @return  Object which is the value of the property.
 */
public Object getPropertyValue(Object propName) {
	if (ID_SIZE.equals(propName))
		return new DimensionPropertySource(getSize());
	else if( ID_LOCATION.equals(propName))
		return new LocationPropertySource(getLocation());
	return null;
}

public Dimension getSize() {
	return size;
}

public Vector getSourceConnections() {
	return (Vector)outputs.clone();
}

public Vector getTargetConnections() {
	Enumeration enum = inputs.elements();
	Vector v = new Vector(inputs.size());
	while (enum.hasMoreElements())
		v.addElement(enum.nextElement());
	return v;
}

/**
 * 
 */
public boolean isPropertySet(){
	return true;	
}

/*
 * Does nothing for the present, but could be
 * used to reset the properties of this to 
 * whatever values are desired.
 *
 * @param id  Parameter which is to be reset.
 *
public void resetPropertyValue(Object id){
	if(ID_SIZE.equals(id)){;}
	if(ID_LOCATION.equals(id)){;}
}*/

public void setID(String s) {
	id = s;
}

public void setLocation(Point p) {
	if (location.equals(p)) return;
	location = p;
	firePropertyChange("location", null, p);  //$NON-NLS-1$
}

protected void setOutput(String terminal, boolean val) {
	Enumeration enum = outputs.elements();
	Wire w;
	while (enum.hasMoreElements()) {
		w = (Wire) enum.nextElement();
		if (w.getSourceTerminal().equals(terminal) && this.equals(w.getSource()))
			w.setValue(val);
	}
}

/**
 * Sets the value of a given property with the value
 * supplied. Also fires a property change if necessary.
 * 
 * @param id  Name of the parameter to be changed.
 * @param value  Value to be set to the given parameter.
 */
public void setPropertyValue(Object id, Object value){
	if (ID_SIZE.equals(id)){
		DimensionPropertySource dimPS = (DimensionPropertySource)value;
		setSize(new Dimension(dimPS.getValue()));
	}
	else if (ID_LOCATION.equals(id)){
		LocationPropertySource locPS = (LocationPropertySource)value;
		setLocation(new Point(locPS.getValue()));
	}
}

public void setSize(Dimension d) {
	if (size.equals(d)) return;
	size = d;
	firePropertyChange("size", null, size);  //$NON-NLS-1$
}

}
