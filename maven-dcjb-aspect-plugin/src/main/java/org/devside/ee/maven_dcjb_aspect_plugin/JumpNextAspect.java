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
	
	static ClassPool pool = ClassPool.getDefault();
	static Class[] defaultExceptions = {IllegalStateException.class/*,SocketException.class,IOException.class*/};
	
	public  void execute() {
		
		try {
			String path = project.getBuild().getDirectory() + File.separator + "classes";
			pool.appendClassPath(path);
			changeClass( path,"",path);
		} catch (NotFoundException ee) {
			getLog().error(ee);
		} catch (IOException e) {
			getLog().error(e);
		} catch (CannotCompileException e) {
			getLog().error(e);
		} catch (ClassNotFoundException e) {
			getLog().error(e);
		}
		
		
		
		
	}

	private void changeClass(String path,String pack, String basePath)
			throws NotFoundException, ClassNotFoundException, CannotCompileException, IOException {
		
		File folder = new File(path);
		File[] files = folder.listFiles();
		for(File f:files){
			if (f.isDirectory()){
				if (!pack.isEmpty())
					pack = pack + "." + f.getName();
				else 
					pack = f.getName();
				changeClass(path + File.separator + f.getName(), pack , basePath);
			}else if (f.getName().endsWith(".class")){
				CtClass cc;
				cc = pool.get(pack + "." + f.getName().substring(0,f.getName().indexOf(".class")));
				
				
				boolean aspectApplied = false;
				for (CtMethod m : cc.getDeclaredMethods()) {
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
					cc.writeFile(basePath);
			}
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
