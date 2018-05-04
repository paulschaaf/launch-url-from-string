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

import com.intellij.icons.AllIcons
import com.intellij.ide.BrowserUtil
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.*
import java.net.URI

class RegexNavigablePsiElement(private val element: PsiElement, private val url: String): NavigatablePsiElement, PsiElement by element {
   override fun canNavigate() = true
   override fun canNavigateToSource() = false
   override fun getNavigationElement() = this
   override fun getName(): String? = null

   override fun navigate(b: Boolean) = BrowserUtil.browse(URI(url))

   override fun getPresentation() = object: ItemPresentation {
      override fun getLocationString(): String? = null
      override fun getIcon(p0: Boolean) = AllIcons.General.Web
      override fun getPresentableText() = "Browse $url"
   }
}