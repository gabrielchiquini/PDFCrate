package pdfcrate.example;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

public class Common {
  public static final String LOREM_IPSUM_PARAGRAPH =
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id mi sit amet diam scelerisque faucibus sit amet quis quam. Cras dictum massa vestibulum magna dapibus egestas. Cras hendrerit condimentum feugiat. Phasellus quis purus vitae leo varius sollicitudin eu ut est. Aenean euismod ante velit, nec iaculis neque hendrerit non. Aliquam lectus neque, commodo non congue nec, cursus eget diam. Integer quam nisl, hendrerit in consequat dapibus, sollicitudin a dolor. Morbi eu sapien sed lacus tristique laoreet ac eu elit";
  public static final String LOREM_IPSUM_MULTIPLE_PARAGRAPHS =
      """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed id mi sit amet diam scelerisque faucibus sit amet quis quam. Cras dictum massa vestibulum magna dapibus egestas. Cras hendrerit condimentum feugiat. Phasellus quis purus vitae leo varius sollicitudin eu ut est. Aenean euismod ante velit, nec iaculis neque hendrerit non. Aliquam lectus neque, commodo non congue nec, cursus eget diam. Integer quam nisl, hendrerit in consequat dapibus, sollicitudin a dolor. Morbi eu sapien sed lacus tristique laoreet ac eu elit.
        Praesent blandit nunc efficitur dolor consectetur aliquam. Nulla facilisi. Phasellus eros dolor, faucibus sit amet ligula non, blandit dictum elit. Nullam laoreet, metus vel dictum elementum, massa enim ornare purus, feugiat dignissim libero diam euismod est. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam tincidunt urna est, id egestas odio suscipit ut. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Praesent augue velit, tempus vel lorem eget, malesuada porttitor neque. Duis et mi turpis. Fusce vel scelerisque lacus, et ultrices neque. Integer feugiat varius ex, non ullamcorper nulla auctor et. Pellentesque orci nisl, eleifend id vulputate ac, iaculis non magna. Vivamus commodo elit purus, nec aliquam nulla mollis ut. Donec in lacus ac erat pretium iaculis sit amet ac ligula.
        Mauris id sodales nulla. Sed ut aliquet dolor. Nulla congue quam vel tellus venenatis lobortis. Morbi eu tempor sapien. Sed consectetur dignissim tortor, non lacinia est. Donec interdum id nulla pharetra maximus. Nullam mollis nec mi ac rhoncus. Mauris enim justo, vehicula vitae dui eu, dapibus laoreet justo. Proin elementum tellus vitae aliquam egestas. Proin id libero sit amet augue malesuada egestas id sed turpis. Praesent placerat tincidunt ante, a iaculis risus tristique non.
        Fusce id ex consectetur lacus pulvinar tempor a eu velit. Proin lectus purus, placerat at fermentum in, fringilla nec diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Sed augue mauris, posuere eu enim lacinia, tristique accumsan dolor. Vestibulum eget scelerisque ex. Quisque gravida maximus risus vitae laoreet. Cras et rutrum tellus. Suspendisse id ipsum arcu.
        Aliquam erat volutpat. Integer ullamcorper sapien nec lacus luctus, sit amet luctus ipsum fringilla. Pellentesque leo ligula, tristique nec ultricies ac, porttitor ut lorem. Proin pulvinar ex ut blandit eleifend. Vivamus commodo purus eget ligula interdum, ut tempor risus sagittis. Pellentesque vestibulum varius purus, ac aliquam arcu iaculis quis. Morbi vehicula leo eget lectus pellentesque lacinia. Quisque est leo, eleifend sed turpis quis, faucibus sagittis risus. Phasellus a ornare ex. Vivamus quam dui, gravida eu ante eu, laoreet blandit dolor. Cras suscipit rhoncus pellentesque. Nunc aliquam sapien a risus feugiat porttitor. Integer accumsan dignissim magna in hendrerit. Pellentesque vel dapibus massa.
                  """;

  public static File imageFile() throws URISyntaxException {
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final var url = classloader.getResource("test.png");
    return new File(Objects.requireNonNull(url).toURI());
  }
}
