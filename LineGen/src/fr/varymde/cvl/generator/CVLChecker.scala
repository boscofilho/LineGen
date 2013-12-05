/**
 * @author jbferrei
 */
package fr.varymde.cvl.generator

import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.PrintWriter

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.bufferAsJavaList

import org.omg.CVLMetamodelMaster.cvl.ObjectExistence
import org.omg.CVLMetamodelMaster.cvl.ObjectSubstitution
import org.omg.CVLMetamodelMaster.cvl.VPackage

import fsm.FSM

object CVLChecker {
  def checkVAM(s: String): Boolean = {

    if (s.equals("FM ( )"))
      return false
    var f = File.createTempFile("fml", ".fml");
    if (f.exists())
      f.delete();
    var fo = new FileOutputStream(f);
    var p = new PrintWriter(fo);
    p.print("fm2 = ");
//    println("s=" +s)
    p.println(s);
    p.println("println isValid fm2");
    p.println("println counting fm2");
    p.println("println deads fm2");
    p.println("println cores fm2");
    p.println("exit");
    p.flush();
    p.close();
    fo.close();

    //println(f.getAbsolutePath());
    //var ommand = {"/usr/bin/java"; "-version"};// -jar /home/barais/workspaces/VARYMDE/cvl/cvlderivation/lib/FML-1.0.3.jar " + f.getAbsolutePath();
    var command = new java.util.ArrayList[String]
    command.add("java");
    command.add("-jar");
    command.add("lib/FML.jar");
    command.add(f.getAbsolutePath());

    var probuilder = new ProcessBuilder(command);

    //You can set up your work directory
    //     probuilder.directory(new File("/tmp"));

    var process = probuilder.start();

    var out1 = new StringBuilder()
/*
    var ise = process.getErrorStream();
    var isre = new InputStreamReader(ise);
    var bre = new BufferedReader(isre);
    var liner: String = null;
    liner = bre.readLine()

    while (liner != null) {
      out1.append(liner)
      liner = bre.readLine()
    }*/
   
    //Read out dir output
    var is = process.getInputStream();
    var isr = new InputStreamReader(is);
    var br = new BufferedReader(isr);
    var line: String = null;
    line = br.readLine()
    
    while (line != null) {
      if (line.contains("parsing errors")){
        process.destroy();
        println(line)
        
        println("s=" +s)
        
         System.err.println("exit to check with parsing error")

        return false;
      }
      out1.append(line)
      line = br.readLine()
    }
    
    //Wait to get exit value
    var exitValue = process.waitFor();
    //System.out.println("\n\nExit output is " + out1.toString);

    return out1.toString.startsWith("true")

  }


  def checkVRM(f:FSM,root: VPackage):Boolean = {

    var p2 = root.getPackageElement().get(0).asInstanceOf[VPackage]
    var vrm = root.getPackageElement().filter(e=> e.getName().equals("VRM")).get(0)
    vrm.asInstanceOf[VPackage].getPackageElement().foreach(e=> 
      {
        if (e.isInstanceOf[ObjectSubstitution]){
    	  var p = e.asInstanceOf[ObjectSubstitution].getPlacementObject().getReference()
    	  var p1 = e.asInstanceOf[ObjectSubstitution].getReplacementObject().getReference()
    	  if ((f.getFinalState().contains(p) && f.getInitialState().equals(p1)) || (f.getFinalState().contains(p1) && f.getInitialState().equals(p))){
    	    return false;
    	  }
    	  
      }else if 
        (e.isInstanceOf[ObjectExistence]){
        
    	  e.asInstanceOf[ObjectExistence].getOptionalObject().foreach(p=> {var p1 = p.getReference()
    	  if (f.getInitialState().equals(p1)){
    	    return false;
    	  }
      })
      } 
      }
    )
    return true
  }
}