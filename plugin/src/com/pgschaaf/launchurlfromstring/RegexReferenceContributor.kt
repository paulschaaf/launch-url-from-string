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
import java.util.*
import java.util.stream.Stream

/**
 * These are likely to be present regardless of which language plugins are.
 */
val CommonStringLiteralClassNames = setOf("com.intellij.psi.PsiLiteral",   // covers Java & Scala
                                          "com.intellij.psi.xml.XmlTag",
                                          "com.intellij.psi.xml.XmlAttributeValue")

class RegexReferenceContributor: PsiReferenceContributor() {
   override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
      val commonLoaders = CommonStringLiteralClassNames.stream()
            .map {it to javaClass.classLoader}

      val pluginLoaders = Arrays.stream(PluginManager.getPlugins())
            .map {PluginToStringLiteralClassNameMap[it.pluginId.idString] to it.pluginClassLoader}
            .filter {(className, _)-> className != null}  // ignore any plugins that we don't handle

      Stream.concat(commonLoaders, pluginLoaders)
            .map {(className, loader)-> loadPsiElementClass(className, loader)}
            .filter {clazz-> clazz != null}    // somehow the named class failed to load
            .map(StandardPatterns::instanceOf)
            .forEach {registrar.registerReferenceProvider(it, RegexPsiReferenceProvider)}
   }

   private fun loadPsiElementClass(stringLiteralClassName: String?, loader: ClassLoader) =
         try {
            @Suppress("UNCHECKED_CAST")
            if (stringLiteralClassName == null) {
               // todo pschaaf 05/124/18 17:05: log it as this shouldn't happen
               null
            }
            else Class.forName(stringLiteralClassName, true, loader) as Class<PsiElement>
         }
         catch (e: ClassNotFoundException) { // todo pschaaf 04/120/18 15:04: log the error
            // if we can't find the plugin (e.g. because it isn't installed) then skip it
            null
         }

   private object RegexPsiReferenceProvider: PsiReferenceProvider() {
      override fun getReferencesByElement(element: PsiElement, context: ProcessingContext) =
            arrayOf(PsiStringRegexToHyperlink(element))
   }
}
