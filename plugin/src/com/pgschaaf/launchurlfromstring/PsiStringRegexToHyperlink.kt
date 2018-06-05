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
import com.intellij.openapi.vcs.IssueNavigationLink
import com.intellij.psi.*
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.pgschaaf.util.*

class PsiStringRegexToHyperlink<T: PsiElement>(element: T): PsiPolyVariantReferenceBase<T>(element, true) {
   override fun getVariants() = arrayOfNulls<Any>(0)
   override fun isReferenceTo(element: PsiElement) = false

   override fun multiResolve(incompleteCode: Boolean) = multiResolve(element.clickableString)

   private fun multiResolve(selectedString: String): Array<ResolveResult?> =
         if (selectedString.isEmpty())
            arrayOfNulls(0)
         else
            IssueNavigationConfiguration.getInstance(element.project).links.stream()
                  .map {link-> link.destinationFor(selectedString)}
                  .filter {destStr -> destStr.isNotEmpty() && destStr != selectedString}
                  .limit(1)  // look no further than the first match
                  .map {destStr-> WebReference(element, destStr).resolve()}
                  .map {reference-> PsiElementResolveResult(reference!!)}
                  .toArray {length-> arrayOfNulls<ResolveResult>(length)}
}

val PsiElement.clickableString
   get() = when (this) {
//      is PsiLiteral        -> value as? String ?: ""  // todo pschaaf 05/30/18 10:05: unneeded for Java, Kotlin or XML
      is XmlAttributeValue -> value ?: ""
      is XmlTag            -> value.trimmedText
      else                 -> text.unquoted
   }

fun IssueNavigationLink.destinationFor(text: String) = issuePattern.toRegex().replace(text, linkRegexp)
