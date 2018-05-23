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
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import com.pgschaaf.util.withoutNulls
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

private const val PropertiesFileName = "StringLiteralClassNames"
private const val testPlugin = "wikipedia:Kotlin_(programming_language)"

/**
 * These are likely to be present regardless of which language plugins are.
 */
val CommonStringLiteralClassNames = setOf("com.intellij.psi.PsiLiteral",   // covers Java & Scala
                                          "com.intellij.psi.xml.XmlTag",
                                          "com.intellij.psi.xml.XmlAttributeValue")

class RegexReferenceContributor: PsiReferenceContributor() {
   override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) = ElementPatterns.registerIn(registrar)
}

object ElementPatterns {
   val bundle = ResourceBundle.getBundle(PropertiesFileName)
   val handledPlugins = bundle.keySet()

   /** The Psi Element classes for which we will handle navigation **/
   var patterns: List<ElementPattern<PsiElement>>
      private set

   init {
      val pluginLoaders = Arrays.stream(PluginManager.getPlugins())
            .filter {plugin-> handledPlugins.contains(plugin.pluginId.idString)}
            .map {plugin-> bundle.getString(plugin.pluginId.idString)!! to plugin.pluginClassLoader}

      val commonLoaders = CommonStringLiteralClassNames.stream()
            .map {it to javaClass.classLoader}

      patterns = Stream.concat(commonLoaders, pluginLoaders)
            .map {(className, loader)-> loadPsiElementClass(className, loader)}
            .withoutNulls()
            .map(StandardPatterns::instanceOf)
            .collect(Collectors.toList<ElementPattern<PsiElement>>())
   }

   private fun loadPsiElementClass(stringLiteralClassName: String, loader: ClassLoader) =
         try {
            @Suppress("UNCHECKED_CAST")
            Class.forName(stringLiteralClassName, true, loader) as Class<PsiElement>
         }
         catch (e: ClassNotFoundException) {
            null  // though we handle this plugin, it isn't installed
         }

   fun registerIn(registrar: PsiReferenceRegistrar) = patterns.forEach {
      registrar.registerReferenceProvider(it, RegexPsiReferenceProvider)
   }

   private object RegexPsiReferenceProvider: PsiReferenceProvider() {
      override fun getReferencesByElement(element: PsiElement, context: ProcessingContext) =
            arrayOf(PsiStringRegexToHyperlink(element))
   }
}
