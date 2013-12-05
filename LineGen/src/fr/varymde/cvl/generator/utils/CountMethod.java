package fr.varymde.cvl.generator.utils;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreValidator;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.util.UMLValidator;


public class CountMethod {

	
	public static void main(String[] args) {
		System.err.println(EcoreValidator.class.getDeclaredMethods().length);
		int i=0;	
		for (java.lang.reflect.Method m : UMLValidator.class.getDeclaredMethods()){
				
				if (m.getName().startsWith("validate")){
					i++;
				}
			}
			
		int j = 0;
		System.err.println(UMLPackage.eINSTANCE.getEClassifiers().size());
		for(EClassifier e : UMLPackage.eINSTANCE.getEClassifiers()){
			if (e instanceof EClass)
				j++;
			
		}
		//System.err.println(UMLPackage.eINSTANCE.getESuperPackage());
		System.err.println(i);
		System.err.println(j);
		
		
		
//		UMLPackage.eINSTANCE.getEClassifiers()
	}
	
	public static String lourd() {
		return "Vincent is heavy!!!!!!";
	}
}
