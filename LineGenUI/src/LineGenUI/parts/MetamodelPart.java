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
package LineGenUI.parts;


import java.io.File;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import fr.varymde.cvl.generator.Synthesis;

public class MetamodelPart {

	private Label metamodelLabel;
	private Text fileText;
	private File metamodelFile;
	
	private Button openButton;
	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new GridLayout(4,false));
		parent.setSize(parent.getSize().x, 50);
		parent.redraw();
		metamodelLabel = new Label(parent, SWT.NONE);
		metamodelLabel.setText("Metamodel Path");
		metamodelLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,4,1 ));
		
		fileText = new Text(parent, SWT.BORDER);
	    fileText.setMessage("Select the .ecore metamodel file");
	    fileText.setEditable(false);
	    fileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		openButton = new Button(parent,SWT.OPEN);
		openButton.setText("Open");
		

	    
		
		openButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e){
				FileDialog dialog = new FileDialog(parent.getShell());
				dialog.open();
				fileText.setText(dialog.getFileName());
				
				setMetamodelFile(new File(dialog.getFilterPath(),dialog.getFileName()));
			    Synthesis.metaModelURL_$eq(getMetamodelFile().getAbsolutePath());
			    Resource res = Synthesis.loadBaseMetamodel(getMetamodelFile().getAbsolutePath());
			    Synthesis.metaModelResource_$eq(res);
			    if(res!=null)
			    	System.out.println("\n Metamodel succesfully loaded.\n");
			    			
			}
			
		});

	}

	@Focus
	public void setFocus() {
	}

	public File getMetamodelFile() {
		return metamodelFile;
	}

	public void setMetamodelFile(File metamodelFile) {
		this.metamodelFile = metamodelFile;
	}
}
