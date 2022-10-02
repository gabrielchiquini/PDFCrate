package org.example;

import pdfcrate.components.FlexibleText;
import pdfcrate.components.Lines;
import pdfcrate.components.Paragraph;
import pdfcrate.components.SimpleText;
import pdfcrate.document.Document;
import pdfcrate.document.TextStyle;
import pdfcrate.util.Point;
import pdfcrate.util.Size;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Main {

  public static final String BASE =
      String.join(
          "\n",
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
          "Aliquam venenatis orci vel aliquet auctor.",
          "Fusce vestibulum augue et finibus blandit.",
          "Pellentesque in libero a nunc porttitor mollis.",
          "Nunc non velit vitae ligula efficitur rhoncus.",
          "Aliquam ac neque quis turpis interdum vulputate non non turpis.",
          "Aliquam nec purus at massa posuere commodo a eu turpis.",
          "Nam vitae nisi non purus maximus hendrerit.",
          "Nulla mattis mi sed mi dignissim, vel commodo eros sagittis.",
          "Aenean et diam tincidunt lectus hendrerit aliquet efficitur at odio.",
          "Proin a dui eu tortor accumsan egestas.",
          "Nulla in nisi volutpat, finibus mauris nec, bibendum augue.",
          "In viverra nisi nec placerat varius.",
          "Mauris fermentum metus a leo malesuada, non interdum urna euismod.",
          "Morbi vehicula arcu et orci blandit placerat.",
          "Fusce sed nulla sed tellus posuere malesuada eu ac nisi.",
          "Fusce in dui non massa ullamcorper eleifend.",
          "Nunc vitae odio finibus, viverra augue et, tristique turpis.",
          "Etiam dapibus lacus quis dapibus ultrices.",
          "Pellentesque mattis diam ac dui lacinia, vel commodo erat sollicitudin.",
          "Pellentesque id mauris et nibh convallis auctor.",
          "Fusce suscipit magna a mi vestibulum porttitor.",
          "Nulla a nunc varius diam condimentum vestibulum.",
          "In a lectus semper, ultricies orci ac, gravida metus.",
          "Vestibulum nec purus rhoncus, rhoncus tortor vitae, placerat urna.",
          "Nunc ut turpis eleifend, lacinia mauris vel, ultricies est.",
          "Morbi venenatis nunc at nisl aliquet, ac varius lacus dignissim.",
          "Etiam consectetur ipsum quis tellus semper dictum.",
          "Sed et nisi tristique, aliquet turpis at, suscipit eros.",
          "Pellentesque sit amet leo at magna bibendum hendrerit.",
          "Fusce congue elit nec malesuada sagittis.",
          "Curabitur eleifend mauris et sapien finibus, ac dignissim massa aliquet.",
          "Morbi ac dolor ut ante dictum consectetur.",
          "Nam tempor ipsum eget porta dignissim.",
          "Aenean pharetra ante eu pellentesque convallis.",
          "Phasellus quis diam quis orci tempor dignissim eget vel erat."
);

  public static void main(String[] args) throws FileNotFoundException {
    final var document = new Document();
    document.setMargin(new Size(30, 30));
    document.add(Lines.builder().start(new Point(0, 0)).end(new Point(100, 100)).build());
    document.add(new Paragraph(BASE));
    document.add(new SimpleText(BASE));
    var textStyle = TextStyle.builder().fontSize(1000f).build();
    document.add(new FlexibleText("TAMANHO 1000", textStyle));
    document.add(Lines.builder().start(new Point(0, 0)).end(new Point(100, 100)).build());
    textStyle = TextStyle.builder().fontSize(32f).build();
    document.add(new FlexibleText("TAMANHO 32", textStyle));
    document.add(new SimpleText(BASE));
    document.render(new FileOutputStream("test-output/test.pdf"));
  }
}
