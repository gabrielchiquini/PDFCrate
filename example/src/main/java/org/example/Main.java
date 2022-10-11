package org.example;

import pdfcrate.components.Image;
import pdfcrate.components.*;
import pdfcrate.document.Document;
import pdfcrate.document.LineStyle;
import pdfcrate.document.TextStyle;
import pdfcrate.render.Alignment;
import pdfcrate.util.Edges;
import pdfcrate.util.Point;
import pdfcrate.util.Size;
import pdfcrate.util.SpacingStyle;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class Main {

  public static final String BASE =
      """
          Lorem ipsum dolor sit amet, consectetur adipiscing elit.
          Aliquam venenatis orci vel aliquet auctor.
          Fusce vestibulum augue et finibus blandit.
          Pellentesque in libero a nunc porttitor mollis.
          Nunc non velit vitae ligula efficitur rhoncus.
          Aliquam ac neque quis turpis interdum vulputate non non turpis.
          Aliquam nec purus at massa posuere commodo a eu turpis.
          Nam vitae nisi non purus maximus hendrerit.
          Nulla mattis mi sed mi dignissim, vel commodo eros sagittis.
          Aenean et diam tincidunt lectus hendrerit aliquet efficitur at odio.
          Proin a dui eu tortor accumsan egestas.
          Nulla in nisi volutpat, finibus mauris nec, bibendum augue.
          In viverra nisi nec placerat varius.
          Mauris fermentum metus a leo malesuada, non interdum urna euismod.
          Morbi vehicula arcu et orci blandit placerat.
          Fusce sed nulla sed tellus posuere malesuada eu ac nisi.
          Fusce in dui non massa ullamcorper eleifend.
          Nunc vitae odio finibus, viverra augue et, tristique turpis.
          Etiam dapibus lacus quis dapibus ultrices.
          Pellentesque mattis diam ac dui lacinia, vel commodo erat sollicitudin.
          Pellentesque id mauris et nibh convallis auctor.
          Fusce suscipit magna a mi vestibulum porttitor.
          Nulla a nunc varius diam condimentum vestibulum.
          In a lectus semper, ultricies orci ac, gravida metus.
          Vestibulum nec purus rhoncus, rhoncus tortor vitae, placerat urna.
          Nunc ut turpis eleifend, lacinia mauris vel, ultricies est.
          Morbi venenatis nunc at nisl aliquet, ac varius lacus dignissim.
          Etiam consectetur ipsum quis tellus semper dictum.
          Sed et nisi tristique, aliquet turpis at, suscipit eros.
          Pellentesque sit amet leo at magna bibendum hendrerit.
          Fusce congue elit nec malesuada sagittis.
          Curabitur eleifend mauris et sapien finibus, ac dignissim massa aliquet.
          Morbi ac dolor ut ante dictum consectetur.
          Nam tempor ipsum eget porta dignissim.
          Aenean pharetra ante eu pellentesque convallis.
          Phasellus quis diam quis orci tempor dignissim eget vel erat.""";

  public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
    final var document = new Document();
    final var path =
        List.of(
            new Point(0, 0),
            new Point(40, 40),
            new Point(150, 40),
            new Point(120, 20),
            new Point(120, 100),
            new Point(0, 100));
    document.setMargin(Edges.all(30));
    document.add(
        Lines.builder()
            .points(path)
            .lineStyle(
                LineStyle.builder()
                    .width(5f)
                    .capStyle(1)
                    .dashPattern(new float[] {10, 6})
                    .dashPhase(0f)
                    .color(Color.darkGray)
                    .build())
            .build());
    document.add(new Paragraph(BASE));
    document.add(new Padding(new SimpleText(BASE), Edges.all(60)));
    var textStyle = TextStyle.builder().fontSize(1000f).build();
    document.add(new FlexibleText("TAMANHO 1000", Alignment.LEFT, textStyle));
    document.add(Lines.builder().points(path).build());
    document.add(
        new ColumnLayout(
            List.of(new FlexibleText("OLAOLAOLA", Alignment.LEFT, textStyle), new Paragraph(BASE)),
            List.of(
                new ColumnConstraints(SpacingStyle.ABSOLUTE, 100, Edges.symmetric(20, 30)),
                new ColumnConstraints(SpacingStyle.PROPORTIONAL, 1, Edges.symmetric(20, 10)))));
    textStyle = TextStyle.builder().fontSize(32f).build();
    document.add(new FlexibleText("TAMANHO 32", Alignment.CENTER, textStyle));
    document.add(new SimpleText(BASE));
    final var cellPadding = new Edges(0f, 10f, 10f, 0);
    document.add(
        new Table(
            new SizedComponent[][] {
              {
                new FlexibleText("TESTING TESTING:", Alignment.LEFT),
                new FlexibleText("TESTING TESTED", Alignment.LEFT),
                new SimpleText("WHAT ABOUT\nWRAPPING LINES\nINSIDE TABLES")
              },
              {
                new FlexibleText("TESTING TESTING 341:", Alignment.LEFT),
                new FlexibleText("TESTING TESTED", Alignment.LEFT),
                new FlexibleText("TEST TESTING, TESTED", Alignment.LEFT)
              },
              {
                Lines.builder().points(path).build(),
                new SimpleText("THAT TEXT IS TOO BIG TO FIT IN ONLY 200 pixels"),
                new FlexibleText("TEST TESTING, TESTED", Alignment.LEFT)
              },
              {
                Lines.builder().points(path).build(),
                new SimpleText("THAT TEXT IS TOO BIG TO FIT IN ONLY 200 pixels"),
                new FlexibleText("TEST TESTING, TESTED", Alignment.LEFT)
              }
            },
            new TableColumn[] {
              new TableColumn(new ColumnSizing(SpacingStyle.SHRINK, 0f), cellPadding),
              new TableColumn(new ColumnSizing(SpacingStyle.ABSOLUTE, 200f), cellPadding),
              new TableColumn(new ColumnSizing(SpacingStyle.PROPORTIONAL, 1f), cellPadding)
            }));

    document.add(Image.fromFile(imageFile(), new Size(400, 100f)));
    document.add(ScalingImage.fromWidth(imageFile(), 200f));
    document.render(new FileOutputStream("test-output/test.pdf"));
  }

  private static File imageFile() throws URISyntaxException {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final var url = classloader.getResource("test.png");
    return new File(Objects.requireNonNull(url).toURI());
  }
}
