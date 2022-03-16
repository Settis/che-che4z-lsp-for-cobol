/*
 * Copyright (c) 2022 Broadcom.
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
package org.eclipse.lsp.cobol.usecases;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp.cobol.core.preprocessor.delegates.copybooks.DialectType;
import org.eclipse.lsp.cobol.positive.CobolText;
import org.eclipse.lsp.cobol.usecases.engine.UseCaseEngine;
import org.junit.jupiter.api.Test;

/** WRK qualifier should work even with copybooks with too short name. */
class TestCopyMaidWithWrkShortName {
  private static final String TEXT =
      "       IDENTIFICATION DIVISION.\n"
          + "       PROGRAM-ID.    TEST.\n"
          + "       ENVIRONMENT DIVISION.\n"
          + "       DATA DIVISION.\n"
          + "       WORKING-STORAGE SECTION.\n"
          + "        01 {$*PARENT}.\n"
          + "            05 COPY MAID {~BHTRGL-XBG} WRK.\n"
          + "        01 {$*ABCD}.\n"
          + "            05 COPY MAID {~BHTRGL-ABC} WRK.\n"
          + "       PROCEDURE DIVISION.\n"
          + "           DISPLAY {$NT}.\n"
          + "           DISPLAY {$CD}.";

  private static final String BHTRGL_XBG = "1           09 {$*A3^NT} PIC X.\n";

  private static final String BHTRGL_ABC =
      "            09 {$*A^CD} PIC X.\n"
          + "            09 PIC X.\n"
          + "            09 FILLER PIC x.";

  @Test
  void test() {
    UseCaseEngine.runTest(
        TEXT,
        ImmutableList.of(
            new CobolText("BHTRGL-XBG", DialectType.MAID.name(), BHTRGL_XBG),
            new CobolText("BHTRGL-ABC", DialectType.MAID.name(), BHTRGL_ABC)),
        ImmutableMap.of());
  }
}