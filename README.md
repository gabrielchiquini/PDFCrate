# PDFCrate

A component-based library for PDF generation, using PDFBox internally

It has components to render images, display word-wrapping text, display multiple columns and more

## Usage:

Add JitPack to repositories

````
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
````

Add PDFCrate dependency

````
dependencies {
    ...
    implementation 'com.github.gabrielchiquini:PDFCrate:<version>'
}
````

Creating and rendering the document to a file

```
new Document()
        .margin(Edges.all(20))
        .add(new Text("Hello world!"))
        .render(new FileOutputStream("hello-world.pdf"))
```

Details about components usage in [javadoc](https://javadoc.jitpack.io/com/github/gabrielchiquini/PDFCrate/0.2.1/javadoc)

More examples can be found in [examples](https://github.com/gabrielchiquini/PDFCrate/tree/main/examples)
