/*
   Copyright [2024] [Lukas Mansour]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package io.github.lukasmansour.patch;

import com.github.difflib.patch.PatchFailedException;
import com.github.difflib.unifieddiff.UnifiedDiff;
import com.github.difflib.unifieddiff.UnifiedDiffFile;
import com.github.difflib.unifieddiff.UnifiedDiffReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Applies unified diff patches.
 */
@Mojo(name = "apply", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class PatchMojo extends AbstractMojo {

    @Parameter(property = "patch.targetDirectory", defaultValue = "${project.basedir}")
    private File targetDirectory;

    @Parameter(property = "patch.patchDirectory", defaultValue = "src/main/patches")
    private File patchDirectory;

    public void execute() throws MojoExecutionException {
        getLog().info(targetDirectory.toString());
        getLog().info("Applying patches...");

        File[] patchFiles = patchDirectory.listFiles(
            (dir, name) -> name.endsWith(".diff") || name.endsWith(".patch"));
        if (patchFiles == null || !patchDirectory.isDirectory()) {
            throw new MojoExecutionException("'patchDirectory' must be a directory.");
        }
        if (!targetDirectory.isDirectory()) {
            throw new MojoExecutionException("'targetDirectory' must be a directory.");
        }

        try {
            for (File patchFile : patchFiles) {
                String patchFileName = patchFile.getName();
                getLog().info(String.format("Applying patch '%s'...", patchFile.getName()));

                UnifiedDiff diff = UnifiedDiffReader.parseUnifiedDiff(
                    new FileInputStream(patchFile));

                for (UnifiedDiffFile file : diff.getFiles()) {
                    Path targetFile = targetDirectory.toPath().resolve(file.getToFile());
                    String targetFileName = targetFile.getFileName().toString();

                    try {
                        List<String> results = file.getPatch().applyTo(
                            Files.readAllLines(targetFile)
                        );

                        Files.write(targetFile, results);
                        getLog().info(
                            String.format("Applied diff to '%s' successfully.", targetFileName));
                    } catch (PatchFailedException pfe1) {
                        getLog().warn(String.format(
                            "Failed to apply patch file '%s' to file '%s'. (It may already have been applied!)",
                            patchFileName, targetFileName));
                        break;
                    }
                    getLog().info(String.format("Finished applying diff to '%s'.", targetFileName));
                }
                getLog().info(String.format("Finished applying patch '%s'.", patchFileName));
            }

        } catch (NoSuchFileException nsfe) {
            throw new MojoExecutionException(
                String.format("Could not find the file '%s' for patching. ", nsfe.getFile()));
        } catch (IOException e) {
            throw new MojoExecutionException("Something went wrong with IO Operations.", e);
        }
        getLog().info("Finished applying patches.");
    }
}