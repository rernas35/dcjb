package org.devside.ee.maven_dcjb_aspect_plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.classworlds.ClassRealm;
import org.devside.ee.dcjb.client.annotation.JumpNextOne;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;


@Mojo(name="applyClientCode")
public class JumpNextAspect extends AbstractMojo{
	
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;

	public  void execute() {
		Class[] defaultExceptions = {Exception.class/*,SocketException.class,IOException.class*/};
		boolean aspectApplied = false;
		
		CtClass cc = null;
		try {
			ClassPool pool = ClassPool.getDefault();
			
			System.out.println("asdasdsad");
			pool.appendClassPath(project.getBuild().getDirectory() + File.separator + "classes");
			cc = pool.get("com.illucit.ejbremote.TestEJB");
			
			for (CtMethod m : cc.getMethods()) {
				JumpNextOne jumpNextOne = (JumpNextOne) m.getAnnotation(JumpNextOne.class);
				if (jumpNextOne != null){
					List<Class> exceptions =new ArrayList<Class>(); 
					exceptions.addAll(Arrays.asList(jumpNextOne.exceptions()));
					exceptions.addAll(Arrays.asList(defaultExceptions));
					
					String methodName = m.getName();
					m.setName(m.getName() + "Internal");
					m.setModifiers(AccessFlag.PRIVATE);
					int i=0;
					String paramStr = "";
					String varStr = "";
					for(CtClass t :  m.getParameterTypes()){
						if (i > 0 ){
							paramStr += ",";
							varStr += ",";
						}
						String v= "p" + i;
						paramStr += t.getName() + " " + v;
						varStr += v;
						i++;
					}
					
					
					
					String signature =  m.getReturnType().getName() + " " + methodName + "(" + paramStr + ")" ;
					boolean isVoid = m.getReturnType().getName().equals("void");
					String body =  getModifier(m) + " " + signature 
	                 		+ "{"
	                 		+ "boolean success=false;"
	                 		+ "boolean retry=true;"
	                 		+ (!isVoid?m.getReturnType().getName() + " retValue;":"") // Object retValue;
	                 		+ "while (retry){"
	                 		+ "try { "
	                 		+ (!isVoid?"retValue = ":"")  + "$proceed( " + varStr + ");"
	                 		+ "success=true;"
	                 		+ "retry=false;}"
	                 		+ addCatch(varStr, exceptions)
	                 		+ "}"
	                 		+ (!isVoid?"return retValue;":"")
	                 		+ "}";
					CtMethod newMethod = CtNewMethod.make(body,cc, "this", m.getName()	);
					cc.addMethod(newMethod);
					aspectApplied = true;
				}
			}
			if (aspectApplied)
				cc.writeFile("/Users/rernas/git/remote-ejb-example/client/target/classes");
			
		} catch (NotFoundException e) {
			getLog().error(e);
		} catch (IOException e) {
			getLog().error(e);
		} catch (CannotCompileException e) {
			getLog().error(e);
		} catch (ClassNotFoundException e) {
			getLog().error(e);
		}
		
		
		
		
	}
	
	private static String getModifier(CtMethod m){
		switch (m.getModifiers()) {
		case AccessFlag.PRIVATE:
			return "private";
		case AccessFlag.PUBLIC:
			return "public";
		case AccessFlag.PROTECTED:
			return "protected";		
		default:
			return "";
		}
	}
	
	
	public static String addCatch(String varStr,List<Class> clazzList) throws NotFoundException, CannotCompileException{
		String catchStr = "";
		for(Class clazz:clazzList){
			catchStr += "catch(" + clazz.getName() + " e){ "
								+ "retry=true; "
						+ "}";
		}
		
		return catchStr;
	}
	
	
	public static void main(String[] args) {
		
		JumpNextOne jnoo ;
		
		
	}
	
	
}
