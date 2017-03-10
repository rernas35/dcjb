package org.devside.ee.dcjb.plugin;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugins.annotations.Mojo;
import org.devside.ee.dcjb.client.annotation.JumpNextOne;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;


@Mojo(name="applyClientCode")
public class JumpNextAspect {

	public static void main(String[] args) throws Exception {
		
		Class[] defaultExceptions = {Exception.class/*,SocketException.class,IOException.class*/};
		boolean aspectApplied = false;
		
		ClassPool pool = ClassPool.getDefault();
		pool.appendClassPath("/Users/rernas/git/remote-ejb-example/client/target/classes/");
		CtClass cc = pool.get("com.illucit.ejbremote.TestEJB");
		
		for (CtMethod m : cc.getMethods()) {
			JumpNextOne jumpNextOne = (JumpNextOne) m.getAnnotation(JumpNextOne.class);
			if (jumpNextOne != null){
				List<Class> exceptions =new ArrayList<Class>(); 
//				exceptions.addAll(Arrays.asList(jumpNextOne.exceptions()));
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
	
	
	
}
