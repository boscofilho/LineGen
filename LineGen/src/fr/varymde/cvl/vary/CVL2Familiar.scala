package fr.varymde.cvl.vary
import org.eclipse.emf.ecore.EObject
import org.omg.CVLMetamodelMaster.cvl.VPackage
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import org.omg.CVLMetamodelMaster.cvl.ObjectExistence
import org.omg.CVLMetamodelMaster.cvl.ObjectSubstitution
import org.omg.CVLMetamodelMaster.cvl.FragmentSubstitution
import org.omg.CVLMetamodelMaster.cvl.LinkExistence
import org.eclipse.emf.ecore.resource.Resource
import org.omg.CVLMetamodelMaster.cvl.ParametricSlotAssignmet
import org.omg.CVLMetamodelMaster.cvl.OpaqueVariationPoint
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.omg.CVLMetamodelMaster.cvl.LinkAssignment
import fd2assets._
import org.eclipse.uml2.uml.UMLPackage
import org.eclipse.uml2.uml.resource.UMLResource
import org.eclipse.emf.common.util.Diagnostic
import org.eclipse.emf.ecore.util.Diagnostician
import org.omg.CVLMetamodelMaster.cvl.CompositeVariationPoint

object CVL2Familiar {


    def main(args: Array[String]) {

    if (args.size < 1) {
      println("please  provide the uri of the cvl")
      
    }
    else
      println( toFamiliarfromUri(args.apply(0)))
    }


    
    def loadCVLModel(cvluri : String):org.eclipse.emf.ecore.resource.Resource = {
 if (!EPackage.Registry.INSTANCE.containsKey(org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE.getNsURI())) {
      EPackage.Registry.INSTANCE.put(org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE.getNsURI(), org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE);
    }
    var fact = new XMIResourceFactoryImpl
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("cvl", fact);
    if (!EPackage.Registry.INSTANCE.containsKey(OfficeModel.OfficeModelPackage.eINSTANCE.getNsURI())) {
      EPackage.Registry.INSTANCE.put(OfficeModel.OfficeModelPackage.eINSTANCE.getNsURI(), OfficeModel.OfficeModelPackage.eINSTANCE);
    }

    if (!EPackage.Registry.INSTANCE.containsKey(UMLPackage.eNS_URI)) {

      EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
    }
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
    UMLPackage.eINSTANCE.eClass();

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", fact);

    var rs = new ResourceSetImpl()
    var uri = URI.createURI(cvluri);
    //Resource xtextResource = resourceSet.createResource(uri);
    var res = rs.getResource(uri, true);
    //    var uri1 = URI.createURI("My.officemodel");
    //Resource xtextResource = resourceSet.createResource(uri);
    //    var res1 = rs.getResource(uri1, true);

    EcoreUtil.resolveAll(res)
    EcoreUtil.resolveAll(res.getResourceSet())
    	return res;
    
    }
    

     def checkDomainModelsfromURI(cvluri:String) :  java.util.List[Diagnostic] = {
    		checkDomainModels(loadCVLModel(cvluri).getContents().get(0).asInstanceOf[VPackage])

     }
 def checkDomainModels(root:VPackage) :  java.util.List[Diagnostic] = {
   
    /*res.getResourceSet().getResources.select(e =>
	      e.getURI() != null  && e.getURI().toString() != null && e.getURI().toFileString().contains("officemodel")).first.load(new java.util.HashMap[Any,Any]())
   */
   
    var roots = new java.util.ArrayList[EObject]()
    
    
    val l = new CVLChecker
    l.findDomainModelElement(root)
    
    
    l.l.foreach(e1 =>  {
      var r = l.findRoot(e1) 
      if (!roots.contains(r)) 
        roots.add(r) })
    //TODO find domain root
    var diags = new java.util.ArrayList[Diagnostic]()
      
    roots.foreach(r =>     diags.add (Diagnostician.INSTANCE.validate(r) ))
    return diags
 }
    
  def toFamiliar(root:VPackage) : String  ={


    var familiar = new FamiliarPrinter
    return  familiar.start(root)
  }
 


  def toFamiliarfromUri(cvluri:String) : String  ={

		return   toFamiliar(loadCVLModel(cvluri).getContents().get(0).asInstanceOf[VPackage])
  }
 
}


class FamiliarPrinter {

  var domainResources: java.util.List[Resource] = new java.util.ArrayList[Resource]()

 // var stdio: _root_.k2.io.StdIOClass = new _root_.k2.io.StdIOClass()

  var res: StringBuilder = new StringBuilder
  var res2: StringBuilder = new StringBuilder

  def start(o: VPackage): String = {

    res.append("FM (")

    traverse(o, null)
    res.append(res2.toString)

    res.append(" )")

    return res.toString

  }

  def traverse(o: EObject, parent: org.omg.CVLMetamodelMaster.cvl.Choice): Unit = {
    o match {
      case e: VPackage => {
        e.getPackageElement().foreach(e1 => traverse(e1, null))

      }
      case e: org.omg.CVLMetamodelMaster.cvl.Choice => {

        if (e.getChild().filter(e1=> e1.isInstanceOf[org.omg.CVLMetamodelMaster.cvl.Choice]).size() > 0) {
        res.append(e.getName() + " : ")

          var lower = 0
          var upper = e.getChild().size()
          if (e.getGroupMultiplicity() != null) {
            lower = e.getGroupMultiplicity().getLower()
            upper = e.getGroupMultiplicity().getUpper()
          }
          var lchoicenames: java.util.List[String] = new java.util.ArrayList[String]()

          e.getChild().foreach(e => 
            if (e.isInstanceOf[org.omg.CVLMetamodelMaster.cvl.Choice]) {
          
            lchoicenames.add(e.getName())
            }
            )
          
          
          val choicename = lchoicenames.toArray[String](Array[java.lang.String]())

          if (upper.equals(lower)) {
            res.append(choicename.mkString(" "))
          } else if (lower == 1) {
            if (choicename.size == 1)
            res.append(choicename.mkString(" | "))
            
            else
            {
            res.append("(")
            res.append(choicename.mkString(" | "))
            res.append(")")
            }
            } else if (lower > 1) {
            res.append("(")
            res.append(choicename.mkString(" | "))
            res.append(")+")
          } else if (lower == 0 && upper > 1) {
            //res.append("(")
            e.getChild().foreach(e => if (e.isInstanceOf[org.omg.CVLMetamodelMaster.cvl.Choice]) { res.append("[" + e.getName() + "]")})
            //res.append(")")
          } else if (lower == 0 && upper == 1) {
            if (choicename.size==1){
            res.append("[")
            res.append(choicename.mkString(" | "))
            res.append("]")
              
            }else{
            res.append("(")
            res.append(choicename.mkString(" | "))
            res.append(")?")
            }
          }
          res.append("; ")
        }
        if (e.isIsImpliedByParent() && parent != null) {
          res2.append(parent.getName() + " -> " + e.getName() + "; ")
        }
        e.getChild().foreach(e1 => traverse(e1, e))
      }
      case _ => {}
    }
  }

}

class CVLChecker{
  var l = new java.util.ArrayList[EObject]()
  
  
  def findDomainModelElement(o: EObject): Unit = {
		  
    o match {
      case e: VPackage =>{
        e.getPackageElement().foreach(e1 => findDomainModelElement(e1))
        
      }
      case e: ObjectExistence => {
        e.getOptionalObject().foreach(e2=> l.add(e2.getReference()))
    	
      
      }
      case e: ParametricSlotAssignmet => {

      }

      case e: ObjectSubstitution => {
        l.add(e.getPlacementObject().getReference())
        l.add(e.getReplacementObject().getReference())
      }
      case e: FragmentSubstitution => {
    	  

      }
      case e: LinkExistence => {
        l.add(e.getOptionalLink().getReference())
      }
      case e: LinkAssignment => {

        l.add(e.getLink().getReference())
        l.add(e.getNewEnd().getReference())

      }
      case e:CompositeVariationPoint=>{
        e.getChildren().foreach(e1=> findDomainModelElement(e1))
        
      }
      case e: OpaqueVariationPoint => {
        e.getSourceObject().foreach(e2=>l.add(e2.getReference()) )
      }
      case _ => {}

    }

  }
  
        def findRoot(e: EObject): EObject = {
    if (e.eContainer() == null || e.eContainer().isInstanceOf[Resource])
      return e
    else
      return findRoot(e.eContainer())
  }


  
}