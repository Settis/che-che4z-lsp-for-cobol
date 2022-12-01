/*
 * Copyright (c) 2020 Broadcom.
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
package org.eclipse.lsp.cobol.usecases.idms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp.cobol.usecases.DialectConfigs;
import org.eclipse.lsp.cobol.test.engine.UseCaseEngine;
import org.eclipse.lsp.cobol.utils.Fixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/** These test for variations of valid COMMIT statements */
class TestCommit {

  private static final String BOILERPLATE =
      "        IDENTIFICATION DIVISION. \r\n"
          + "        PROGRAM-ID. test1. \r\n"
          + "        DATA DIVISION. \r\n"
          + "        WORKING-STORAGE SECTION. \r\n"
          + "        PROCEDURE DIVISION. \r\n";

  private static final String COMMIT_TASK_ALL = "           COMMIT TASK ALL .\r\n";

  private static final String COMMIT_TASK = "           COMMIT TASK .\r\n";

  private static final String COMMIT_ALL = "           COMMIT ALL .\r\n";

  private static Stream<String> textsToTest() {
    return Stream.of(
        BOILERPLATE + COMMIT_TASK_ALL, BOILERPLATE + COMMIT_TASK, BOILERPLATE + COMMIT_ALL);
  }

  @ParameterizedTest
  @MethodSource("textsToTest")
  @DisplayName("Parameterized - varying tests")
  void test(String text) {
    UseCaseEngine.runTestForDiagnostics(
        text,
        ImmutableList.of(Fixtures.subschemaCopy("")),
        ImmutableMap.of(),
        ImmutableList.of(),
        DialectConfigs.getIDMSAnalysisConfig());
  }
}
