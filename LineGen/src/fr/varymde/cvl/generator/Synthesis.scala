package fr.varymde.cvl.generator

import java.io.File
import java.util.HashMap
import java.util.concurrent.Executors
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable.Buffer
import scala.collection.mutable.HashSet
import scala.util.Random
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.Diagnostician
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.emf.emfstore.modelgenerator.InvalidModelExceptionFromDiagronstic
import org.eclipse.uml2.uml.Package
import org.eclipse.uml2.uml.UMLPackage
import org.eclipse.uml2.uml.resource.UMLResource
import org.omg.CVLMetamodelMaster.cvl.VPackage
import fr.varymde.cvl.Derivator
import fr.varymde.cvl.generator.CVLChecker.checkVAM
import fr.varymde.cvl.generator.CVLChecker.checkVRM
import fr.varymde.cvl.generator.CVLGenerator.InvalidVRM
import fr.varymde.cvl.generator.CVLGenerator.generateCVL
import fr.varymde.cvl.generator.CVLGenerator.generateConfig
import fr.varymde.cvl.generator.CVLGenerator.generateVRM
import fr.varymde.cvl.generator.CVLGenerator.ww
import fr.varymde.cvl.vary.CVL2Familiar
import fsm.FSM
import fsm.FsmFactory
import fsm.FsmPackage
import fsm.Transition
import org.eclipse.emf.ecore.resource.ResourceSet
import java.util.Collections
import java.io.IOException
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import scala.collection.mutable.ListBuffer
import fr.varymde.cvl.generator.EcoreInstanceGenerator
import org.eclipse.emf.ecore.EValidator

object Synthesis {

  var validModel = 0
  var inValidModel = 0
  var inValidVAMModel = 0
  var inValidVRMModel = 0
  var ValidModelAfterDerivation = 0
  var counterExample = 0
  
  var numberOfCounterExample = 11;

  val modelDeneratorWidth = 5
  val modelDeneratorDepth = 4
  val numberMaxOfState = 100
  val ExpNumber = Random.nextInt(10000)
  var start = System.currentTimeMillis()
  
  var metaModelURL = ""
  var vamURL = ""
  var baseModelURL = ""
  
  var metaModelResource:Resource = null
  var filterPath:String = ""
    
  
  
  
  def main(args: Array[String]) {
    
    //start = System.currentTimeMillis()
    //runEcore(args)
    runFSM()
    //metaModelURL = "models/fsm.ecore"
    //runAnyEcoreMetamodel(metaModelURL)
//    println("time to find "+numberOfCounterExample+" counter examples: " + (System.currentTimeMillis() - start));
//
//    println("inValidModel " + inValidModel + ";" +
//      "inValidVAMModel " + inValidVAMModel + ";" +
//      "inValidVRMModel " + inValidVRMModel + ";" +
//      "validModelAfterDerivation " + ValidModelAfterDerivation + ";" +
//      "counterExample " + counterExample + ";")
  }
  def runAnyEcoreMetamodel(){
    metaModelResource = loadBaseMetamodel(metaModelURL)
    
    val instanceResource = EcoreInstanceGenerator.createEcoreModelInstance()
    //println(p)
    
  }
  def loadBaseMetamodel(path:String):Resource = {
		    
	val reg = Resource.Factory.Registry.INSTANCE;
	val m = reg.getExtensionToFactoryMap();
	m.put("ecore", new EcoreResourceFactoryImpl());
    //println("ESTE EH O PATH NO SIN: "+ path)
	// Obtain a new resource set
	val resSet = new ResourceSetImpl();
	// Get the resource
	val uri = URI.createFileURI(new File(path).getAbsoluteFile().toString())
	val resource = resSet.getResource(uri, true);
	    var fact = new XMIResourceFactoryImpl

//	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(baseModelExtension, fact);
//	        	EPackage.Registry.INSTANCE.put(FsmPackage.eINSTANCE.getNsURI(), FsmPackage.eINSTANCE);

//	val ep = resource.getContents().get(0).asInstanceOf[EPackage]
	//ep.getNsURI()

//	 if (!EPackage.Registry.INSTANCE.containsKey(FsmPackage.eINSTANCE.getNsURI())) {
//    	EPackage.Registry.INSTANCE.put(FsmPackage.eINSTANCE.getNsURI(), FsmPackage.eINSTANCE);
//    }
	
//	if (!EPackage.Registry.INSTANCE.containsKey(ep.getNsURI())) {
//    	EPackage.Registry.INSTANCE.put(ep.getNsURI(), ep)
//    }
	
	EcoreUtil.resolveAll(resource);
	EcoreUtil.resolveAll(resource.getResourceSet());

	
	return resource;
  }
    
  
  def runFSM() {

    //heckVRM()
    // System.exit(0);

    if (!EPackage.Registry.INSTANCE.containsKey(org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE.getNsURI())) {
    	EPackage.Registry.INSTANCE.put(org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE.getNsURI(), org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE);
    }
    var fact = new XMIResourceFactoryImpl
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("cvl", fact);
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("fsm", fact);
//    if (!EPackage.Registry.INSTANCE.containsKey(OfficeModel.OfficeModelPackage.eINSTANCE.getNsURI())) {
//    	EPackage.Registry.INSTANCE.put(OfficeModel.OfficeModelPackage.eINSTANCE.getNsURI(), OfficeModel.OfficeModelPackage.eINSTANCE);
//    }

    if (!EPackage.Registry.INSTANCE.containsKey(FsmPackage.eINSTANCE.getNsURI())) {
    	EPackage.Registry.INSTANCE.put(FsmPackage.eINSTANCE.getNsURI(), FsmPackage.eINSTANCE);
    }

//    if (!EPackage.Registry.INSTANCE.containsKey(UMLPackage.eNS_URI)) {
//    	EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
//    }
//    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
//    UMLPackage.eINSTANCE.eClass();

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", fact);
//    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("officemodel", fact);
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("fsm", fact);

    //var uri = URI.createURI(args.apply(0));
    //Resource xtextResource = resourceSet.createResource(uri);
    //var res = rs.getResource(uri, true);
    //    var uri1 = URI.createURI("My.officemodel");
    //Resource xtextResource = resourceSet.createResource(uri);
    //    var res1 = rs.getResource(uri1, true);

    var f: fsm.FSM = null
    var v1: VPackage = null
    var oldCounter = counterExample
    
    var exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    
    //for (i <- (counterExample until numberOfCounterExample).par) {
      
    
    while (counterExample < numberOfCounterExample) {
 
    	if (oldCounter != counterExample){
    		println("inValidModel " + inValidModel + ";" +
    				"inValidVAMModel " + inValidVAMModel + ";" +
    				"inValidVROMModel " + inValidVRMModel + ";" +
    				"validModelAfterDerivation " + ValidModelAfterDerivation + ";" +
    				"counterExample " + counterExample + ";")
    		println("time to find "+counterExample+" counter examples: " + (System.currentTimeMillis() - start));

    	}
      oldCounter = counterExample

      var valid = true
      var rs = new ResourceSetImpl()
      var res = rs.createResource(URI.createFileURI("toto.fsm"));

      var f1 = createFSM(res)

      //println(f1.getOwnedState().size())
      try {
        checkFSM(f1)
      } catch {
        case t: NoInitialState => { valid = false; inValidModel = inValidModel + 1 } //;println("FinalEqualInitial")}
        case t: FinalEqualInitial => { valid = false; inValidModel = inValidModel + 1 } //;println("FinalEqualInitial")}
        case t: InderteministFSM => { valid = false; inValidModel = inValidModel + 1 } //;println("InderteministFSM")}
        case t: FSMWithEtatPuit => { valid = false; inValidModel = inValidModel + 1 } //;println("FSMWithEtatPuit")}
        case t: FSMWithStateThatCanNotBeTarget => { valid = false; inValidModel = inValidModel + 1 } //;println("FSMWithStateThatCanNotBeTarget")}
        case t => { valid = false; println("strange" +t) }
      }
      if (valid) {
        f = f1
        var rescvl = rs.createResource(URI.createFileURI("toto.cvl"));

        v1 = generateCVL(rescvl)
        var s = CVL2Familiar.toFamiliar(v1)
        //println(s)
        var vamvalid = checkVAM(s);
        inValidVAMModel = inValidVAMModel + 1
        if (vamvalid) {

          //System.exit(0);

          //TODO Check CVL VAM with familiar    	

          try{
            generateVRM(f, v1)
          
          }catch {
          		case t: InvalidVRM => {inValidVRMModel = inValidVRMModel+1
          	}
          }
          
          if (checkVRM(f,v1)){
          //TODO generate derivation  
          generateConfig(v1)

          //Save current model before derivation
          var f1 = new File("toto.fsm")
          if (f1.exists())
            f1.delete()
          var f2 = new File("toto.cvl")
          if (f2.exists())
            f2.delete()

          res.save(new HashMap)
          rescvl.save(new HashMap)

          //Call Derivation

          var deriv = new Derivator
          deriv.start(v1)

          //if (deriv.domainResources.size()>0) {
          var fsmres = f.eResource().getContents().get(0).asInstanceOf[FSM] //deriv.domainResources.get(0).getContents().get(0).asInstanceOf[FSM];

          var notcouterexample = true
          try {
            var r = checkFSM(fsmres)
          } catch {
            case t: NoInitialState => { notcouterexample = false; counterExample = counterExample + 1; println("NoInitialState") }
            case t: FinalEqualInitial => { notcouterexample = false; counterExample = counterExample + 1; println("FinalEqualInitial") }
            case t: InderteministFSM => { notcouterexample = false; counterExample = counterExample + 1; println("InderteministFSM") }
            case t: FSMWithEtatPuit => { notcouterexample = false; counterExample = counterExample + 1; println("FSMWithEtatPuit") }
            case t: FSMWithStateThatCanNotBeTarget => { notcouterexample = false; counterExample = counterExample + 1; println("FSMWithStateThatCanNotBeTarget") }
            case t => { valid = false; println("strange" + t) }
          }

          if (!notcouterexample) {
            System.err.println("found one counter example")

            var newuri = res.getURI()
            newuri = newuri.trimFileExtension()
            newuri = URI.createURI(newuri.toString() + "_new" + ww)
            newuri = newuri.appendFileExtension(res.getURI().fileExtension())
            res.setURI(newuri)
            res.save(new HashMap)
            copyCandidate("toto_new" + ww + ".fsm", "fsm")

          } else {
            ValidModelAfterDerivation = ValidModelAfterDerivation + 1
          }}
          //}}
        }
      }
      
    }

    //    var p = new ProcessBuilder;
    //   p.command().add("/bin/ls")

    // p.directory(new File("/tmp"))

    //var p1 = p.start()
    //var out = System.out
    //System.setOut(new PrintStream(p1.getOutputStream()))

  }

  def runEcore(args: Array[String]) {

    //heckVRM()
    // System.exit(0);

    if (!EPackage.Registry.INSTANCE.containsKey(org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE.getNsURI())) {
      EPackage.Registry.INSTANCE.put(org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE.getNsURI(), org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE);
    }
    var fact = new XMIResourceFactoryImpl
    var fact1 = new EcoreResourceFactoryImpl

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("cvl", fact);
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", fact1);

        if (!EPackage.Registry.INSTANCE.containsKey(UMLPackage.eNS_URI)) {

      EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
    }
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
    UMLPackage.eINSTANCE.eClass();

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", fact);

    
    if (!EPackage.Registry.INSTANCE.containsKey(EcorePackage.eINSTANCE.getNsURI())) {
      EPackage.Registry.INSTANCE.put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
    }

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", fact);

    //var uri = URI.createURI(args.apply(0));
    //Resource xtextResource = resourceSet.createResource(uri);
    //var res = rs.getResource(uri, true);
    //    var uri1 = URI.createURI("My.officemodel");
    //Resource xtextResource = resourceSet.createResource(uri);
    //    var res1 = rs.getResource(uri1, true);

    var f: EPackage = null
    var v1: VPackage = null
    var oldCounter = counterExample

    while (counterExample < numberOfCounterExample) {

      if (oldCounter != counterExample){
        println("inValidModel " + inValidModel + ";" +
      "inValidVAMModel " + inValidVAMModel + ";" +
      "inValidVRMModel " + inValidVRMModel + ";" +
      "validModelAfterDerivation " + ValidModelAfterDerivation + ";" +
      "counterExample " + counterExample + ";")
    println("time to find "+counterExample+" counter examples: " + (System.currentTimeMillis() - start));

      }
      oldCounter = counterExample

      var valid = true
      var rs = new ResourceSetImpl()
      var res = rs.createResource(URI.createFileURI("toto.ecore"));

      var f1 = createEcoreModel(res)

      if (f1 != null) {
        f = f1

        var rescvl = rs.createResource(URI.createFileURI("toto.cvl"));

        v1 = generateCVL(rescvl)
        var s = CVL2Familiar.toFamiliar(v1)
        //println(s)
        var vamvalid = checkVAM(s);
        inValidVAMModel = inValidVAMModel + 1
        if (vamvalid) {

          var validVRM = true

          try {
            generateVRM(f, v1)

          } catch {
            case t: InvalidVRM => { validVRM = false; inValidVRMModel = inValidVRMModel + 1 }
          }

          if (validVRM) {
            generateConfig(v1)

            //Save current model before derivation
            var f1 = new File("toto.ecore")
            if (f1.exists())
              f1.delete()
            var f2 = new File("toto.cvl")
            if (f2.exists())
              f2.delete()

            res.save(new HashMap)
            rescvl.save(new HashMap)

            //Call Derivation

            //println(f.eResource())
            var deriv = new Derivator
            deriv.start(v1)
            //println(f.eResource())

            //if (deriv.domainResources.size()>0) {

            var fsmres: EPackage = null
            if (f.eResource() != null) {
              if (f.eResource().getContents().get(0) != null) {
                fsmres = f.eResource().getContents().get(0).asInstanceOf[EPackage] //deriv.domainResources.get(0).getContents().get(0).asInstanceOf[FSM];
              } else
                println("no object in resources")
            } else
              println("no resources")

            var notcouterexample = checkEcoreModel(fsmres)
            if (!notcouterexample) {
              counterExample = counterExample + 1;
              System.err.println("found one counter example")

              var newuri = res.getURI()
              //println("uri " + newuri)
              newuri = newuri.trimFileExtension()
              newuri = URI.createURI(newuri.toString() + "_new" + ww)
              newuri = newuri.appendFileExtension(res.getURI().fileExtension())
              res.setURI(newuri)
              res.save(new HashMap)
              copyCandidate("toto_new" + ww + ".ecore", "ecore")

            } else {
              ValidModelAfterDerivation = ValidModelAfterDerivation + 1
            }
          }
          //}
        }
      }
    }

    
    
    //    var p = new ProcessBuilder;
    //   p.command().add("/bin/ls")

    // p.directory(new File("/tmp"))

    //var p1 = p.start()
    //var out = System.out
    //System.setOut(new PrintStream(p1.getOutputStream()))

  }

   def runUML(args: Array[String]) {

    //heckVRM()
    // System.exit(0);

    if (!EPackage.Registry.INSTANCE.containsKey(org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE.getNsURI())) {
      EPackage.Registry.INSTANCE.put(org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE.getNsURI(), org.omg.CVLMetamodelMaster.cvl.CvlPackage.eINSTANCE);
    }
    var fact = new XMIResourceFactoryImpl
    var fact1 = new EcoreResourceFactoryImpl

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("cvl", fact);
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", fact1);

        if (!EPackage.Registry.INSTANCE.containsKey(UMLPackage.eNS_URI)) {

      EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
    }
    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
    UMLPackage.eINSTANCE.eClass();

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", fact);

    
    if (!EPackage.Registry.INSTANCE.containsKey(EcorePackage.eINSTANCE.getNsURI())) {
      EPackage.Registry.INSTANCE.put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
    }

    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", fact);

    //var uri = URI.createURI(args.apply(0));
    //Resource xtextResource = resourceSet.createResource(uri);
    //var res = rs.getResource(uri, true);
    //    var uri1 = URI.createURI("My.officemodel");
    //Resource xtextResource = resourceSet.createResource(uri);
    //    var res1 = rs.getResource(uri1, true);

    var f: EPackage = null
    var v1: VPackage = null
    var oldCounter = counterExample

    while (counterExample < numberOfCounterExample) {

      if (oldCounter != counterExample){
        println("inValidModel " + inValidModel + ";" +
      "inValidVAMModel " + inValidVAMModel + ";" +
      "inValidVRMModel " + inValidVRMModel + ";" +
      "validModelAfterDerivation " + ValidModelAfterDerivation + ";" +
      "counterExample " + counterExample + ";")
    println("time to find "+counterExample+" counter examples: " + (System.currentTimeMillis() - start));

      }
      oldCounter = counterExample

      var valid = true
      var rs = new ResourceSetImpl()
      var res = rs.createResource(URI.createFileURI("toto.uml"));

      var f1 = createEcoreModel(res)

      if (f1 != null) {
        f = f1

        var rescvl = rs.createResource(URI.createFileURI("toto.cvl"));

        v1 = generateCVL(rescvl)
        var s = CVL2Familiar.toFamiliar(v1)
        //println(s)
        var vamvalid = checkVAM(s);
        inValidVAMModel = inValidVAMModel + 1
        if (vamvalid) {

          var validVRM = true

          try {
            generateVRM(f, v1)

          } catch {
            case t: InvalidVRM => { validVRM = false; inValidVRMModel = inValidVRMModel + 1 }
          }

          if (validVRM) {
            generateConfig(v1)

            //Save current model before derivation
            var f1 = new File("toto.ecore")
            if (f1.exists())
              f1.delete()
            var f2 = new File("toto.cvl")
            if (f2.exists())
              f2.delete()

            res.save(new HashMap)
            rescvl.save(new HashMap)

            //Call Derivation

            //println(f.eResource())
            var deriv = new Derivator
            deriv.start(v1)
            //println(f.eResource())

            //if (deriv.domainResources.size()>0) {

            var fsmres: EPackage = null
            if (f.eResource() != null) {
              if (f.eResource().getContents().get(0) != null) {
                fsmres = f.eResource().getContents().get(0).asInstanceOf[EPackage] //deriv.domainResources.get(0).getContents().get(0).asInstanceOf[FSM];
              } else
                println("no object in resources")
            } else
              println("no resources")

            var notcouterexample = checkEcoreModel(fsmres)
            if (!notcouterexample) {
              counterExample = counterExample + 1;
              System.err.println("found one counter example")

              var newuri = res.getURI()
              //println("uri " + newuri)
              newuri = newuri.trimFileExtension()
              newuri = URI.createURI(newuri.toString() + "_new" + ww)
              newuri = newuri.appendFileExtension(res.getURI().fileExtension())
              res.setURI(newuri)
              res.save(new HashMap)
              copyCandidate("toto_new" + ww + ".ecore", "ecore")

            } else {
              ValidModelAfterDerivation = ValidModelAfterDerivation + 1
            }
          }
          //}
        }
      }
    }

    
    
    //    var p = new ProcessBuilder;
    //   p.command().add("/bin/ls")

    // p.directory(new File("/tmp"))

    //var p1 = p.start()
    //var out = System.out
    //System.setOut(new PrintStream(p1.getOutputStream()))

  }
  
  def createFSM(res: Resource): FSM = {

    var f = FsmFactory.eINSTANCE.createFSM();
    res.getContents().add(f)

    var numberOfSate = 1 + Random.nextInt(numberMaxOfState);
    val numberOfTransition = numberOfSate * (1 + Random.nextInt(5))

    var i = 0
    var listStates = Buffer[fsm.State]()
    while (i < numberOfSate) {
      i = i + 1;
      var s = FsmFactory.eINSTANCE.createState();
      s.setName("state" + i);
      listStates.append(s)
      f.getOwnedState().add(s)
      s.setOwningFSM(f)
    }

    var initial = Random.nextInt(listStates.size);
    f.setInitialState(listStates.apply(initial))

    var numberOffin = Random.nextInt(numberOfSate);
    var j = 0
    while (j < numberOffin) {
      j = j + 1;
      var fin = Random.nextInt(listStates.size);
      if (!f.getFinalState().contains(listStates.apply(fin)))
        f.getFinalState().add(listStates.apply(fin))
    }

    var k = 0
    while (k < numberOfTransition) {
      k = k + 1;
      var instate = Random.nextInt(numberOfSate);
      var outstate = Random.nextInt(numberOfSate);
      var t: Transition = FsmFactory.eINSTANCE.createTransition()
      t.setSource(listStates.apply(instate))
      listStates.apply(instate).getIncomingTransition().add(t)
      t.setTarget(listStates.apply(outstate))
      listStates.apply(outstate).getOutgoingTransition().add(t)
      t.setInput("i" + Random.nextInt(1000))
      t.setOutput("o" + Random.nextInt(1000))
    }
    return f;
  }

  def createEcoreModel(res: Resource): EPackage = {

    var m = new org.eclipse.emf.emfstore.modelgenerator.Main

    var p: EPackage = null
    try {
      p = m.generatePackage(modelDeneratorWidth, modelDeneratorDepth);
      res.getContents().add(p)	
    } catch {
      case t: InvalidModelExceptionFromDiagronstic => {
        inValidModel = inValidModel + 1
      }
    }

    return p;

  }
  
    def createUMLModel(res: Resource): Package = {

    var m = new org.eclipse.emf.emfstore.modelgenerator.Main

    var p: Package = null
    try {
      p = m.generateUMLPackage(modelDeneratorWidth, modelDeneratorDepth);
      res.getContents().add(p)
    } catch {
      case t: InvalidModelExceptionFromDiagronstic => {
        inValidModel = inValidModel + 1
      }
    }

    return p;

  }

  def checkFSM(f: FSM): Boolean = {
    var result = true
    
    if (f.getInitialState() == null)
      throw new NoInitialState();
    
    result = result && !f.getFinalState().contains(f.getInitialState())
    if (!result)
      throw new FinalEqualInitial();

    //self.outgoingTransition->forAll(tr1 | self.outgoingTransition->forAll(tr2 |
    //             ( tr2.input=tr1.input ) = (tr1=tr2) ))
    f.getOwnedState().foreach(s => result = result && s.getOutgoingTransition().forall(tr1 => s.getOutgoingTransition().forall(tr2 => (tr2.getInput().equals(tr1.getInput())) == tr2.equals(tr1))))
    if (!result)
      throw new InderteministFSM()

    var incomingState = HashSet[fsm.State]()
    var outgoingState = HashSet[fsm.State]()
    var transition = HashSet[fsm.Transition]()
    f.getOwnedState().foreach(s => {
      s.getIncomingTransition().foreach(tr => transition.add(tr));
      s.getOutgoingTransition().foreach(tr => transition.add(tr))
    })
    transition.foreach(tr => {
      incomingState.add(tr.getSource())
      outgoingState.add(tr.getTarget())
    })

    if (f.getOwnedState().exists(s => !outgoingState.contains(s) && !f.getInitialState().equals(s))) {
      throw new FSMWithStateThatCanNotBeTarget()
    }
    if (f.getOwnedState().exists(s => !incomingState.contains(s) && !f.getFinalState().contains(s))) {
      throw new FSMWithEtatPuit()
    }

    if (result) {
      //println("valid")
      validModel = validModel + 1
    }
    return result;
  }

  def checkEcoreModel(f: EPackage): Boolean = {
    var result = true
    if (f == null)
      return false
    var res = Diagnostician.INSTANCE.validate(f)
    if (res.getChildren().size() > 0) {
      result = false
      res.getChildren().foreach(d1 => { System.err.println(d1.getMessage()) })
    }
    return result;
  }


 
  
  
  
  def copyCandidate(uri: String, extension: String): Unit = {
    var command = new java.util.ArrayList[String]
    command.add("mkdir");
    command.add(ExpNumber + "candidate" + counterExample);
    var probuilder = new ProcessBuilder(command);
    var process = probuilder.start();
    var exitValue = process.waitFor();

    command = new java.util.ArrayList[String]
    command.add("cp");
    command.add("toto.cvl");
    command.add(ExpNumber + "candidate" + counterExample + "/");
    probuilder = new ProcessBuilder(command);
    process = probuilder.start();
    exitValue = process.waitFor();

    command = new java.util.ArrayList[String]
    command.add("cp");
    command.add("toto." + extension);
    command.add(ExpNumber + "candidate" + counterExample + "/");
    probuilder = new ProcessBuilder(command);
    process = probuilder.start();
    exitValue = process.waitFor();

    command = new java.util.ArrayList[String]
    command.add("cp");
    command.add(uri);
    command.add(ExpNumber + "candidate" + counterExample + "/");
    probuilder = new ProcessBuilder(command);
    process = probuilder.start();
    exitValue = process.waitFor();

  }
}

class NoInitialState extends Exception {}
class FinalEqualInitial extends Exception {}
class InderteministFSM extends Exception {}
class FSMWithEtatPuit extends Exception {}
class FSMWithStateThatCanNotBeTarget extends Exception {}

