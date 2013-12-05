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

import fr.varymde.cvl.generator.Synthesis;

public class CounterexamplesPart implements Runnable{

	private Label numberCELabel;
	private Button startButton;
	private Spinner numberCESpinner;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new GridLayout(1,false));
		
		numberCELabel = new Label(parent, SWT.NONE);
		numberCELabel.setText("Number of Counterexamples");
		//numberCELabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		
		numberCESpinner = new Spinner(parent, SWT.BORDER);
		numberCESpinner.setMaximum(100);
		numberCESpinner.setSelection(5);
		numberCESpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		startButton = new Button(parent, SWT.PUSH);
		startButton.setText("Start");
		startButton.setToolTipText("Start Counterexample Generation");
		startButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 2));


		startButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e){			
				//TODO: 
				
				//EcoreInstanceGenerator
				//CVLGenerator.percentageOfChoiceTargetByAV
				Synthesis.numberOfCounterExample_$eq(numberCESpinner.getSelection());
				System.out.println(java.lang.Runtime.getRuntime().maxMemory()); 
				//(new Thread(new CounterexamplesPart())).start();
				Synthesis.runFSM();
				Synthesis.runAnyEcoreMetamodel();

			}

		});

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//EcoreInstanceGenerator
		//CVLGenerator.percentageOfChoiceTargetByAV
		//Synthesis.numberOfCounterExample_$eq(numberCESpinner.getSelection());
		//System.out.println(java.lang.Runtime.getRuntime().maxMemory()); 
		//Synthesis.runFSM();
		//Synthesis.runAnyEcoreMetamodel();
	}

}

