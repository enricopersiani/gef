package com.ibm.etools.gef.examples.logicdesigner.model;
/*
 * Licensed Material - Property of IBM
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 */

import com.ibm.etools.common.command.Command;
import com.ibm.etools.draw2d.geometry.*;
import com.ibm.etools.gef.examples.logicdesigner.LogicResources;

public class SetConstraintCommand
	extends com.ibm.etools.gef.commands.AbstractCommand
{

private Point newPos;
private Dimension newSize;
private Point oldPos;
private Dimension oldSize;
private LogicSubpart part;

public void execute() {
	oldSize = part.getSize();
	oldPos  = part.getLocation();
	part.setLocation(newPos);
	part.setSize(newSize);
}

public String getDescription() {
	String name = part.getClass().getName();
	name = name.substring(name.lastIndexOf(".")+1);//$NON-NLS-1$
	return LogicResources.getString("SetLocationCommand.Description")+" " + name;//$NON-NLS-2$//$NON-NLS-1$
}

public String getLabel(){
	if (oldSize.equals(newSize))
		return LogicResources.getString("SetLocationCommand.Label.Location");//$NON-NLS-1$
	return LogicResources.getString("SetLocationCommand.Label.Resize");//$NON-NLS-1$
}

public void redo() {
	part.setSize(newSize);
	part.setLocation(newPos);
}

public void setLocation(Rectangle r){
	setLocation(r.getLocation());
	setSize(r.getSize());
}

public void setLocation(Point p) {
	newPos = p;
}

public void setPart(LogicSubpart part) {
	this.part = part;
}

public void setSize(Dimension p) {
	newSize = p;
}

public void undo() {
	part.setSize(oldSize);
	part.setLocation(oldPos);
}

}
