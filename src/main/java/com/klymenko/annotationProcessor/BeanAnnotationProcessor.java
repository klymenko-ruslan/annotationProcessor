package com.klymenko.annotationProcessor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes( "com.klymenko.annotationProcessor.Bean" )
@SupportedSourceVersion(SourceVersion.RELEASE_8) 
public class BeanAnnotationProcessor extends AbstractProcessor {
  
	@Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for(Element element : roundEnv.getElementsAnnotatedWith(Bean.class)) {
          if(element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement)element;
            if(!implementsSerializable(typeElement)) {
            	processingEnv.getMessager()
            	             .printMessage(Kind.ERROR,
            	            		       "Bean should implement Serializable interface!");
            }
            Set<VariableElement> variableElements = new HashSet<>();
            Set<String> executableElements = new HashSet<>();
            boolean overridedEquals = false;
            boolean overridedHashCode = false;
            boolean overridedToString = false;
            for(Element enclosedElement : typeElement.getEnclosedElements()) {
                if(enclosedElement instanceof ExecutableElement) {
            		ExecutableElement executableElement = (ExecutableElement) enclosedElement;
            		String methodName = String.valueOf(executableElement.getSimpleName());
            		executableElements.add(methodName);
            		if(isEqualsMethod(executableElement)) {
            			overridedEquals = true;
            		}
            		if(isHashCodeMethod(executableElement)) {
            			overridedHashCode = true;
            		}
            		if(isToStringMethod(executableElement)) {
            			overridedToString = true;
            		}
            	} else if(enclosedElement instanceof VariableElement) {
	                VariableElement variableElement = (VariableElement) enclosedElement;
	                variableElements.add(variableElement);	                
	                if(!isFinalVariable(variableElement)) {
	            	    processingEnv.getMessager()
				 	 		 .printMessage(Kind.ERROR,
				 	 				 	   "Bean's variables should be declared private!");
	               }
	            }
	         }
            if(!overridedEquals) {
            	processingEnv.getMessager()
   				 .printMessage(Kind.ERROR,
   						 	   "Bean should override equals!");
            }
            if(!overridedHashCode) {
            	processingEnv.getMessager()
		 			 .printMessage(Kind.ERROR,
		 					 	   "Bean should override hashCode!");
            }
            if(!overridedToString) {
            	processingEnv.getMessager()
		 			 .printMessage(Kind.ERROR,
		 					 	   "Bean should override toString!");
            }
            for(VariableElement variableElement : variableElements) {
            	String variableName = String.valueOf(variableElement.getSimpleName());
            	variableName = variableName.substring(0,  1).toUpperCase() + variableName.substring(1);
            	String getterName = "get" + variableName;
            	if(!executableElements.contains(getterName)) {
            		processingEnv.getMessager()
		 	 		 .printMessage(Kind.ERROR,
		 	 				 	   "Bean should contain getter method for each field!");
            	}
            	
            	String setterName = "set" + variableName;
            	if(!executableElements.contains(setterName)) {
            		processingEnv.getMessager()
		 	 		 .printMessage(Kind.ERROR,
		 	 				 	   "Bean should contain setter method for each field!");
            	}
            }
          }
        }
        return true;
    }
	
	private boolean implementsSerializable(TypeElement typeElement) {
		boolean implementsSerializable = false;
        for(TypeMirror typeMirror : typeElement.getInterfaces()) {
        	 if(typeMirror.toString().equals(Serializable.class.getName())) {
        		 implementsSerializable = true;
        	 }
        }
        return implementsSerializable;
	}
	
	private boolean isEqualsMethod(ExecutableElement executableElement) {
		String methodName = String.valueOf(executableElement.getSimpleName());
		if(methodName.equals("equals")) {
			if(executableElement.getReturnType().getKind().equals(TypeKind.BOOLEAN)) {
				if(executableElement.getParameters().size() == 1) {
    				String parameterTypeName = String.valueOf(executableElement.getParameters().get(0).asType());
    				if(parameterTypeName.equals(Object.class.getName())) {
    					return true;
    				}
				}
			}
		}
		return false;
	}
	
	private boolean isHashCodeMethod(ExecutableElement executableElement) {
		String methodName = String.valueOf(executableElement.getSimpleName());
		if(methodName.equals("hashCode")) {
			if(executableElement.getReturnType().getKind().equals(TypeKind.INT)) {
				if(executableElement.getParameters().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isToStringMethod(ExecutableElement executableElement) {
		String methodName = String.valueOf(executableElement.getSimpleName());
		if(methodName.equals("toString")) {
			if(executableElement.getReturnType().getKind().equals(TypeKind.DECLARED)) {
				if(executableElement.getParameters().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isFinalVariable(VariableElement variableElement) {
		for(Modifier modifier : variableElement.getModifiers()) {
			System.out.println("Modifier's name: " + modifier.name());
        	if(modifier.equals(Modifier.PRIVATE)) {
        		return true;
        	}
        }
        return false;
	}	
}
