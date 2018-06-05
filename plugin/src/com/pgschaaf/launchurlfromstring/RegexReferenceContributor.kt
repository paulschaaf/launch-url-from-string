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
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import com.pgschaaf.util.keysAndValues
import com.pgschaaf.util.load
import com.pgschaaf.util.withoutNulls
import java.util.ResourceBundle
import java.util.stream.Collectors

private const val PropertiesFileName = "StringLiteralClassNames"
private const val testPlugin = "wikipedia:Kotlin_(programming_language)" // Click on this var to test the plugin

class RegexReferenceContributor: PsiReferenceContributor() {
   override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) = ApplicationContext.submitTo(registrar)
}

object ApplicationContext {
   val pluginClassLoaders = PluginManager.getPlugins()
         .associate {it.pluginId.idString to it.pluginClassLoader}

   private val defaultClassLoader = RegexReferenceContributor::class.java.classLoader

   /** The Psi Element classes for which we will handle navigation **/
   val patterns: List<ElementPattern<PsiElement>> =
         ResourceBundle.getBundle(PropertiesFileName)
               .keysAndValues()
               .map {(className, pluginId)->
                  className to pluginClassLoaders.getOrDefault(pluginId, defaultClassLoader)
               }
               .map {(className, loader)-> loader.load<PsiElement>(className)}
               .withoutNulls()
               .map(StandardPatterns::instanceOf)
               .collect(Collectors.toList())

   fun submitTo(registrar: PsiReferenceRegistrar) =
         patterns.forEach {
            registrar.registerReferenceProvider(it, RegexPsiReferenceProvider)
         }

   private object RegexPsiReferenceProvider: PsiReferenceProvider() {
      override fun getReferencesByElement(element: PsiElement, context: ProcessingContext) =
            arrayOf(PsiStringRegexToHyperlink(element))
   }
}