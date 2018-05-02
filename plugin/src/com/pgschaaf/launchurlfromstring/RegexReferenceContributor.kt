/*
 * Copyright 2018 P.G. Schaaf <paul.schaaf@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.pgschaaf.launchurlfromstring

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManager
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.StandardPatterns
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext

class RegexReferenceContributor: PsiReferenceContributor() {
   override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
      registrar.register(XmlPatterns.xmlAttributeValue(), XmlPatterns.xmlTag())
      registrar.register("com.intellij.psi.PsiLiteral")   // covers Java and Scala

      fun IdeaPluginDescriptor.register(className: String) = registrar.register(className, pluginClassLoader)
      PluginManager.getPlugins().forEach {plugin->
         with(plugin) {
            when (pluginId.idString) {
               "Dart"                 -> register("com.jetbrains.lang.dart.psi.DartStringLiteralExpression")
               "Gosu"                 -> register("gw.gosu.ij.psi.GosuPsiLiteralExpression")
               "org.jetbrains.kotlin" -> register("org.jetbrains.kotlin.psi.KtStringTemplateExpression")
               "PythonCore"           -> register("com.jetbrains.python.psi.PyStringLiteralExpression")

            // todo pschaaf 04/120/18 16:04: the remaining plugins have not been verified for some time
               "JavaScript"           -> register("com.intellij.lang.javascript.psi.JSLiteralExpression")
               "com.jetbrains.php"    -> register("com.jetbrains.php.lang.psi.elements.StringLiteralExpression")
               else -> {}
            }
         }
      }
   }
}

fun PsiReferenceRegistrar.register(className: String, classLoader: ClassLoader = javaClass.classLoader) = try {
   @Suppress("UNCHECKED_CAST")
   val clazz = Class.forName(className, true, classLoader) as Class<PsiElement>
   register(StandardPatterns.instanceOf(clazz))
}
catch (e: ClassNotFoundException) {
   // todo pschaaf 04/120/18 15:04: log the errror
   // if we can't find the plugin (e.g. because it isn't installed) then skip it
}

fun PsiReferenceRegistrar.register(vararg elementPatterns: ElementPattern<out PsiElement>) =
      elementPatterns.forEach {elementPattern-> registerReferenceProvider(elementPattern, RegexPsiReferenceProvider)}

object RegexPsiReferenceProvider: PsiReferenceProvider() {
   override fun getReferencesByElement(element: PsiElement, context: ProcessingContext) =
         arrayOf(PsiStringRegexToHyperlink(element))
}