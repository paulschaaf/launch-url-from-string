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

import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import com.pgschaaf.util.webReference

object RegexPsiReferenceContributor: PsiReferenceContributor() {
   override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) = ApplicationContext.psiElementPatterns.forEach {
      registrar.registerReferenceProvider(it, RegexPsiReferenceProvider)
   }

   object RegexPsiReferenceProvider: PsiReferenceProvider() {
      override fun getReferencesByElement(element: PsiElement, context: ProcessingContext) =
            element.webReference
               .map {arrayOf(it)}
               .orElseGet {arrayOfNulls(0)}
   }
}
