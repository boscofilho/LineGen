package LineGenUI.parts;


import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;


public class GenVAMPart {
	private Label maxDepthLabel;
	private Label maxChildPerChoice;
	private Button generateButton;
	private Spinner depthSpinner;
	private Spinner childrenSpinner;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new GridLayout(2,false));
		
		maxDepthLabel = new Label(parent, SWT.NONE);
		maxDepthLabel.setText("VAM Max Depth");
		maxDepthLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		
		maxChildPerChoice = new Label(parent, SWT.NONE);
		maxChildPerChoice.setText("Max Children per node");	
		maxChildPerChoice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		depthSpinner = new Spinner(parent, SWT.BORDER);
		depthSpinner.setMaximum(100);
		depthSpinner.setSelection(5);
		depthSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		childrenSpinner = new Spinner(parent, SWT.BORDER);
		childrenSpinner.setMaximum(100);
		childrenSpinner.setSelection(5);
		childrenSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				
		
		generateButton = new Button(parent, SWT.PUSH);
		generateButton.setText("Generate VAM");
		generateButton.setToolTipText("Generate a random Variability Abstraction Model");
		generateButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 2));


		generateButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e){
				//TODO: Generate just the VAM
				System.out.println("\n Generating VAM ... \n");
			}

		});

	}

}
