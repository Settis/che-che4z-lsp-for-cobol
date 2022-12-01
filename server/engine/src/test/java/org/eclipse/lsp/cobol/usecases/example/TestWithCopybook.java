/*
 * Copyright (c) 2021 Broadcom.
 * The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Broadcom, Inc. - initial API and implementation
 *
 */
package org.eclipse.lsp.cobol.usecases.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp.cobol.test.engine.CobolText;
import org.eclipse.lsp.cobol.usecases.engine.UseCaseEngine;
import org.junit.jupiter.api.Test;

/** UseCase test example with copybooks */
class TestWithCopybook {
  private static final String TEXT =
      "       IDENTIFICATION DIVISION.\n"
          + "       PROGRAM-ID.    TEST.\n"
          + "       ENVIRONMENT DIVISION.\n"
          + "       DATA DIVISION.\n"
          + "       WORKING-STORAGE SECTION.\n"
          + "         COPY {~THEBOOK}.\n"
          + "       Procedure Division.\n"
          + "           display {$VAR}.";

  private static final String COPYBOOK_CONTENT = "       01  {$*VAR}     PIC S9(4) COMP.";

  @Test
  void test() {
    UseCaseEngine.runTest(
        TEXT, ImmutableList.of(new CobolText("THEBOOK", COPYBOOK_CONTENT)), ImmutableMap.of());
  }
}
