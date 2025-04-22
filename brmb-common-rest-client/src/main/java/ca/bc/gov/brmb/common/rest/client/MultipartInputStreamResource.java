package ca.bc.gov.brmb.common.rest.client;

import java.io.InputStream;
import java.util.Objects;

import org.springframework.core.io.InputStreamResource;

public class MultipartInputStreamResource extends InputStreamResource {

  private final String filename;

  public MultipartInputStreamResource(InputStream inputStream, String filename) {
    super(inputStream);
    this.filename = filename;
  }

  @Override
  public String getFilename() {
    return this.filename;
  }

  @Override
  public long contentLength() {
    return -1;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    MultipartInputStreamResource that = (MultipartInputStreamResource) o;
    return Objects.equals(filename, that.filename);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), filename);
  }
}