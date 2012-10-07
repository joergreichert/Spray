package org.eclipselabs.spray.styles.jvmmodel

import com.google.inject.Inject
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.access.IJvmTypeProvider$Factory
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.xbase.jvmmodel.AbstractModelInferrer
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder
import org.eclipselabs.spray.runtime.graphiti.styles.DefaultSprayStyle
import org.eclipselabs.spray.styles.generator.GradientGenerator
import org.eclipselabs.spray.styles.generator.StyleGenerator
import org.eclipselabs.spray.styles.styles.Gradient
import org.eclipselabs.spray.styles.styles.Style
import org.eclipselabs.spray.runtime.graphiti.styles.ISprayGradient

class StyleJvmModelInferrer extends AbstractModelInferrer {

	@Inject extension TypeReferences typeReferences
    @Inject extension JvmTypesBuilder
    @Inject extension StyleGenerator styleGenerator
    @Inject extension GradientGenerator gradientGenerator
    @Inject extension IJvmTypeProvider$Factory typeProviderFactory

	def void infer(Style element, IJvmDeclaredTypeAcceptor acceptor, boolean isPrelinkingPhase) {
		val JvmTypeReference superTypeRef = element.calculateSuperTypeRef
		val typeProvider = typeProviderFactory.createTypeProvider
		var existingClass = typeProvider.findTypeByName(element.packageName + "." + element.className)
		if(existingClass == null) {
			acceptor.accept(element.toClass(element.packageName + "." + element.className)).initializeLater [
				if(superTypeRef != null) superTypes += superTypeRef.cloneWithProxies
			]
		} else {
			if(existingClass instanceof JvmDeclaredType) {
				element.associate(existingClass)
				acceptor.accept(existingClass as JvmDeclaredType)
			}
		}
	}

	def void infer(Gradient element, IJvmDeclaredTypeAcceptor acceptor, boolean isPrelinkingPhase) {
		val typeProvider = typeProviderFactory.createTypeProvider
		var existingClass = typeProvider.findTypeByName(element.packageName + "." + element.className)
		if(existingClass == null) {
			acceptor.accept(element.toClass(element.packageName + "." + element.className))
		} else {
			if(existingClass instanceof JvmDeclaredType) {
				element.associate(existingClass)
				acceptor.accept(existingClass as JvmDeclaredType)
			}
		}
	}
	
	def dispatch String getPackageName(Style style) {
		styleGenerator.packageName(style)
	}
	
	def dispatch String getPackageName(Gradient gradient) {
		gradientGenerator.packageName(gradient)
	}	
	
	def dispatch String getClassName(Style style) {
		styleGenerator.className(style)
	}
	
	def dispatch String getClassName(Gradient gradient) {
		gradientGenerator.className(gradient)
	}	
	
	def dispatch JvmTypeReference calculateSuperTypeRef(Style style) {
		var JvmTypeReference varSuperTypeRef = null
		if(style.superStyle != null) {
			varSuperTypeRef = cloneWithProxies(style.superStyle)
		} else {
			val superType = typeReferences.findDeclaredType(typeof(DefaultSprayStyle), style)
			if(superType != null) varSuperTypeRef = typeReferences.createTypeRef(superType)
		}
		varSuperTypeRef
	}
	
	def dispatch JvmTypeReference calculateSuperTypeRef(Gradient gradient) {
		var JvmTypeReference varSuperTypeRef = null
		val superType = typeReferences.findDeclaredType(typeof(ISprayGradient), gradient)
		if(superType != null) varSuperTypeRef = typeReferences.createTypeRef(superType)
		varSuperTypeRef
	}	
}
