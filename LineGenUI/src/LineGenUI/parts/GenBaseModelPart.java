package LineGenUI.parts;

import java.io.File;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import fr.varymde.cvl.generator.EcoreInstanceGenerator;
import fr.varymde.cvl.generator.Synthesis;

public class GenBaseModelPart {
	private Label baseModelLabel;
	private Text fileText;
	private Button generateButton;
	private Label extensionLabel;
	private Text extension;
	private Label maxManyLabel;

	private Spinner maxManySpinner;
	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new GridLayout(2,false));
		
		extensionLabel = new Label(parent, SWT.NONE);
		extensionLabel.setText("Extension");
		extensionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1 ));
		
		maxManyLabel = new Label(parent, SWT.NONE);
		maxManyLabel.setText("Max Many");
		maxManyLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,1,1 ));
		
		setExtension(new Text(parent, SWT.BORDER));
	    getExtension().setMessage("Insert the extension without dot");
	    getExtension().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	    
	    maxManySpinner = new Spinner(parent, SWT.BORDER);
		maxManySpinner.setMaximum(100);
		maxManySpinner.setSelection(20);
		maxManySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		generateButton = new Button(parent, SWT.PUSH);
		generateButton.setText("Generate BM");
		generateButton.setToolTipText("Generate a random Base Model");
		generateButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 2));

		

		generateButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e){
				System.out.println("\nGenerating BM ... \n");
				
				EcoreInstanceGenerator.baseModelExtension_$eq(getExtension().getText());
				EcoreInstanceGenerator.maxUpperBound_$eq(maxManySpinner.getSelection());
				EcoreInstanceGenerator.createEcoreModelInstance();
				//Synthesis.metaModelURL_$eq(file.getAbsolutePath());
				//Synthesis.runAnyEcoreMetamodel(); INSTEAD: generate base model instance
			}

		});

	}
	public Text getExtension() {
		return extension;
	}
	public void setExtension(Text extension) {
		this.extension = extension;
	}

}
