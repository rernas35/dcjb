package org.devside.ee.dcjb.plugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.maven.plugins.annotations.Mojo;
import org.devside.ee.dcjb.client.annotation.JumpNextOne;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;


@Mojo(name="applyClientCode")
public class JumpNextAspect {

	public static void main(String[] args) throws Exception {
		
		boolean aspectApplied = false;
		ClassPool pool = ClassPool.getDefault();
		pool.appendClassPath("/Users/rernas/git/remote-ejb-example/client/target/classes/");
		CtClass cc = pool.get("com.illucit.ejbremote.TestEJB");
		for (CtMethod m : cc.getMethods()) {
			JumpNextOne jumpNextOne = (JumpNextOne) m.getAnnotation(JumpNextOne.class);
			if (jumpNextOne != null){
				for (Class clazz : jumpNextOne.exceptions()) {
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
					
					CtMethod newMethod = CtNewMethod.make(
			                 getModifier(m) + " " + signature +  " { "
			                 		+ "System.out.println(\"$e\");"
			                 		+ "return $proceed( " + varStr + "); }",
			                 cc, "this", m.getName()	);
					cc.addMethod(newMethod);
					aspectApplied = true;
				}
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
	
	
	public static void addCatch(CtMethod method, Class clazz) throws NotFoundException, CannotCompileException{
		CtClass etype = ClassPool.getDefault().get(clazz.getName());
		method.addCatch("{ System.out.println($e);}", etype);
	}
	
	
}
