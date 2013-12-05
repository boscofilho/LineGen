package LineGenUI.parts;

import java.io.File;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
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

public class LoadBaseModelPart {


	private Label baseModelLabel;
	private Text fileText;
	private File baseModelFile;
	
	private Button openButton;
	@PostConstruct
	public void createComposite(final Composite parent) {
		parent.setLayout(new GridLayout(4,false));
		parent.setSize(parent.getSize().x, 50);
		parent.redraw();
		baseModelLabel = new Label(parent, SWT.NONE);
		baseModelLabel.setText("Base Model Path");
		baseModelLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,4,1 ));
		
		fileText = new Text(parent, SWT.BORDER);
	    fileText.setMessage("Select the base model file");
	    fileText.setEditable(false);
	    fileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		openButton = new Button(parent,SWT.OPEN);
		openButton.setText("Open");
		

	    
		
		openButton.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e){
				FileDialog dialog = new FileDialog(parent.getShell());
				dialog.open();
				fileText.setText(dialog.getFileName());
				
				setBaseModelFile(new File(dialog.getFilterPath(),dialog.getFileName()));
			    Synthesis.baseModelURL_$eq(getBaseModelFile().getAbsolutePath());
			    			
			}
			
		});

	}

	@Focus
	public void setFocus() {
	}

	public File getBaseModelFile() {
		return baseModelFile;
	}

	public void setBaseModelFile(File baseModelFile) {
		this.baseModelFile = baseModelFile;
	}

	
}
