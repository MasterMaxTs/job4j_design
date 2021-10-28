package ru.job4j.search;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FilesSearcherTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void whenSearchByFileNameWithoutExtension() throws IOException {
        File root = temporaryFolder.getRoot();
        File file1 = temporaryFolder.newFile("log.bin");
        File file2 = temporaryFolder.newFile("log.tmp");
        File file3 = temporaryFolder.newFile("log.txt");
        File target = temporaryFolder.newFile("target.txt");
        ArgsName argsName = ArgsName.of(
                new String[]{"-d=" + root, "-n=log", "-t=name", "-o=" + target}
        );
        Path directory = Path.of(argsName.get("d"));
        String condition = argsName.get("n");
        String typeSearch = argsName.get("t");
        Path out = Path.of(argsName.get("o"));
        FilesSearcher searcher = new FilesSearcher();
        searcher.searchFiles(directory, condition, typeSearch, out);
        try (Scanner sc = new Scanner(out)) {
            List<String> rsl = List.of(sc.nextLine(), sc.nextLine(), sc.nextLine());
            List<String> expected = List.of(
                    file1.getAbsolutePath(),
                    file2.getAbsolutePath(),
                    file3.getAbsolutePath()
            );
            assertThat(rsl, is(expected));
        }
    }

        @Test
        public void whenSearchByFullFileName() throws IOException {
            File root = temporaryFolder.getRoot();
            File file1 = temporaryFolder.newFile("log.bin");
            File file2 = temporaryFolder.newFile("log.tmp");
            File file3 = temporaryFolder.newFile("log.txt");
            File target = temporaryFolder.newFile("target.txt");
            ArgsName argsName = ArgsName.of(
                new String[]{"-d=" + root, "-n=log.tmp", "-t=name", "-o=" + target}
            );
            Path directory = Path.of(argsName.get("d"));
            String condition = argsName.get("n");
            String typeSearch = argsName.get("t");
            Path out = Path.of(argsName.get("o"));
            FilesSearcher searcher = new FilesSearcher();
            searcher.searchFiles(directory, condition, typeSearch, out);
            try (Scanner sc = new Scanner(out)) {
                List<String> files = new ArrayList<>();
                while (sc.hasNextLine()) {
                    files.add(sc.nextLine());
                }
                assertThat(files.get(0), is(file2.getAbsolutePath()));
                assertThat(files.size(), is(1));
            }
        }

        @Test
    public void whenSearchUsageRegex() throws IOException {
            File root = temporaryFolder.getRoot();
            File file1 = temporaryFolder.newFile("log.bin");
            File file2 = temporaryFolder.newFile("log.tmp");
            File file3 = temporaryFolder.newFile("log.py");
            File target = temporaryFolder.newFile("target.txt");
            ArgsName argsName = ArgsName.of(
                    new String[]{"-d=" + root, "-n=.+\\.\\D{2}", "-t=regex",
                            "-o=" + target}
            );
            Path directory = Path.of(argsName.get("d"));
            String condition = argsName.get("n");
            String typeSearch = argsName.get("t");
            Path out = Path.of(argsName.get("o"));
            FilesSearcher searcher = new FilesSearcher();
            searcher.searchFiles(directory, condition, typeSearch, out);
            try (Scanner sc = new Scanner(out)) {
                List<String> files = new ArrayList<>();
                while (sc.hasNextLine()) {
                    files.add(sc.nextLine());
                }
                assertThat(files.get(0), is(file3.getAbsolutePath()));
                assertThat(files.size(), is(1));
            }
        }

    @Test
    public void whenSearchUsageMask() throws IOException {
        File root = temporaryFolder.getRoot();
        File file1 = temporaryFolder.newFile("log.txt");
        File file2 = temporaryFolder.newFile("log.tmp");
        File file3 = temporaryFolder.newFile("words.tmp");
        File target = temporaryFolder.newFile("target.txt");
        ArgsName argsName = ArgsName.of(
                new String[]{"-d=" + root, "-n=**.tmp", "-t=mask",
                        "-o=" + target}
        );
        Path directory = Path.of(argsName.get("d"));
        String condition = argsName.get("n");
        String typeSearch = argsName.get("t");
        Path out = Path.of(argsName.get("o"));
        FilesSearcher searcher = new FilesSearcher();
        searcher.searchFiles(directory, condition, typeSearch, out);
        try (Scanner sc = new Scanner(out)) {
            List<String> files = new ArrayList<>();
            while (sc.hasNextLine()) {
                files.add(sc.nextLine());
            }
            List<String> expected = List.of(
                    file2.getAbsolutePath(),
                    file3.getAbsolutePath()
            );
            assertThat(files, is(expected));
            assertThat(files.size(), is(2));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenInvalidEntryArgumentsThanException() {
        String[] args = new String[]
                {
                "-d=c:/",
                "-n=**.tmp",
                "-t=mask"
                };
        FilesSearcher searcher = new FilesSearcher();
        searcher.validate(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenInvalidValueInTypeSearchParameterThanException() {
        String[] args = new String[]
                {
                "-d=c:/",
                "-n=**.tmp",
                "-t=fileName",
                "-o=c:/target/files.txt"
                };
        FilesSearcher searcher = new FilesSearcher();
        searcher.validate(args);
    }
}