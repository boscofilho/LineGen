package fr.varymde.cvl
import org.eclipse.emf.ecore.EObject
import org.omg.CVLMetamodelMaster.cvl.Choice
import org.omg.CVLMetamodelMaster.cvl.VPackage
import org.omg.CVLMetamodelMaster.cvl.ChoiceResolutuion
import org.omg.CVLMetamodelMaster.cvl.impl.CvlFactoryImpl
import org.omg.CVLMetamodelMaster.cvl.CvlFactory
import k2.standard.JavaConversions._

object Selection {

  
  
  def start(o:VPackage)={
    if (checkNoChoiceResolution(o))
      o.getPackageElement().each(p=> generateResolution(p,o,null))    
           
  }
  

  def checkNoChoiceResolution(o:VPackage)  = {
		 true
  }
  
  def generateResolution(o:EObject, root:VPackage,parent:ChoiceResolutuion):Unit={
    o match {
    case e:Choice => { 
      var ls : ChoiceResolutuion = CvlFactory.eINSTANCE.createChoiceResolutuion()
      ls.setResolvedChoice(e)
      if (root!=null)
    	  root.getPackageElement().add(ls)
      else
    	  parent.getChild().add(ls)
      e.getChild().each(ch=> generateResolution(ch,null,ls))

    }
    
  }
  
  } 
}