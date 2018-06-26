package com.pgschaaf.launchurlfromstring

import com.intellij.psi.impl.FakePsiElement
import org.fest.assertions.Assertions.assertThat
import org.fest.assertions.GenericAssert
import org.junit.Test
import java.util.*

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

class PsiElementEnhancementsTest {
   fun <T: Any> assertThat(opt: Optional<T>) = OptionalAssert(opt)

   @Test
   fun `PsiElement clickableString`() {
      val expected = "hello"
      val element = MockPsiElement(expected)
      val actual = element.clickableString
      assertThat(actual)
            .describedAs("element.clickableString")
            .isEqualTo(expected)
   }
}
class MockPsiElement(val string: String?): FakePsiElement() {
   override fun getParent() = this
   override fun getText() = string
}

class OptionalAssert<T>(actual: Optional<T>):
      GenericAssert<OptionalAssert<T>, Optional<T>>(OptionalAssert::class.java as Class<OptionalAssert<T>>, actual) {
   fun isPresent() = {}
   fun isAbsent() = {}
}

