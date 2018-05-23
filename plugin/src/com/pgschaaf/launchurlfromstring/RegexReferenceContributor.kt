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
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import com.pgschaaf.util.withoutNulls
import java.util.ResourceBundle
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

fun ClassLoader.psiElementClassNamed(name: String?) =
      if (name == null)
         null
      else
         try {
            @Suppress("UNCHECKED_CAST")
            Class.forName(name, true, this) as Class<PsiElement>
         }
         catch (e: ClassNotFoundException) {
            null  // though we handle this plugin, it isn't installed
         }

object ElementPatterns {
   val plugins = PluginManager.getPlugins().associateBy {it.pluginId.idString}

   private val commonLoaders = CommonStringLiteralClassNames.stream()
         .map {it to javaClass.classLoader}

   private val pluginLoaders = ResourceBundle.getBundle(PropertiesFileName)
         .keysAndValues()
         .map {(className, pluginId)-> className to plugins[pluginId]?.pluginClassLoader}

   /** The Psi Element classes for which we will handle navigation **/
   val patterns = Stream.concat(commonLoaders, pluginLoaders)
         .map {(className, loader)-> loader?.psiElementClassNamed(className)}
         .withoutNulls()
         .map(StandardPatterns::instanceOf)
         .collect(Collectors.toList())

   fun registerIn(registrar: PsiReferenceRegistrar) = patterns.forEach {
      registrar.registerReferenceProvider(it, RegexPsiReferenceProvider)
   }

   private object RegexPsiReferenceProvider: PsiReferenceProvider() {
      override fun getReferencesByElement(element: PsiElement, context: ProcessingContext) =
            arrayOf(PsiStringRegexToHyperlink(element))
   }
}

fun ResourceBundle.keysAndValues(): Stream<Pair<String, String>> = this.keySet().stream()
      .map {it to getString(it)}