/**
 * @author jbferrei
 */
package fr.varymde.cvl.generator

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable.Buffer
import scala.util.Random

import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.emfstore.modelgenerator.common.ModelGeneratorUtil
import org.omg.CVLMetamodelMaster.cvl.Choice
import org.omg.CVLMetamodelMaster.cvl.ChoiceResolutuion
import org.omg.CVLMetamodelMaster.cvl.CvlFactory
import org.omg.CVLMetamodelMaster.cvl.VPackage

import fsm.FSM


object CVLGenerator {

var vamMaxDepth = 5
var vamMaxChoiceByLevel = 5 
var percentageOfChoiceTargetByAVP = 0.3
var chi = 1
var level = 0;
var ww = 0;

  
def generateConfig(root: VPackage) = {

    var p2 = root.getPackageElement().get(0).asInstanceOf[VPackage]

    var p1: VPackage = CvlFactory.eINSTANCE.createVPackage()
    p1.setName("Resolution")
    root.getPackageElement().add(p1)

    p2.getPackageElement().foreach(s => p1.getPackageElement().add(generateChoiceResolution(null, s.asInstanceOf[Choice], p1)))

  }

  def generateChoiceResolution(cp: ChoiceResolutuion, c: Choice, vc: VPackage): ChoiceResolutuion = {

    var cr = CvlFactory.eINSTANCE.createChoiceResolutuion()
    cr.setResolvedChoice(c)
    cr.setName("cr_" + c.getName())
    cr.setDecision(Math.random > 0.5)
    cr.setResolvedVSpec(c)
    vc.getPackageElement().add(cr)
    if (cp != null)
      cp.getChild().add(cr)
    c.getChild().foreach(c1 => generateChoiceResolution(cr, c1.asInstanceOf[Choice], vc))
    return cr
  }

  def fixMinimumAndMaximumChoiceResolution(cr: ChoiceResolutuion, c: Choice, vc: VPackage): Unit = {
    //	  cr.getChild().
    var numberResolvedToTrue = 0

    var crtrue = Buffer[ChoiceResolutuion]()
    var crfalse = Buffer[ChoiceResolutuion]()
    cr.getChild().foreach(cr1 => {

      if (cr1.asInstanceOf[ChoiceResolutuion].isDecision()) {
        numberResolvedToTrue = numberResolvedToTrue + 1
        crtrue.append(cr1.asInstanceOf[ChoiceResolutuion])
      } else {
        crfalse.append(cr1.asInstanceOf[ChoiceResolutuion])
      }
    })

    if (numberResolvedToTrue < c.getGroupMultiplicity().getLower()) {
      var n = 0;
      while (n < c.getGroupMultiplicity().getLower() - numberResolvedToTrue) {
        crfalse.apply(n).setDecision(true)
        n = n + 1
      }
    }
    if (numberResolvedToTrue > c.getGroupMultiplicity().getUpper()) {
      var n = 0;
      while (n < numberResolvedToTrue - c.getGroupMultiplicity().getUpper()) {
        crtrue.apply(n).setDecision(false)
        n = n + 1
      }

    }

  }
 /**
   * General idea:
   * For each Choice (Decide if it is pointed by a VP)
   * Generate the type of VP
   * For each VP (decide if it targets only a state, only a transition, a set of state and transition)
   */


  def generateVRM(f: EObject, root: VPackage) = {

    var p2 = root.getPackageElement().get(0).asInstanceOf[VPackage]

    var p1: VPackage = CvlFactory.eINSTANCE.createVPackage()
    p1.setName("VRM")
    root.getPackageElement().add(p1)

    if (f.isInstanceOf[FSM])
      p2.getPackageElement().foreach(s => generateVP(s.asInstanceOf[Choice], p1, f.asInstanceOf[FSM]))
    else {
      var maps: java.util.Map[EClass, java.util.List[EObject]] = ModelGeneratorUtil.getAllClassesAndObjects(f)
      var eclassList = ModelGeneratorUtil.getAllEClasses()
      p2.getPackageElement().foreach(s => generateVP(s.asInstanceOf[Choice], p1, f.asInstanceOf[EPackage], maps, eclassList))
    }

  }

  def generateVP(c: Choice, vrm: VPackage, f: FSM): Unit = {
    if (Math.random < percentageOfChoiceTargetByAVP) {

      var vRMtype = Random.nextInt(4)
      //ObjectExistence
      if (vRMtype < 2) {
        var existence = CvlFactory.eINSTANCE.createObjectExistence()
        vrm.getPackageElement().add(existence)
        existence.getBindingVspec().add(c)
        existence.getBindingChoice().add(c)
        existence.setName("vp_" + c.getName())

        var numberObOfObject = Random.nextInt(20)
        if (numberObOfObject < 5) {
          var handle = CvlFactory.eINSTANCE.createObjectHandle()
          handle.setReference(f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())))
          existence.getOptionalObject().add(handle)
        } else if (numberObOfObject < 10) {
          var tr = f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())).getIncomingTransition()

           if (tr.size()==0)
                throw new InvalidVRM
                var handle = CvlFactory.eINSTANCE.createObjectHandle()
          handle.setReference(tr.apply(Random.nextInt(tr.size())))
          existence.getOptionalObject().add(handle)
        } else if (numberObOfObject < 15) {
          var handle1 = CvlFactory.eINSTANCE.createObjectHandle()
          handle1.setReference(f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())))
          existence.getOptionalObject().add(handle1)
          var tr = f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())).getIncomingTransition()
              if (tr.size()==0)
                throw new InvalidVRM

          var handle = CvlFactory.eINSTANCE.createObjectHandle()
          handle.setReference(tr.apply(Random.nextInt(tr.size())))
          existence.getOptionalObject().add(handle)
        } else {
          var numberofHandle = Random.nextInt(20)
          var m = 0
          while (m < numberofHandle) {
            m = m + 1
            if (Random.nextBoolean) {
              var handle = CvlFactory.eINSTANCE.createObjectHandle()
              handle.setReference(f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())))
              existence.getOptionalObject().add(handle)
            } else {
              var tr = f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())).getIncomingTransition()
              var handle = CvlFactory.eINSTANCE.createObjectHandle()
              if (tr.size()==0)
                throw new InvalidVRM

              handle.setReference(tr.apply(Random.nextInt(tr.size())))
              existence.getOptionalObject().add(handle)
            }
          }
        }

      } //ObjectSubstitution
      else if (vRMtype < 3) {
        var subs = CvlFactory.eINSTANCE.createObjectSubstitution()
        vrm.getPackageElement().add(subs)

        subs.getBindingVspec().add(c)
        subs.getBindingChoice().add(c)
        subs.setName("vp_" + c.getName())

        if (Random.nextBoolean) {
          var handle = CvlFactory.eINSTANCE.createObjectHandle()
          handle.setReference(f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())))
          subs.setPlacementObject(handle)
          var handle1 = CvlFactory.eINSTANCE.createObjectHandle()
          handle1.setReference(f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())))
          subs.setReplacementObject(handle1)
        } else {
          if (Random.nextBoolean) {
            var tr = f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())).getIncomingTransition()
            var tr1 = f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())).getIncomingTransition()
              if (tr.size()==0 | tr1.size()==0)
                throw new InvalidVRM

            
            var handle = CvlFactory.eINSTANCE.createObjectHandle()
            handle.setReference(tr.get(Random.nextInt(tr.size())))
            subs.setPlacementObject(handle)
            var handle1 = CvlFactory.eINSTANCE.createObjectHandle()
            handle1.setReference(tr1.get(Random.nextInt(tr1.size())))
            subs.setReplacementObject(handle1)

          } else {
            var tr = f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())).getIncomingTransition()
            var tr1 = f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())).getOutgoingTransition()
              if (tr.size()==0 | tr1.size()==0)
                throw new InvalidVRM

            var handle = CvlFactory.eINSTANCE.createObjectHandle()
            handle.setReference(tr.get(Random.nextInt(tr.size())))
            subs.setPlacementObject(handle)
            var handle1 = CvlFactory.eINSTANCE.createObjectHandle()
            handle1.setReference(tr1.get(Random.nextInt(tr1.size())))
            subs.setReplacementObject(handle1)
          }
        }

      } //Linksubstitution
      else {
        var subs = CvlFactory.eINSTANCE.createLinkExistence()
        vrm.getPackageElement().add(subs)
        subs.getBindingVspec().add(c)
        subs.getBindingChoice().add(c)
        subs.setName("vp_" + c.getName())
        var handle = CvlFactory.eINSTANCE.createLinkHandle()
        handle.setReference(f.getOwnedState().get(Random.nextInt(f.getOwnedState().size())))
        if (Random.nextBoolean) {
          handle.setMOFRef("incomingTransition")
        } else {
          handle.setMOFRef("outgoingTransition")
        }
        subs.setOptionalLink(handle)
      }
    }
    c.getChild().foreach(c1 => generateVP(c1.asInstanceOf[Choice], vrm, f))
  }

  def generateVP(c: Choice, vrm: VPackage, f: EObject, maps: java.util.Map[EClass, java.util.List[EObject]], eclassList: java.util.List[EClass]): Unit = {
    if (Math.random < percentageOfChoiceTargetByAVP) {

      var vRMtype = Math.random * 4
      //ObjectExistence
      if (vRMtype < 2) {
        var existence = CvlFactory.eINSTANCE.createObjectExistence()
        vrm.getPackageElement().add(existence)
        existence.getBindingVspec().add(c)
        existence.getBindingChoice().add(c)
        existence.setName("vp_" + c.getName())

        var numberObOfObject = 1 + Random.nextInt(5)

        while (numberObOfObject > 0) {

          var tor: java.util.List[EObject] = null
          var t = 0;
          while ((tor == null || tor.size() == 0) && t < 1000) {
            var cla = eclassList.get(Random.nextInt(eclassList.size()))
            tor = maps.get(cla)
            t = t + 1
          }
          if (tor != null && tor.size() > 0) {
            var handle = CvlFactory.eINSTANCE.createObjectHandle()
            handle.setReference(tor.get(Random.nextInt(tor.size())))
            existence.getOptionalObject().add(handle)
          } else {
            throw new InvalidVRM
          }
          numberObOfObject = numberObOfObject - 1

        }

      } //ObjectSubstitution
      else if (vRMtype < 3) {
        var subs = CvlFactory.eINSTANCE.createObjectSubstitution()
        vrm.getPackageElement().add(subs)

        subs.getBindingVspec().add(c)
        subs.getBindingChoice().add(c)
        subs.setName("vp_" + c.getName())

        var tor: java.util.List[EObject] = null
        var t = 0;
        while ((tor == null || tor.size() < 2) && t < 1000) {
          var cla = eclassList.get(Random.nextInt(eclassList.size()))
          tor = maps.get(cla)
          t = t + 1
        }
        if (tor != null && tor.size() > 1) {
          var handle = CvlFactory.eINSTANCE.createObjectHandle()
          handle.setReference(tor.get(Random.nextInt(tor.size())))
          subs.setPlacementObject(handle)
          var rehandle = CvlFactory.eINSTANCE.createObjectHandle()
          t = 0
          var replacement = tor.get(Random.nextInt(tor.size()))
          while (t < 1000 && replacement.equals(handle.getReference())) {
            replacement = tor.get(Random.nextInt(tor.size()))
            t = t + 1
          }
          if (!replacement.equals(handle.getReference())) {
            rehandle.setReference(replacement)
            subs.setReplacementObject(rehandle)
          } else
            throw new InvalidVRM

        } else {
          throw new InvalidVRM
        }

      } //Linksubstitution
      else {
        var subs = CvlFactory.eINSTANCE.createLinkExistence()
        vrm.getPackageElement().add(subs)
        subs.getBindingVspec().add(c)
        subs.getBindingChoice().add(c)
        subs.setName("vp_" + c.getName())
        var handle = CvlFactory.eINSTANCE.createLinkHandle()
        //handle.setReference(f.getOwnedState().get(Math.floor(Math.random * f.getOwnedState().size()).toInt))
        //TODO select an object

        var tor: java.util.List[EObject] = null
        var t = 0;
        while ((tor == null || tor.size() == 0) && t < 1000) {
          var cla = eclassList.get(Random.nextInt(eclassList.size()))
          tor = maps.get(cla)
          t = t + 1
        }
        if (tor != null && tor.size() > 0) {
          handle.setReference(tor.get(Random.nextInt(tor.size())))
        } else {
          throw new InvalidVRM
        }
        var refs = ModelGeneratorUtil.getValidReferences(handle.getReference(), new java.util.HashSet, false)
        if (refs.size() == 0)
          throw new InvalidVRM
        handle.setMOFRef(refs.get(Random.nextInt(refs.size())).getName())
        subs.setOptionalLink(handle)
      }
    }
    c.getChild().foreach(c1 => generateVP(c1.asInstanceOf[Choice], vrm, f, maps, eclassList))

  }
  def generateCVL(rescvl: Resource): VPackage = {
    var numberOfVamMaxDeepth = Random.nextInt(vamMaxDepth);
    var p: VPackage = CvlFactory.eINSTANCE.createVPackage()
    p.setName("ThalesTestCase_" + ww)
    rescvl.getContents().add(p)
    ww = ww + 1
    var p1: VPackage = CvlFactory.eINSTANCE.createVPackage()
    p1.setName("VAM")
    p.getPackageElement().add(p1)

    var c1: Choice = CvlFactory.eINSTANCE.createChoice()
    c1.setName("Root")
    c1.setDefaultResolution(Random.nextBoolean)
    c1.setIsImpliedByParent(Random.nextBoolean)

    p1.getPackageElement().add(c1)

    generateChoidChildren(c1, numberOfVamMaxDeepth)
    return p
  }

  def generateChoidChildren(c1: Choice, numberOfVamMaxDeepth: Int): Unit = {
    level = level + 1
    var numberOfChoiceForThisLevel = Random.nextInt(vamMaxChoiceByLevel);
    var lowerinthislevel = Random.nextInt(numberOfChoiceForThisLevel+1);
    var upperinthislevel = Random.nextInt(numberOfChoiceForThisLevel+1)+1;
    var m = CvlFactory.eINSTANCE.createMultiplicityInterval()

    if (upperinthislevel < lowerinthislevel) {
      var temp = upperinthislevel
      upperinthislevel = lowerinthislevel
      lowerinthislevel = temp
    }
    m.setLower(lowerinthislevel)
    m.setUpper(upperinthislevel)
    c1.setGroupMultiplicity(m)

    var i = 0
    while (i < numberOfChoiceForThisLevel) {
      i = i + 1;
      var c = CvlFactory.eINSTANCE.createChoice();
      c.setName("ch" + chi);
      chi = chi + 1
      c.setDefaultResolution(Random.nextBoolean)
      c.setIsImpliedByParent(Random.nextBoolean)
      c1.getChild().add(c)
    }

    if (level < numberOfVamMaxDeepth)
      c1.getChild().foreach(ch => generateChoidChildren(ch.asInstanceOf[Choice], numberOfVamMaxDeepth))
    level = level - 1

  }
class InvalidVRM extends Exception {}
}