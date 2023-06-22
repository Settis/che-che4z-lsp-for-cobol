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
package org.eclipse.lsp.cobol.usecases;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp.cobol.common.error.ErrorSource;
import org.eclipse.lsp.cobol.test.engine.UseCaseEngine;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

/** This test checks that the compiler directives that contain typos processed as usual lines */
class TestTyposOnCompilerDirectives {
  private static final String PROCESS_WITHOUT_NUMBER_TYPO =
      "PROESS {DATA|1}(24),RMODE(24),NODYNAM\r\n";

  private static final String PROCESS_WITH_NUMBER_TYPO =
      "012345 {PROESS|1} DATA(24),RMODE(24),NODYNAM\r\n";

  private static final String PROCESS_WITHOUT_NUMBER_DATA_TYPO =
      "PROCESS {DDATA|1}(24),RMODE(24),NODYNAM\r\n";

  private static final String PROCESS_WITH_NUMBER_DATA_TYPO =
      "012345 PROCESS {DDATA|1}(24),RMODE(24),NODYNAM\r\n";

  private static final String FOLLOWING_TEXT =
      "000000 Identification DIVISION.                                         23323232\r\n"
          + "002800 Program-ID.                                                      23323232\r\n"
          + "002800  HELLOWORLD.                                                     23323232\r\n"
          + "024200 PROCEDURE DIVISION .                                             CM1014.2\r\n";

  private static final String DATA_ERROR_MESSAGE =
      "Syntax error on 'DDATA' expected {ADATA, EPILOG, NATLANG, NOCMPR2, ADV, AFP, APOST, AR, ARCH, ARITH, AWO, BLOCK0,"
          + " BUF, BUFSIZE, CICS, CODEPAGE, COMPILE, COPYLOC, COPYRIGHT, CP, CPP, CPLC, CPYR, CURR, CURRENCY, C, DATA,"
          + " DATEPROC, DBCS, DECK, DEF, DEFINE, DIAGTRUNC, DISPSIGN, DLL, DP, DS, DTR, DU, DUMP, DYN, DYNAM, D, EX,"
          + " EXIT, EXP, EXPORTALL, FASTSRT, FLAG, FLAGSTD, FSRT, F, GDS, GRAPHIC, HGPR, IC, INITCHECK, INITIAL, INL,"
          + " INLINE, INTDATE, INVD, INVDATA, LANG, LANGUAGE, LC, LEASM, LIB, LIN, LINECOUNT, LIST, LP, MAP, MARGINS,"
          + " MAXPCF, MD, MDECK, NAME, NC, NOADATA, NOADV, NOAWO, NOBLOCK0, NOC, NOCICS, NOCOMPILE, NOCOPYLOC, "
          + "NOCOPYRIGHT, NOCPLC, NOCPYR, NOCURR, NOCURRENCY, NOD, NODBCS, NODE, NODECK, NODEF, NODEFINE, NODATEPROC,"
          + " NODP, NODIAGTRUNC, NODLL, NODTR, NODU, NODUMP, NODYN, NODYNAM, NOEPILOG, NOEX, NOEXIT, NOEXP, "
          + "NOEXPORTALL, NOF, NOFASTSRT, NOFLAG, NOFLAGSTD, NOFSRT, NOFLAGMIG, NOGRAPHIC, NOIC, NOINITCHECK, "
          + "NOINITIAL, NOINL, NOINLINE, NOINVD, NOINVDATA, NOLIB, NOLIST, NOMAP, NOMD, NOMDECK, NONAME, NONC, "
          + "NONUM, NONUMBER, NONUMCHECK, NOOBJ, NOOBJECT, NOOFF, NOOFFSET, NOOPSEQUENCE, NOOPT, NOOPTIMIZE, NOP, "
          + "NOPARMCHECK, NOPC, NOPROLOG, NORENT, NORULES, NOS, NOSEQ, NOSEQUENCE, NOSERV, NOSERVICE, NOSO, NOSOURCE,"
          + " NOSQL, NOSQLC, NOSQLCCSID, NOSQLIMS, NOSSR, NOSSRANGE, NOSTGOPT, NOSTDTRUNC, NOSUPP, NOSUPPRESS, "
          + "NOTERM, NOTERMINAL, NOTEST, NOTHREAD, NOVBREF, NOWD, NOWORD, NOX, NOXREF, NOZC, NOZONECHECK, NOZWB, "
          + "NS, NSEQ, NSYMBOL, NUM, NUMBER, NUMCHECK, NUMPROC, OBJ, OBJECT, OFF, OFFSET, OP, OPMARGINS, OPSEQUENCE,"
          + " OPT, OPTFILE, OPTIMIZE, OUT, OUTDD, PARMCHECK, PC, PGMN, PGMNAME, PROLOG, QUA, QUALIFY, QUOTE, Q, RENT,"
          + " RES, RMODE, RULES, SEQ, SEQUENCE, SERV, SERVICE, SIZE, SO, SOURCE, SPACE, SQL, SQLC, SQLCCSID, SQLIMS, "
          + "SSR, SSRANGE, STGOPT, SUPP, SUPPRESS, S, SZ, TERM, TERMINAL, TEST, THREAD, TRUNC, TUNE, VBREF, VLR, VS, "
          + "VSAMOPENFS, WD, WORD, XMLPARSE, XOPTS, XP, XREF, X, YEARWINDOW, YW, ZC, ZD, ZONECHECK, ZONEDATA, ZWB, "
          + "', ','}";

  @Test
  void testProcessWithoutNumbersWithTypo() {
    UseCaseEngine.runTest(
        PROCESS_WITHOUT_NUMBER_TYPO + FOLLOWING_TEXT,
        ImmutableList.of(),
        ImmutableMap.of(
            "1",
            new Diagnostic(
                new Range(),
                "Syntax error on 'DATA' expected {CBL, ID, IDENTIFICATION, PROCESS}",
                DiagnosticSeverity.Error,
                ErrorSource.PARSING.getText())));
  }

  @Test
  void testProcessWithNumbersWithTypo() {
    UseCaseEngine.runTest(
        PROCESS_WITH_NUMBER_TYPO + FOLLOWING_TEXT,
        ImmutableList.of(),
        ImmutableMap.of(
            "1",
            new Diagnostic(
                new Range(),
                "Syntax error on 'PROESS' expected {CBL, ID, IDENTIFICATION, PROCESS}",
                DiagnosticSeverity.Error,
                ErrorSource.PARSING.getText())));
  }

  @Test
  void testProcessWithTypoOnData() {
    UseCaseEngine.runTest(
        PROCESS_WITH_NUMBER_DATA_TYPO + FOLLOWING_TEXT,
        ImmutableList.of(),
        ImmutableMap.of(
            "1",
            new Diagnostic(
                new Range(),
                DATA_ERROR_MESSAGE,
                DiagnosticSeverity.Error,
                ErrorSource.PARSING.getText())));
  }

  @Test
  void testProcessWithoutNumberWithTypoOnData() {
    UseCaseEngine.runTest(
        PROCESS_WITHOUT_NUMBER_DATA_TYPO + FOLLOWING_TEXT,
        ImmutableList.of(),
        ImmutableMap.of(
            "1",
            new Diagnostic(
                new Range(),
                DATA_ERROR_MESSAGE,
                DiagnosticSeverity.Error,
                ErrorSource.PARSING.getText())));
  }
}
