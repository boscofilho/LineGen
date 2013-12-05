/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package LineGenUI.handlers;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import fr.varymde.cvl.generator.Synthesis;

public class OpenHandler {

	@Execute
	public void execute(
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell){
		DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.open();
		Synthesis.filterPath_$eq(dialog.getFilterPath());
		System.out.println("\nOutput folder set to:\n " + dialog.getFilterPath());
	}
}
