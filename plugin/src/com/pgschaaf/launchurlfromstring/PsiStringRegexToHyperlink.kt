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

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vcs.IssueNavigationConfiguration
import com.intellij.psi.*
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag

class PsiStringRegexToHyperlink<T: PsiElement>(element: T): PsiPolyVariantReferenceBase<T>(element, true) {
   val project = ProjectManager.getInstance().openProjects.first()
   val issueNavigationConfiguration = IssueNavigationConfiguration.getInstance(project)

   override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult?> {
      val stringValue = element.stringValue
      return if (stringValue.isNullOrEmpty())
         arrayOfNulls(0)
      else
         issueNavigationConfiguration.links.stream()
               .map {link-> link.issuePattern.toRegex() to link.linkRegexp}
               .filter {(issuePattern, _)-> issuePattern.containsMatchIn(stringValue!!)}
               .map {(issuePattern, urlPattern)-> stringValue!!.replace(issuePattern, urlPattern)}
               .map {urlString-> element.regexResolveResult(urlString!!)}
               .limit(1)  // look no further than the first match
               .toArray {length-> arrayOfNulls<ResolveResult>(length)}
   }

   override fun getVariants() = arrayOfNulls<Any>(0)

   override fun isReferenceTo(element: PsiElement) = false
}

fun PsiElement.regexResolveResult(url: String): PsiElementResolveResult = PsiElementResolveResult(
      RegexNavigablePsiElement(this, url))

val PsiElement.stringValue
   get() = when (this) {
      is PsiLiteral        -> value as? String
      is XmlAttributeValue -> value
      is XmlTag            -> value.trimmedText
      else                 -> text
   }?.unquoted

val String.unquoted
   get() = when (first()) {
      '"'  -> removeSurrounding("\"")
      '\'' -> removeSurrounding("'")
      else -> this
   }
