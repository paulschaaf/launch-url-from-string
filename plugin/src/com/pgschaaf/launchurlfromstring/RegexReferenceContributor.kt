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

import com.intellij.ide.plugins.PluginManager
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import com.intellij.util.containers.stream
import java.util.stream.Stream

val CommonClasses = setOf("com.intellij.psi.PsiLiteral",   // covers Java & Scala
                          "com.intellij.psi.xml.XmlTag",
                          "com.intellij.psi.xml.XmlAttributeValue")

// Rather than making this a mapOf(...) I made it an object with a get operator because this makes the mappings far
// easier to read and manage.
object PluginClasses {
   operator fun get(key: String) = when (key) {
      "Dart"                 -> "com.jetbrains.lang.dart.psi.DartStringLiteralExpression"
      "Gosu"                 -> "gw.gosu.ij.psi.GosuPsiLiteralExpression"
      "JavaScript"           -> "com.intellij.lang.javascript.psi.JSLiteralExpression"
      "org.jetbrains.kotlin" -> "org.jetbrains.kotlin.psi.KtStringTemplateExpression"
      "com.jetbrains.php"    -> "com.jetbrains.php.lang.psi.elements.StringLiteralExpression"
      "PythonCore"           -> "com.jetbrains.python.psi.PyStringLiteralExpression"
//      "org.jetbrains.plugins.ruby" -> "org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral"
//      "org.jetbrains.plugins.ruby" -> "org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RLiteral"
      else                   -> null
   }
}

class RegexReferenceContributor: PsiReferenceContributor() {
   override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
      val commonLoaders = CommonClasses.stream()
            .map {it to javaClass.classLoader}

      val pluginLoaders = PluginManager.getPlugins().stream()
            .map {PluginClasses[it.pluginId.idString] to it.pluginClassLoader}
            .filter {(className, _)-> className != null}  // keep the installed plugins that we handle

      Stream.concat(commonLoaders, pluginLoaders)
            .map {(className, loader)-> loader.loadPsiElementClass(className)}
            .filter {it != null}    // somehow the named class failed to load
            .map {StandardPatterns.instanceOf(it)}
            .forEach {registrar.registerReferenceProvider(it, RegexPsiReferenceProvider)}
   }

   private object RegexPsiReferenceProvider: PsiReferenceProvider() {
      override fun getReferencesByElement(element: PsiElement, context: ProcessingContext) =
            arrayOf(PsiStringRegexToHyperlink(element))
   }

   private fun ClassLoader.loadPsiElementClass(stringLiteralClassName: String) =
         try {
            @Suppress("UNCHECKED_CAST")
            Class.forName(stringLiteralClassName, true, this) as Class<PsiElement>
         }
         catch (e: ClassNotFoundException) { // todo pschaaf 04/120/18 15:04: log the error
            // if we can't find the plugin (e.g. because it isn't installed) then skip it
            null
         }
}
