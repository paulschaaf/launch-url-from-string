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

import com.intellij.openapi.paths.WebReference
import com.intellij.openapi.vcs.IssueNavigationConfiguration
import com.intellij.psi.*
import com.intellij.util.containers.getIfSingle
import com.pgschaaf.util.*

class PsiStringRegexToHyperlink<T: PsiElement>(element: T): PsiReferenceBase<T>(element) {
   override fun getVariants() = arrayOfNulls<Any>(0)

   override fun resolve() = resolve(element.clickableString)?.element

   private fun resolve(selectedString: String) =
      if (selectedString.isNotEmpty())
         IssueNavigationConfiguration.getInstance(element.project).links.stream()
            .map {link-> link.destinationFor(selectedString)}
            .filter {destStr-> destStr.isNotEmpty() && destStr != selectedString}
            .limit(1)  // look no further than the first match
            .map {destStr-> WebReference(element, destStr).resolve()}
            .map {reference-> PsiElementResolveResult(reference!!)}
            .getIfSingle()
      else null
}
