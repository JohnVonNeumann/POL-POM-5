package com.playonlinux.tools.archive;

import com.google.common.io.Files;
import com.playonlinux.tools.files.FileAnalyser;
import com.playonlinux.tools.files.FileUtilities;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ExtractorTest {
    final URL inputUrl = ExtractorTest.class.getResource(".");
    private Tar tar = new Tar(new FileUtilities());
    private Zip zip = new Zip();

    private Extractor extractor = new Extractor(new FileAnalyser(), tar, zip);

    @Test
    public void testUncompressTarFile() throws IOException {
        testUncompress("test1.tar");
    }

    @Test
    public void testUncompressTarGzFile() throws IOException {
        testUncompress("test2.tar.gz");
    }

    @Test
    public void testUncompressTarBz2File() throws IOException {
        testUncompress("test3.tar.bz2");
    }

    @Test
    public void testUncompressZipFile() throws IOException {
        testUncompress("test4.zip");
    }

    @Test
    public void testUncompress_withSymbolicLinks() throws IOException {
        final File inputFile = new File(inputUrl.getPath(), "tarLink.tar.gz");
        final File temporaryDirectory = Files.createTempDir();

        temporaryDirectory.deleteOnExit();

        final List<File> extractedFiles = extractor.uncompress(inputFile, temporaryDirectory, e -> {});

        final File file1 = new File(temporaryDirectory, "file1.txt");
        final File file2 = new File(temporaryDirectory, "file1_link.txt");

        assertTrue(file1.exists());
        assertTrue(file2.exists());

        assertEquals("file1content", new String(FileUtils.readFileToByteArray(file1)));
        assertEquals("file1content", new String(FileUtils.readFileToByteArray(file2)));

        assertTrue(java.nio.file.Files.isSymbolicLink(Paths.get(file2.getPath())));
    }

    private void testUncompress(String fileName) throws IOException {
        final File inputFile = new File(inputUrl.getPath(), fileName);
        final File temporaryDirectory = Files.createTempDir();

        temporaryDirectory.deleteOnExit();

        final List<File> extractedFiles = extractor.uncompress(inputFile, temporaryDirectory, e -> {});

        assertTrue(new File(temporaryDirectory, "directory1").isDirectory());
        final File file1 = new File(temporaryDirectory, "file1.txt");
        final File file2 = new File(temporaryDirectory, "file2.txt");
        final File file0 = new File(new File(temporaryDirectory, "directory1"), "file0.txt");

        assertTrue(file1.exists());
        assertTrue(file2.exists());
        assertTrue(file0.exists());

        assertEquals("file1content", new String(FileUtils.readFileToByteArray(file1)));
        assertEquals("file2content", new String(FileUtils.readFileToByteArray(file2)));
        assertEquals("file0content", new String(FileUtils.readFileToByteArray(file0)));

        System.out.println(extractedFiles);;
        assertEquals(4, extractedFiles.size());
    }

    @Test
    public void testGunzip() throws IOException {
        final File inputFile = new File(inputUrl.getPath(), "pol.txt.gz");
        final File outputFile = File.createTempFile("output", "txt");

        tar.gunzip(inputFile, outputFile);

        assertEquals("PlayOnLinux", new String(FileUtils.readFileToByteArray(outputFile)));
    }

    @Test
    public void testBunzip2() throws IOException {
        final File inputFile = new File(inputUrl.getPath(), "pol.txt.bz2");
        final File outputFile = File.createTempFile("output", "txt");

        tar.bunzip2(inputFile, outputFile);

        assertEquals("PlayOnLinux", new String(FileUtils.readFileToByteArray(outputFile)));
    }

    @Test(expected = ArchiveException.class)
    public void testBunzip2_extractGzip() throws IOException {
        final File inputFile = new File(inputUrl.getPath(), "pol.txt.gz");
        final File outputFile = File.createTempFile("output", "txt");
        outputFile.deleteOnExit();
        tar.bunzip2(inputFile, outputFile);
    }

    @Test(expected = ArchiveException.class)
    public void tesGunzip_extractBzip2() throws IOException {
        final File inputFile = new File(inputUrl.getPath(), "pol.txt.bz2");
        final File outputFile = File.createTempFile("output", "txt");
        outputFile.deleteOnExit();
        tar.gunzip(inputFile, outputFile);

    }
}