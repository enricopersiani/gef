package com.ibm.etools.gef.examples.logicdesigner.model;
/*
 * Licensed Material - Property of IBM
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 */

import org.eclipse.ui.views.properties.*;
import com.ibm.etools.draw2d.geometry.*;
import com.ibm.etools.gef.examples.logicdesigner.LogicResources;

public class LocationPropertySource
	implements IPropertySource{

public static String ID_XPOS = "xPos"; //$NON-NLS-1$
public static String ID_YPOS = "yPos"; //$NON-NLS-1$
protected static IPropertyDescriptor[] descriptors;

static{
	descriptors = new IPropertyDescriptor[] {
		new TextPropertyDescriptor(ID_XPOS,LogicResources.getString("LocationPropertySource.Property.X.Label")), //$NON-NLS-1$
		new TextPropertyDescriptor(ID_YPOS,LogicResources.getString("LocationPropertySource.Property.Y.Label"))  //$NON-NLS-1$
	};
}

protected Point point = null;

public LocationPropertySource(Point point){
	this.point = new Point(point);
}

public Object getEditableValue(){
	return this;
}

public IPropertyDescriptor[] getPropertyDescriptors(){
	return descriptors;
}

public Object getPropertyValue(Object propName){
	if(ID_XPOS.equals(propName)){
		return new String(new Integer(point.x).toString());
	}
	if(ID_YPOS.equals(propName)){
		return new String(new Integer(point.y).toString());
	}
	return null;
}

public Point getValue(){
	return new Point(point);
}

public boolean isPropertySet(Object propName){
	if(ID_XPOS.equals(propName) || ID_YPOS.equals(propName))return true;
	return false;
}

public void resetPropertyValue(Object propName){}

public void setPropertyValue(Object propName, Object value){
	if(ID_XPOS.equals(propName)){
		Integer newInt = new Integer((String)value);
		point.x = newInt.intValue();
	}
	if(ID_YPOS.equals(propName)){
		Integer newInt = new Integer((String)value);
		point.y = newInt.intValue();
	}
}

public String toString(){
	return new String("["+point.x+","+point.y+"]");//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
}

}
