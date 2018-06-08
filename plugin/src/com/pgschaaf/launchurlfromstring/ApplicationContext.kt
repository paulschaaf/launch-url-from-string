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
import com.pgschaaf.util.keysAndValues
import com.pgschaaf.util.tryToLoad
import com.pgschaaf.util.withoutNulls
import java.util.*
import java.util.stream.Collectors

object ApplicationContext {
   const val PropertiesFileName = "StringLiteralClassNames"

   private val psiStringMappings = ResourceBundle.getBundle(PropertiesFileName)

   private val defaultClassLoader = RegexPsiReferenceContributor::class.java.classLoader

   private val pluginClassLoaders = PluginManager.getPlugins()
         .associate {it.pluginId.idString to it.pluginClassLoader}
         .withDefault {defaultClassLoader}

   /** The Psi Element classes for which we will handle navigation **/
   val psiElementPatterns: List<ElementPattern<PsiElement>> =
         psiStringMappings
               .keysAndValues()
               .map {(className, pluginId)->
                  pluginClassLoaders
                        .getValue(pluginId)
                        .tryToLoad<PsiElement>(className)
               }
               .withoutNulls()
               .map(StandardPatterns::instanceOf)
               .collect(Collectors.toList())
}
