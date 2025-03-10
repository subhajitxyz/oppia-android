package org.oppia.android.scripts.coverage

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.oppia.android.scripts.common.CommandExecutorImpl
import org.oppia.android.scripts.common.ScriptBackgroundCoroutineDispatcher
import org.oppia.android.scripts.proto.BazelTestTarget
import org.oppia.android.scripts.proto.Coverage
import org.oppia.android.scripts.proto.CoverageDetails
import org.oppia.android.scripts.proto.CoverageFailure
import org.oppia.android.scripts.proto.CoverageReport
import org.oppia.android.scripts.proto.CoveredLine
import org.oppia.android.scripts.testing.TestBazelWorkspace
import org.oppia.android.testing.assertThrows
import java.io.File
import java.util.concurrent.TimeUnit

/** Tests for [CoverageRunner]. */
class CoverageRunnerTest {
  @field:[Rule JvmField] val tempFolder = TemporaryFolder()

  private val scriptBgDispatcher by lazy { ScriptBackgroundCoroutineDispatcher() }
  private val longCommandExecutor by lazy { initializeCommandExecutorWithLongProcessWaitTime() }

  private lateinit var coverageRunner: CoverageRunner
  private lateinit var testBazelWorkspace: TestBazelWorkspace
  private lateinit var bazelTestTarget: String

  private lateinit var sourceContent: String
  private lateinit var testContent: String

  @Before
  fun setUp() {
    coverageRunner = CoverageRunner(tempFolder.root, scriptBgDispatcher, longCommandExecutor)
    bazelTestTarget = "//:testTarget"
    testBazelWorkspace = TestBazelWorkspace(tempFolder)

    sourceContent =
      """
      package com.example
      
      class AddNums {
      
          companion object {
              fun sumNumbers(a: Int, b: Int): Any {
                  return if (a == 0 && b == 0) {
                      "Both numbers are zero"
                  } else {
                      a + b
                  }
              }
          }
      }
      """.trimIndent()

    testContent =
      """
      package com.example
      
      import org.junit.Assert.assertEquals
      import org.junit.Test
      
      class AddNumsTest {
      
          @Test
          fun testSumNumbers() {
              assertEquals(AddNums.sumNumbers(0, 1), 1)
              assertEquals(AddNums.sumNumbers(3, 4), 7)         
              assertEquals(AddNums.sumNumbers(0, 0), "Both numbers are zero")
          }
      }
      """.trimIndent()
  }

  @After
  fun tearDown() {
    scriptBgDispatcher.close()
  }

  @Test
  fun testRetrieveCoverageDataForTestTarget_emptyDirectory_throwsException() {
    val exception = assertThrows<IllegalStateException>() {
      coverageRunner.retrieveCoverageDataForTestTarget(bazelTestTarget)
    }

    assertThat(exception).hasMessageThat().contains("not invoked from within a workspace")
  }

  @Test
  fun testRetrieveCoverageDataForTestTarget_invalidTestTarget_throwsException() {
    testBazelWorkspace.initEmptyWorkspace()

    val exception = assertThrows<IllegalStateException>() {
      coverageRunner.retrieveCoverageDataForTestTarget(bazelTestTarget)
    }

    assertThat(exception).hasMessageThat().contains("Expected non-zero exit code")
    assertThat(exception).hasMessageThat().contains("no such package")
  }

  @Test
  fun testRetrieveCoverageDataForTestTarget_withIncorrectPackageStructure_generatesFailureReport() {
    testBazelWorkspace.initEmptyWorkspace()
    testBazelWorkspace.addSourceAndTestFileWithContent(
      filename = "AddNums",
      testFilename = "AddNumsTest",
      sourceContent = sourceContent,
      testContent = testContent,
      sourceSubpackage = "coverage/example",
      testSubpackage = "coverage/example"
    )

    val results = coverageRunner.retrieveCoverageDataForTestTarget(
      "//coverage/example:AddNumsTest"
    )

    val expectedResult = CoverageReport.newBuilder()
      .setFailure(
        CoverageFailure.newBuilder()
          .setBazelTestTarget("//coverage/example:AddNumsTest")
          .setFailureMessage(
            "Coverage retrieval failed for the test target: " +
              "//coverage/example:AddNumsTest"
          )
          .build()
      ).build()

    assertThat(results).hasSize(1)
    assertThat(results[0]).isEqualTo(expectedResult)
  }

  @Test
  fun testRetrieveCoverageDataForTestTarget_withNoDepsToSourceFile_generatesFailureReport() {
    testBazelWorkspace.initEmptyWorkspace()
    testBazelWorkspace.addSourceAndTestFileWithContent(
      filename = "AddNums",
      testFilename = "AddNumsTest",
      sourceContent = sourceContent,
      testContent = testContent,
      sourceSubpackage = "coverage/main/java/com/example",
      testSubpackage = "coverage/test/java/com/example"
    )

    val subTestFile = tempFolder.newFile("coverage/test/java/com/example/SubNumsTest.kt")
    subTestFile.writeText(
      """
      package com.example
      
      import org.junit.Assert.assertEquals
      import org.junit.Test
      import com.example.AddNums
      
      class SubNumsTest {
      
          @Test
          fun testSubNumbers() {
              assertEquals(AddNums.sumNumbers(0, 1), 1)
              assertEquals(AddNums.sumNumbers(3, 4), 7)         
              assertEquals(AddNums.sumNumbers(0, 0), "Both numbers are zero")
          }
      }
      """.trimIndent()
    )

    val testBuildFile = File(tempFolder.root, "coverage/test/java/com/example/BUILD.bazel")
    testBuildFile.appendText(
      """
      kt_jvm_test(
          name = "SubNumsTest",
          srcs = ["SubNumsTest.kt"],
          deps = [
            "//coverage/main/java/com/example:addnums",
            "@maven//:junit_junit",
          ],
          visibility = ["//visibility:public"],
          test_class = "com.example.SubNumsTest",
      )
      """.trimIndent()
    )

    val results = coverageRunner.retrieveCoverageDataForTestTarget(
      "//coverage/test/java/com/example:SubNumsTest"
    )

    val expectedResult = CoverageReport.newBuilder()
      .setFailure(
        CoverageFailure.newBuilder()
          .setBazelTestTarget("//coverage/test/java/com/example:SubNumsTest")
          .setFailureMessage("Source File: SubNums.kt not found in the coverage data")
          .build()
      ).build()

    assertThat(results).hasSize(1)
    assertThat(results[0]).isEqualTo(expectedResult)
  }

  @Test
  fun testRetrieveCoverageDataForTestTarget_validSampleTestTarget_returnsCoverageData() {
    testBazelWorkspace.initEmptyWorkspace()
    testBazelWorkspace.addSourceAndTestFileWithContent(
      filename = "AddNums",
      testFilename = "AddNumsTest",
      sourceContent = sourceContent,
      testContent = testContent,
      sourceSubpackage = "coverage/main/java/com/example",
      testSubpackage = "coverage/test/java/com/example"
    )

    val results = coverageRunner.retrieveCoverageDataForTestTarget(
      "//coverage/test/java/com/example:AddNumsTest"
    )

    val expectedResult = CoverageReport.newBuilder()
      .setDetails(
        CoverageDetails.newBuilder()
          .addBazelTestTargets(
            BazelTestTarget.newBuilder()
              .setTestTargetName("//coverage/test/java/com/example:AddNumsTest")
          )
          .setFilePath("coverage/main/java/com/example/AddNums.kt")
          .setFileSha1Hash("cdb04b7e8a1c6a7adaf5807244b1a524b4f4bb44")
          .addCoveredLine(
            CoveredLine.newBuilder()
              .setLineNumber(3)
              .setCoverage(Coverage.NONE)
              .build()
          )
          .addCoveredLine(
            CoveredLine.newBuilder()
              .setLineNumber(7)
              .setCoverage(Coverage.FULL)
              .build()
          )
          .addCoveredLine(
            CoveredLine.newBuilder()
              .setLineNumber(8)
              .setCoverage(Coverage.FULL)
              .build()
          )
          .addCoveredLine(
            CoveredLine.newBuilder()
              .setLineNumber(10)
              .setCoverage(Coverage.FULL)
              .build()
          )
          .setLinesFound(4)
          .setLinesHit(3)
          .build()
      ).build()

    assertThat(results).hasSize(1)
    assertThat(results[0]).isEqualTo(expectedResult)
  }

  private fun initializeCommandExecutorWithLongProcessWaitTime(): CommandExecutorImpl {
    return CommandExecutorImpl(
      scriptBgDispatcher, processTimeout = 5, processTimeoutUnit = TimeUnit.MINUTES
    )
  }
}
