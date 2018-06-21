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
import com.intellij.openapi.paths.WebReference
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import org.jetbrains.rpc.LOG
import java.util.*
import java.util.stream.Collectors

// Click on the String value to test the plugin -- compiler should optimize this away
private inline val testPlugin
   get() = "wikipedia:Kotlin_(programming_language)"

object RegexPsiReferenceContributor: PsiReferenceContributor() {
   const val ClassMapPropertiesFileName = "StringLiteralClassNames"

   private val classLoaders =
         PluginManager
               .getPlugins()
               .associate {it.pluginId.idString to it.pluginClassLoader}
               .withDefault {javaClass.classLoader}

   private val referenceProviders =
         ResourceBundle
               .getBundle(ClassMapPropertiesFileName)
               .keysAndValues()
               .map {(className, pluginId)-> classLoaders.getValue(pluginId) to className}
               .map {(loader, className)-> loader.tryToLoad<PsiElement>(className)}
               .filter {it.isPresent}
               .map {StandardPatterns.instanceOf(it.get())}
               .collect(Collectors.toList())

   override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) =
         referenceProviders.forEach {
            registrar.registerReferenceProvider(it, RegexPsiReferenceProvider)
         }
}

object RegexPsiReferenceProvider: PsiReferenceProvider() {
   override fun getReferencesByElement(element: PsiElement, context: ProcessingContext) =
         element
               .url
               .map {WebReference(element, it)}!!
               .map {arrayOf(it)}
               .orElseGet {arrayOfNulls(0)}!!
}

/* ----------------- ENHANCEMENTS ----------------- */
fun <T> ClassLoader.tryToLoad(name: String) = Optional.ofNullable(
      try {
         @Suppress("UNCHECKED_CAST")
         Class.forName(name, true, this) as Class<T>
      }
      catch (e: ClassNotFoundException) {
         LOG.info("$this could not load class: '$name'")
         null
      }
)

fun ResourceBundle.keysAndValues() = keySet().stream().map {it to getString(it)}!!