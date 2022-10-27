package pdfcrate.examples;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class GenerateAll {
  public static void main(String[] args) throws IOException, URISyntaxException {
    final var outputDir = new File("test-output");
    if (!outputDir.exists()) {
      //noinspection ResultOfMethodCallIgnored
      outputDir.mkdir();
    }
    CustomTextStyle.main(null);
    Drawing.main(null);
    ImageSizing.main(null);
    InlineComponents.main(null);
    MultiColumnLayout.main(null);
    MultipleComponentsCentered.main(null);
    SimpleTable.main(null);
    TextComponentsShowcase.main(null);
  }
}
