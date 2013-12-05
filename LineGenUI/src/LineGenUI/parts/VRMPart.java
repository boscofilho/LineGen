package LineGenUI.parts;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class VRMPart {
	private Label linkPercentageLabel;
	private Spinner linkPercentageSpinner;

	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new GridLayout(1,false));
		
		linkPercentageLabel = new Label(parent, SWT.NONE);
		linkPercentageLabel.setText("Link Percentage");
		linkPercentageLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		
		setLinkPercentageSpinner(new Spinner(parent, SWT.BORDER));
		getLinkPercentageSpinner().setMaximum(100);
		getLinkPercentageSpinner().setSelection(70);
		getLinkPercentageSpinner().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
	

	}

	public Spinner getLinkPercentageSpinner() {
		return linkPercentageSpinner;
	}

	public void setLinkPercentageSpinner(Spinner linkPercentageSpinner) {
		this.linkPercentageSpinner = linkPercentageSpinner;
	}



}
