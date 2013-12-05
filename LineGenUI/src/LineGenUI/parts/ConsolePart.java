package LineGenUI.parts;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;


public class ConsolePart {
	
	private Text text;

	@PostConstruct
	public void createComposite(final Composite parent) {
		text = new Text(parent, SWT.READ_ONLY | SWT.MULTI |SWT.H_SCROLL | SWT.V_SCROLL);
		  OutputStream out = new OutputStream() {
		   @Override
		   public void write(int b) throws IOException {
		    if (text.isDisposed()) return;
		    text.append(String.valueOf((char) b));
		   }
		  };
		  final PrintStream oldOut = System.out;
		  System.setOut(new PrintStream(out));
		  text.addDisposeListener(new DisposeListener() {
		   public void widgetDisposed(DisposeEvent e) {
		    System.setOut(oldOut);
		   }
		  });

	}


}
