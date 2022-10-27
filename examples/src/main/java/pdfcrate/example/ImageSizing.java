package pdfcrate.example;

import pdfcrate.components.Image;
import pdfcrate.components.Paragraph;
import pdfcrate.components.Text;
import pdfcrate.document.Document;
import pdfcrate.util.Edges;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;

import static pdfcrate.example.Common.imageFile;

/** Shows how the image can be scaled in the document */
public class ImageSizing {
  public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
    new Document()
        .margin(Edges.all(20))
        // Scaling based in the width
        .add(new Text("Scaling width:"))
        .add(Image.fromWidth(imageFile(), 1200))
        // Scaling based in the height
        .add(new Text("Scaling height:"))
        .add(Image.fromHeight(imageFile(), 100))
        // Stretching image to make it a square
        .add(new Paragraph("Streching:"))
        .add(Image.scale(imageFile(), 100, 100))
        .render(new FileOutputStream("test-output/image-sizing.pdf"));
  }
}
