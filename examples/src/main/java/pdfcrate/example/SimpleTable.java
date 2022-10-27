package pdfcrate.example;

import pdfcrate.components.*;
import pdfcrate.document.Document;
import pdfcrate.util.Edges;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class SimpleTable {
  public static void main(String[] args) throws FileNotFoundException {
    final var document = new Document();
    document
        .margin(Edges.all(20))
        .textStyle(document.textStyle().toBuilder().fontSize(10f).build())
        .add(
            new Table(
                new SizedComponent[][] {
                  {
                    new Text("SHRINK COLUMN"),
                    new Text("ABSOLUTE COLUMN"),
                    new Text("PROPORTIONAL COLUMN")
                  },
                  {
                    new Spacer(0, 0),
                    new Text("TALLEST ROW SET HEIGHT"),
                    new Text(List.of("SUPPORTS", "MULTILINE", "TEXT"))
                  },
                  {
                    new Text("LARGEST COLUMN SET WIDTH"),
                    new Text("200 PT WIDTH"),
                    new Text("WILL USE THE REMAINING SIZE"),
                  },
                },
                new TableColumn[] {
                  TableColumn.shrink(Edges.symmetric(4, 4)),
                  TableColumn.absolute(200f, Edges.symmetric(4, 4)),
                  TableColumn.proportional(1f, Edges.symmetric(4, 4))
                }))
        .render(new FileOutputStream("test-output/simple-table.pdf"));
  }
}
