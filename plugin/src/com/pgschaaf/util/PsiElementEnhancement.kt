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

package com.pgschaaf.util

import com.intellij.openapi.paths.WebReference
import com.intellij.openapi.vcs.IssueNavigationConfiguration
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import java.util.*

/** Return the string portion of this PsiElement that should be treated as a hyperlink **/
val PsiElement.clickableString: Optional<String>
   get() {
      val str = when (this) {
         is XmlAttributeValue -> value ?: ""
         is XmlTag            -> value.trimmedText
         else                 -> text.unquoted
      }
      return if (str.isEmpty()) Optional.empty()
      else Optional.of(str)
   }

private val PsiElement.issueNavigationConfig
   get() = IssueNavigationConfiguration.getInstance(project)!!

val PsiElement.url
   get() = clickableString
         .flatMap {
            issueNavigationConfig.links.stream()
                  .map {link-> link.destinationFor(it)}
                  .filter {destStr-> destStr.isNotEmpty() && destStr != it}
                  .findFirst()
         }!!

val PsiElement.webReference
   get() = url.map {WebReference(this, it)}!!
