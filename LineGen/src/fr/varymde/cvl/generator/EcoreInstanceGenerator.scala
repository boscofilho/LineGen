package fr.varymde.cvl.generator

import org.eclipse.emf.ecore.EPackage
import scala.collection.mutable.ListBuffer
import org.eclipse.emf.ecore.EObject
import scala.collection.JavaConversions.asScalaBuffer
import org.eclipse.emf.ecore.EClass
import scala.util.Random
import org.eclipse.emf.ecore.util.EcoreUtil
import scala.collection.mutable.Map
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.common.util.URI
import scala.collection.JavaConverters._
import java.util.HashMap
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl
import org.eclipse.emf.ecore.util.Diagnostician




object EcoreInstanceGenerator {
    var maxUpperBound = 20;
    var baseModelExtension = ""

	def createEcoreModelInstance():Resource={
		val ep = Synthesis.metaModelResource.getContents().get(0).asInstanceOf[EPackage]
		val objects: ListBuffer[EObject] = ListBuffer()
		val classContexts:Map[EClass,EClassContext] = Map()
		ep.getEClassifiers().foreach(ec => 
		if (ec.isInstanceOf[EClass]){
			var lowerBound = 0
			var upperBound = 0
			var cc:EClassContext = null
			//calculate how many instances of a given class can be created
			ec.asInstanceOf[EClass].getEReferences().foreach(er =>{
				if(er.getEType().isInstanceOf[EClass]){
					if (classContexts.contains(er.getEType().asInstanceOf[EClass])){
						cc = classContexts.get(er.getEType().asInstanceOf[EClass]).get
								if(cc.lowestBound < er.getLowerBound())
									cc.lowestBound = er.getLowerBound()
									if(cc.highestBound < er.getUpperBound() || er.getUpperBound() == -1)
										cc.highestBound = er.getUpperBound()
										if (er.getUpperBound() == -1)
											cc.highestBound = maxUpperBound // TODO: take as parameter the max for many *
					}
					else
						classContexts += (er.getEType().asInstanceOf[EClass] -> new EClassContext(er.getEType().asInstanceOf[EClass],er.getLowerBound(),er.getUpperBound()))
				}
			if( !classContexts.contains(ec.asInstanceOf[EClass]) )
			  classContexts += (ec.asInstanceOf[EClass] -> new EClassContext(ec.asInstanceOf[EClass],-3,-3))
			})
		})
		//create instances
		classContexts.foreach{case (ec,cc) =>{
				if (cc.highestBound == -3 && cc.lowestBound == -3)
					objects += EcoreUtil.create(ec)
				else{
					var randomUpper = Random.nextInt((cc.highestBound - cc.lowestBound + 1)) + cc.lowestBound
					//println("Para Classe: " + ec.getName() + "High " + cc.highestBound + "Low +" + cc.lowestBound + " Random " + randomUpper)
					for(i <- 0 until randomUpper) 
						objects += EcoreUtil.create(ec)
				}
			}
		}
		//println("OBJECTS: " )
		objects.foreach(println)
		
		//Put objects into structure
		objects.foreach(o =>{
			o.eClass().getEReferences().foreach(er =>{
				val fo = objects.filter(_.eClass() == er.getEType().asInstanceOf[EClass])
						if(!fo.isEmpty){
							if(er.isMany()){
								o.eGet(er).asInstanceOf[EList[EObject]].add(fo(Random.nextInt(fo.length)))
							}
							else{
								//println(" PRA TRANS: " + o.eClass().getName() + "POSSIBILIDADES:" + fo(Random.nextInt(fo.length)))
								o.eSet(er, fo(Random.nextInt(fo.length)))
							}
//							println(" Conteudo da classe " + o.eClass().getName())
//							println(o.eContents())
						}
			}
		    )
		  
		}
		
		)
		var rs = new ResourceSetImpl()	
	    rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(baseModelExtension, new XMLResourceFactoryImpl())
	    var res = rs.createResource(URI.createFileURI(Synthesis.filterPath + "/BaseModel." + baseModelExtension))
	    
	    var lbRoots:ListBuffer[EObject] = new ListBuffer()
	    //find root
	    objects.foreach( o => {
	      val root = findInstanceRoot(o)
	      if (root!=null && !lbRoots.contains(root))
	    	  lbRoots += findInstanceRoot(o)
	      })
	    //println("THE ROOT: " + lbRoots)    
	    lbRoots.foreach(root => if(root != null)res.getContents().add(root))

	    
	   // Diagnostician.INSTANCE.validate
	    var resul = Diagnostician.INSTANCE.validate(lbRoots(0))
	    if (resul.getChildren().size() > 0) {
	    	resul.getChildren().foreach(d1 => { System.err.println(d1.getMessage()) })
	    }
		//println(resul)
		res.save(new HashMap)
		
		println("\n BaseModel." + baseModelExtension + " succesfully generated:\n " + res.getURI().toString())
		
		res
	}
	def findInstanceRoot(eo:EObject):EObject={
	  if(eo.eContainer() != null)
	    findInstanceRoot(eo.eContainer())
	  else
	    eo
	}
	
}

case class EClassContext(eclass:EClass, var lowestBound:Int = -3, var highestBound:Int = -3)